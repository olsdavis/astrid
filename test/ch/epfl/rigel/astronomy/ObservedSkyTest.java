package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.test.Bench;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 04/04/2020
 */
public class ObservedSkyTest {

    private static class Pair {
        private CelestialObject object;
        private CartesianCoordinates coordinates;

        Pair(CelestialObject object, CartesianCoordinates coordinates) {
            this.object = object;
            this.coordinates = coordinates;
        }
    }

    static StarCatalogue catalogue;

    /**
     * The classic method for finding the closest object, pretty efficient.
     */
    static CelestialObject linearSearch(List<Pair> all, CartesianCoordinates coordinates, double d) {
        double best = Double.POSITIVE_INFINITY;
        CelestialObject closest = null;
        for (Pair pair : all) {
            final double dist = pair.coordinates.distSquared(coordinates);
            if (best > dist && dist <= d * d) {
                closest = pair.object;
                best = dist;
            }
        }
        return closest;
    }

    /**
     * The classic method implemented with streams (parallelStream), suggested in
     * the class group. This was written just to prove that it is highly inefficient.
     */
    static CelestialObject parallelSearch(List<Pair> all, ObservedSky sky, CartesianCoordinates coordinates, double d) {
        return all.parallelStream()
                .filter(p -> p.coordinates.distSquared(coordinates) <= d * d)
                .min((a, b) -> {
                    final double dA = a.coordinates.distSquared(coordinates);
                    final double dB = b.coordinates.distSquared(coordinates);
                    if (dA > dB) {
                        return 1;
                    } else if (dA < dB) {
                        return -1;
                    }
                    return 0;
                })
                .map(p -> p.object)
                .orElse(null);
    }

    @BeforeAll
    static void setUp() throws IOException {
        catalogue = new StarCatalogue.Builder()
                .loadFrom(ObservedSkyTest.class.getResourceAsStream("/hygdata_v3.csv"), HygDatabaseLoader.INSTANCE)
                .build();
    }

    /**
     * @param sky the {@link ObservedSky} instance
     * @return a list containing pairs of all celestial objects from the passed {@code sky} instance
     * with their {@link CartesianCoordinates} on the plan.
     */
    private List<Pair> all(ObservedSky sky) {
        final List<Pair> all = new ArrayList<>(sky.planets().size() + sky.stars().size() + 2);
        final double[] planets = sky.planetPositions();
        final double[] stars = sky.starPositions();
        for (int i = 0; i < sky.planets().size(); i++) {
            Planet planet = sky.planets().get(i);
            all.add(new Pair(planet, CartesianCoordinates.of(planets[2 * i], planets[2 * i + 1])));
        }
        for (int i = 0; i < sky.stars().size(); i++) {
            Star star = sky.stars().get(i);
            all.add(new Pair(star, CartesianCoordinates.of(stars[2 * i], stars[2 * i + 1])));
        }
        all.add(new Pair(sky.sun(), sky.sunPosition()));
        all.add(new Pair(sky.moon(), sky.moonPosition()));
        return all;
    }

    @Test
    void testStarsDistance() {
        final SplittableRandom random = TestRandomizer.newRandom();
        final ObservedSky sky = new ObservedSky(ZonedDateTime.now(), GeographicCoordinates.ofDeg(0, 0),
                new StereographicProjection(HorizontalCoordinates.of(0, 0)),
                catalogue
        );
        final List<Pair> all = all(sky);
        for (int i = 0; i < 100 * TestRandomizer.RANDOM_ITERATIONS; i++) {
            final double dist = 10d;
            final CartesianCoordinates coordinates = CartesianCoordinates.of(random.nextDouble(-10, 10),
                    random.nextDouble(-10, 10));
            final CelestialObject obj = linearSearch(all, coordinates, dist);
            final CelestialObject found = sky.objectClosestTo(coordinates, dist).orElse(null);
            assertEquals(obj, found);
        }
    }

    @Test
    void notWorking() {
        final CartesianCoordinates coordinates = CartesianCoordinates.of(-0.5027, 0.1949);
        final double dist = 0.1218880416988799;
        final ObservedSky sky = new ObservedSky(ZonedDateTime.now(), GeographicCoordinates.ofDeg(0, 0),
                new StereographicProjection(HorizontalCoordinates.of(0, 0)),
                catalogue
        );
        final List<Pair> all = all(sky);
        final CelestialObject obj = linearSearch(all, coordinates, dist);
        final CelestialObject found = sky.objectClosestTo(coordinates, dist).orElse(null);
        assertEquals(obj, found);
    }

    // DISABLED: Benchmarking
    @Disabled
    @Test
    void benchmark() {
        final List<CartesianCoordinates> coordinates = new ArrayList<>(TestRandomizer.RANDOM_ITERATIONS);
        final SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < 1000 * TestRandomizer.RANDOM_ITERATIONS; i++) {
            coordinates.add(CartesianCoordinates.of(random.nextDouble(-10, 10), random.nextDouble(-10, 10)));
        }
        final ObservedSky sky = new ObservedSky(ZonedDateTime.now(), GeographicCoordinates.ofDeg(0, 0),
                new StereographicProjection(HorizontalCoordinates.of(0, 0)),
                catalogue
        );

        Bench.printBench(() -> {
            for (CartesianCoordinates coordinate : coordinates) {
                sky.objectClosestTo(coordinate, 10d);
            }
        }, coordinates.size());
        final List<Pair> all = all(sky);
        Bench.printBench(() -> {
            for (CartesianCoordinates coordinate : coordinates) {
                linearSearch(all, coordinate, 10d);
            }
        }, coordinates.size());
        // just to prove it's crap
        /*Bench.printBench(() -> {
            for (CartesianCoordinates coordinate : coordinates) {
                parallelSearch(all, sky, coordinate, 0.5d);
            }
        }, coordinates.size());*/
    }

    // DISABLED: outputs a value
    @Disabled
    @Test
    void meanDistance() {
        final ObservedSky sky = new ObservedSky(ZonedDateTime.now(), GeographicCoordinates.ofDeg(0, 0),
                new StereographicProjection(HorizontalCoordinates.of(0, 0)),
                catalogue
        );
        final List<CartesianCoordinates> coordinates = new ArrayList<>(catalogue.stars().size());
        final double[] positions = sky.starPositions();
        for (int i = 0; i < catalogue.stars().size(); i++) {
            coordinates.add(CartesianCoordinates.of(positions[2 * i], positions[2 * i + 1]));
        }
        BigDecimal decimal = new BigDecimal("0");
        int count = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            final CartesianCoordinates a = coordinates.get(i);
            for (int j = i; j < coordinates.size(); j++) {
                final CartesianCoordinates b = coordinates.get(j);
                decimal = decimal.add(new BigDecimal(String.valueOf(a.dist(b))));
                count++;
            }
        }
        System.out.println(decimal.divide(new BigDecimal(count), MathContext.DECIMAL32));
        // result = 2.557845
    }

}
