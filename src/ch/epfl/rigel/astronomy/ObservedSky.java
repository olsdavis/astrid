package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    private final StarCatalogue catalogue;

    private final Sun sun;
    private final CartesianCoordinates sunProjection;

    private final Moon moon;
    private final CartesianCoordinates moonProjection;

    private final List<Planet> planets;
    private final double[] planetPositions;

    private final double[] starPositions;

    /**
     * @param moment     the moment at which the sky is observed
     * @param observer   the position from which the sky is observed
     * @param projection the projection to use
     * @param catalogue  the catalogue of stars
     */
    public ObservedSky(ZonedDateTime moment, GeographicCoordinates observer, StereographicProjection projection, StarCatalogue catalogue) {
        this.catalogue = catalogue;

        // days since J2010
        final double d = Epoch.J2010.daysUntil(moment);
        // the conversion used for the current situation
        final EclipticToEquatorialConversion eclipticToEq = new EclipticToEquatorialConversion(moment);
        final EquatorialToHorizontalConversion eqToHorizontal = new EquatorialToHorizontalConversion(moment, observer);

        sun = SunModel.SUN.at(d, eclipticToEq);
        sunProjection = projection.apply(eqToHorizontal.apply(sun.equatorialPos()));

        moon = MoonModel.MOON.at(d, eclipticToEq);
        moonProjection = projection.apply(eqToHorizontal.apply(moon.equatorialPos()));

        planets = PlanetModel.ALL.stream()
                .filter(p -> p != PlanetModel.EARTH)
                .map(p -> p.at(d, eclipticToEq))
                .collect(Collectors.toList());
        planetPositions = buildPositionArray(planets, projection, eqToHorizontal);

        starPositions = buildPositionArray(catalogue.stars(), projection, eqToHorizontal);
    }

    /**
     * @param objects    the objects to project and store
     * @param projection the projection to use
     * @param conversion the conversion from equatorial to horizontal coordinates to use
     * @return an array of twice the size of {@code objects} containing the coordinates of the
     * given objects projected on a plan, written as {@code 2 * i -> x, 2 * i + 1 -> y}.
     */
    private double[] buildPositionArray(List<? extends CelestialObject> objects, StereographicProjection projection, EquatorialToHorizontalConversion conversion) {
        final double[] ret = new double[2 * objects.size()];

        for (int i = 0; i < objects.size(); i++) {
            final CelestialObject current = objects.get(i);
            final CartesianCoordinates coordinates = projection.apply(conversion.apply(current.equatorialPos()));
            ret[2 * i] = coordinates.x();
            ret[2 * i + 1] = coordinates.y();
        }

        return ret;
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
     * @see #buildPositionArray(List, StereographicProjection, EquatorialToHorizontalConversion)
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
     * @see #buildPositionArray(List, StereographicProjection, EquatorialToHorizontalConversion)
     */
    public double[] starPositions() {
        return Arrays.copyOf(starPositions, starPositions.length);
    }

    /**
     * @param where       a position on the plan
     * @param maxDistance the maximal distance allowed, from the point {@code where}
     *                    and the celestial objects.
     * @return an {@code Optional} containing the closest object to the provided point {@code where}
     * closer to it than {@code maxDistance}, or an empty {@code Optional} if there is no such object.
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates where, double maxDistance) {
        //TODO: implement
        return Optional.empty();
    }

}
