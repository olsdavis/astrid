package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.*;
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
     * Represents the size of a chunk.
     */
    private static final double CHUNK_SIZE = 0.5d;

    /**
     * @param a a coordinate
     * @return the chunk pair coordinate of {@code x}, inclusive on the left,
     * exclusive on the right.
     */
    private static int pairOf(double a) {
        return (int) Math.floor(a / CHUNK_SIZE);
    }

    /**
     * Represents a pair of {@link SkyChunk} coordinates. Its coordinates
     * on the plan correspond are {@code (x * CHUNK_SIZE, y * CHUNK_SIZE}).
     * It covers an area of {@code CHUNK_SIZE * CHUNK_SIZE}.
     *
     * @see SkyChunk
     */
    private class ChunkPair {
        private int x;
        private int y;

        /**
         * Converts the passed parameters to their chunk subdivision.
         *
         * @param x the x-coordinate of a point
         * @param y the y-coordinate of a point
         */
        ChunkPair(double x, double y) {
            this(pairOf(x), pairOf(y));
        }

        /**
         * Creates the chunk pair of the given subdivision.
         *
         * @param x the x subdivision of the plan
         * @param y the y subdivision of the plan
         */
        ChunkPair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Creates the chunk pair associated to the provided coordinates {@code coordinates}.
         *
         * @param coordinates the coordinates of a point on the plan
         */
        ChunkPair(CartesianCoordinates coordinates) {
            this(coordinates.x(), coordinates.y());
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ChunkPair)) {
                return false;
            }
            return ((ChunkPair) obj).x == x && ((ChunkPair) obj).y == y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "ChunkPair{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    /**
     * Represents a pair of a CelestialObject and its cartesian coordinates
     * on the plan. Used for {@link SkyChunk}s.
     */
    private class CelestialPair {
        private final CartesianCoordinates coordinates;
        private final CelestialObject object;

        /**
         * @param coordinates the object's coordinates on the plan
         * @param object      the object itself
         */
        CelestialPair(CartesianCoordinates coordinates, CelestialObject object) {
            this.coordinates = coordinates;
            this.object = object;
        }
    }

    /**
     * A SkyChunk is a chunk of {@code CHUNK_SIZE * CHUNK_SIZE} of the ObservedSky which contains
     * all the celestial objects in its area. It allows faster search for
     * {@link #objectClosestTo(CartesianCoordinates, double)}.
     */
    private class SkyChunk {
        private final List<CelestialPair> objects = new ArrayList<>();

        /**
         * Represents a result of {@link #closestTo(CartesianCoordinates, double)}.
         */
        class SearchResult {
            double distance;
            CelestialObject object;

            /**
             * @param distance the distance of the closest object
             * @param object   the object itself
             */
            SearchResult(double distance, CelestialObject object) {
                this.distance = distance;
                this.object = object;
            }
        }

        /**
         * @param point       the original point of the {@link #objectClosestTo(CartesianCoordinates, double)} search
         * @param maxDistance the maximal distance to the point {@code point}
         * @return the closest object in this chunk to the provided {@code point}
         * if it is closer to the point {@code point} than {@code maxDistance}.
         */
        Optional<SearchResult> closestTo(CartesianCoordinates point, double maxDistance) {
            CelestialPair closest = null;
            double best = Double.POSITIVE_INFINITY;
            for (CelestialPair object : objects) {
                final double d = point.distSquared(object.coordinates);
                if (best > d && d < maxDistance * maxDistance) {
                    closest = object;
                    best = d;
                }
            }
            if (closest == null) {
                return Optional.empty();
            } else {
                return Optional.of(new SearchResult(best, closest.object));
            }
        }
    }

    // IMPROVEMENT: possibly use immutable lists for star & planet positions
    // instead of using array and copying them. Maybe this will not be feasible
    // because of the future code base; but we may consider this solution.

    private final StarCatalogue catalogue;

    private final Sun sun;
    private final CartesianCoordinates sunProjection;

    private final Moon moon;
    private final CartesianCoordinates moonProjection;

    private final List<Planet> planets;
    private final double[] planetPositions;

    private final double[] starPositions;

    private final Map<ChunkPair, SkyChunk> chunks = new HashMap<>();

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
        putInChunk(sun, sunProjection);

        moon = MoonModel.MOON.at(d, eclipticToEq);
        moonProjection = projection.apply(eqToHorizontal.apply(moon.equatorialPos()));
        putInChunk(moon, moonProjection);

        planets = PlanetModel.ALL.stream()
                .filter(p -> p != PlanetModel.EARTH) // filtering the Earth
                .map(p -> p.at(d, eclipticToEq))
                .collect(Collectors.toList());
        planetPositions = new double[2 * planets.size()];
        for (int i = 0; i < planets.size(); i++) {
            final Planet current = planets.get(i);
            final CartesianCoordinates coordinates = projection.apply(eqToHorizontal.apply(current.equatorialPos()));
            // store coordinates
            planetPositions[2 * i] = coordinates.x();
            planetPositions[2 * i + 1] = coordinates.y();
            // put it in its chunk
            putInChunk(current, coordinates);
        }

        starPositions = new double[2 * catalogue.stars().size()];
        for (int i = 0; i < catalogue.stars().size(); i++) {
            final Star current = catalogue.stars().get(i);
            final CartesianCoordinates coordinates = projection.apply(eqToHorizontal.apply(current.equatorialPos()));
            // store coordinates
            starPositions[2 * i] = coordinates.x();
            starPositions[2 * i + 1] = coordinates.y();
            // put it in its chunk
            putInChunk(current, coordinates);
        }
    }

    /**
     * Adds the provided object to the {@link SkyChunk} it must be in.
     *
     * @param object      the object to add
     * @param coordinates its cartesian coordinates on the plan
     */
    private void putInChunk(CelestialObject object, CartesianCoordinates coordinates) {
        final ChunkPair pair = new ChunkPair(coordinates);
        final SkyChunk chunk;
        if (chunks.containsKey(pair)) {
            chunk = chunks.get(pair);
        } else {
            chunks.put(pair, chunk = new SkyChunk());
        }
        chunk.objects.add(new CelestialPair(coordinates, object));
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
     * @param where       a position on the plan
     * @param maxDistance the maximal distance allowed, from the point {@code where}
     *                    and the celestial objects.
     * @return an {@link Optional} containing the closest object to the provided point {@code where}
     * closer to it than {@code maxDistance}, or {@link Optional#empty()} if there is no such object.
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates where, double maxDistance) {
        // The algorithm behind this method is pretty simple:
        // we split the sky into chunks (called SkyChunks), and,
        // to lookup for the closest object to the provided point,
        // we simply look in all candidate chunks, i.e. the ones
        // that are in range of maxDistance.

        // Overall, this method is much more efficient than a simple linear search,
        // and it is way faster for small distances, since less chunks are loaded.

        // This value represents the radius in which we are going
        // to look for chunks. We let it be at least 1.
        final int limit = (int) Math.ceil(maxDistance / CHUNK_SIZE);
        // LinkedLists allow O(1) time for adding elements.
        // It has shown better results while benchmarking.
        final List<ChunkPair> pairs = new LinkedList<>();
        // add all the chunks that are in range
        for (int i = -limit; i <= limit; i++) {
            for (int j = -limit; j <= limit; j++) {
                final CartesianCoordinates trans = where.translate(i * CHUNK_SIZE, j * CHUNK_SIZE);
                // IMPROVEMENT: here, we should make the criteria stronger
                // so we can reduce the number of linear searches
                // need to work on the distance between the point and the farthest
                // point accessible
                pairs.add(new ChunkPair(trans));
            }
        }

        // apply a linear search over the best results
        CelestialObject closest = null;
        double best = Double.POSITIVE_INFINITY;
        for (ChunkPair pair : pairs) {
            final SkyChunk chunk = chunks.get(pair);
            if (chunk == null) { // no such chunk => no objects at this location, anyway
                continue;
            }
            final Optional<SkyChunk.SearchResult> result = chunk.closestTo(where, maxDistance);
            // no need to check for the < maxDistance criteria, since the chunks already took care of it
            if (result.isPresent() && best > result.get().distance) {
                closest = result.get().object;
                best = result.get().distance;
            }
        }
        return Optional.ofNullable(closest);
    }

}
