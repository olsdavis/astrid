package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class represents the sky at a certain moment in time, containing
 * celestial objects projected on a plan with stereographic projection.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 04/04/2020
 */
public class ObservedSky {

    /**
     * This small class allows to wrap up all the objects of the sky
     * with their coordinates and their CelestialObject representation.
     */
    public static final class CelestialPair {
        private final CartesianCoordinates position;
        private final CelestialObject object;

        /**
         * @param position the position of the object
         * @param object   the CelestialObject data
         */
        private CelestialPair(CartesianCoordinates position, CelestialObject object) {
            this.position = position;
            this.object = object;
        }

        /**
         * @return the position, in {@link CartesianCoordinates}, of the current pair.
         */
        public CartesianCoordinates position() {
            return position;
        }

        /**
         * @return the {@link CelestialObject} that is wrapped by this pair.
         */
        public CelestialObject object() {
            return object;
        }
    }

    private final List<CelestialPair> allObjects;

    private final StereographicProjection projection;
    private final StarCatalogue catalogue;

    private final Sun sun;
    private final CartesianCoordinates sunProjection;

    private final Moon moon;
    private final CartesianCoordinates moonProjection;

    private final List<Planet> planets;
    private final double[] planetPositions;

    private final double[] starPositions;

    /**
     * Initializes the ObservedSky, projects all CelestialObjects, namely the Moon, the Sun,
     * planets and stars, and also distributes them across chunks that make the method
     * {@link #objectClosestTo(CartesianCoordinates, double)} more efficient than standard linear
     * search.
     *
     * @param moment     the moment at which the sky is observed
     * @param observer   the position from which the sky is observed
     * @param projection the projection to use
     * @param catalogue  the catalogue of stars
     */
    public ObservedSky(ZonedDateTime moment, GeographicCoordinates observer, StereographicProjection projection, StarCatalogue catalogue) {
        this.catalogue = catalogue;
        this.projection = projection;
        // -1 to exclude Earth, and +2 for the sun and the moon
        allObjects = new ArrayList<>(catalogue.stars().size() + (PlanetModel.ALL.size() - 1) + 2);
        // the conversion used for the current situation
        final EclipticToEquatorialConversion eclipticToEq = new EclipticToEquatorialConversion(moment);
        final EquatorialToHorizontalConversion eqToHorizontal = new EquatorialToHorizontalConversion(moment, observer);
        // days since J2010
        final double d = Epoch.J2010.daysUntil(moment);
        final Function<EquatorialCoordinates, CartesianCoordinates> fullProj = projection.compose(eqToHorizontal);
        // set up the Sun
        sun = SunModel.SUN.at(d, eclipticToEq);
        sunProjection = fullProj.apply(sun.equatorialPos());
        allObjects.add(new CelestialPair(sunProjection, sun));
        // set up the Moon
        moon = MoonModel.MOON.at(d, eclipticToEq);
        moonProjection = fullProj.apply(moon.equatorialPos());
        allObjects.add(new CelestialPair(moonProjection, moon));
        // set up the planets
        planets = PlanetModel.ALL.stream()
                .filter(p -> p != PlanetModel.EARTH) // filtering the Earth
                .map(p -> p.at(d, eclipticToEq))
                .collect(Collectors.toUnmodifiableList());
        planetPositions = new double[2 * planets.size()];
        setupCoordinates(planets, planetPositions, fullProj);

        starPositions = new double[2 * catalogue.stars().size()];
        setupCoordinates(catalogue.stars(), starPositions, fullProj);
    }

    /**
     * Sets up the coordinates of the provided objects in the provided array.
     *
     * @param objects   the objects
     * @param positions the positions to fill in
     * @param proj      the projection to use
     */
    private void setupCoordinates(List<? extends CelestialObject> objects, double[] positions,
                                  Function<EquatorialCoordinates, CartesianCoordinates> proj) {
        for (int i = 0; i < objects.size(); i++) {
            final CelestialObject current = objects.get(i);
            final CartesianCoordinates coordinates = proj.apply(current.equatorialPos());
            // store coordinates
            positions[2 * i] = coordinates.x();
            positions[2 * i + 1] = coordinates.y();
            allObjects.add(new CelestialPair(coordinates, current));
        }
    }

    /**
     * @return the sun.
     */
    public Sun sun() {
        return sun;
    }

    /**
     * @return the cartesian coordinates of the sun projected on the plan.
     */
    public CartesianCoordinates sunPosition() {
        return sunProjection;
    }

    /**
     * @return the moon.
     */
    public Moon moon() {
        return moon;
    }

    /**
     * @return the cartesian coordinates of the moon projected on the plan.
     */
    public CartesianCoordinates moonPosition() {
        return moonProjection;
    }

    /**
     * @return the list of the planets.
     */
    public List<Planet> planets() {
        return planets;
    }

    /**
     * @return the positions of all the planets, where each one of them
     * takes two indices: the first one, for its x-coordinate; the second one,
     * for its y-coordinate.
     */
    public double[] planetPositions() {
        return Arrays.copyOf(planetPositions, planetPositions.length);
    }

    /**
     * @return the list of the stars.
     */
    public List<Star> stars() {
        return catalogue.stars();
    }

    /**
     * @return the positions of all the planets, where each one of them
     * takes two indices: the first one, for its x-coordinate; the second one,
     * for its y-coordinate.
     */
    public double[] starPositions() {
        return Arrays.copyOf(starPositions, starPositions.length);
    }

    /**
     * @return the list of the asterisms.
     */
    public Set<Asterism> asterisms() {
        return catalogue.asterisms();
    }

    /**
     * @param asterism the asterism to look up the indices for
     * @return a list of integers containing the indices of its stars
     * in the same order as in the asterism.
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        return catalogue.asterismIndices(asterism);
    }

    /**
     * @param o the object to locate
     * @return the position in {@link HorizontalCoordinates} of the provided {@link CelestialObject}
     * on the current sky. Returns {@code null} if the provided objects is not in the list of objects.
     */
    public HorizontalCoordinates locate(CelestialObject o) {
        if (o instanceof Star) {
            final int index = ((Star) o).listIndex();
            return projection.inverseApply(CartesianCoordinates.of(starPositions[2 * index], starPositions[2 * index + 1]));
        } else if (o instanceof Sun) {
            return projection.inverseApply(sunProjection);
        } else if (o instanceof Moon) {
            return projection.inverseApply(moonProjection);
        } else {
            final List<CelestialPair> others = allObjects.subList(0, allObjects.size() - starPositions.length / 2);
            for (CelestialPair other : others) {
                // this relies on the fact that objects different from stars
                // have unique names
                if (other.object.equals(o)) {
                    return projection.inverseApply(other.position);
                }
            }
            return null;
        }
    }

    /**
     * @param where       a position on the plan
     * @param maxDistance the maximal distance allowed, from the point {@code where}
     *                    and the celestial objects.
     * @return an {@link Optional} containing the closest object to the provided point {@code where}
     * closer to it than {@code maxDistance}, or {@link Optional#empty()} if there is no such object.
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates where, double maxDistance) {
        CelestialObject closest = null;
        double best = Double.MAX_VALUE;
        for (CelestialPair pair : allObjects) {
            final double d = pair.position.distSquared(where);
            if (best > d && d <= maxDistance * maxDistance) {
                closest = pair.object;
                best = d;
            }
        }
        return Optional.ofNullable(closest);
    }

    /**
     * @return the collection of all celestial objects.
     */
    public List<CelestialPair> all() {
        return Collections.unmodifiableList(allObjects);
    }

}
