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

    static CelestialObject linearSearch(List<Pair> all, ObservedSky sky, CartesianCoordinates coordinates, double d) {
        double best = Double.POSITIVE_INFINITY;
        CelestialObject closest = null;
        for (Pair pair : all) {
            final double dist = pair.coordinates.distSquared(coordinates);
            if (best > dist && dist <= d) {
                closest = pair.object;
                best = dist;
            }
        }
        return closest;
    }

    @BeforeAll
    static void setUp() throws IOException {
        catalogue = new StarCatalogue.Builder()
                .loadFrom(ObservedSkyTest.class.getResourceAsStream("/hygdata_v3.csv"), HygDatabaseLoader.INSTANCE)
                .build();
    }

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
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final CartesianCoordinates coordinates = CartesianCoordinates.of(random.nextDouble(-1, 1),
                    random.nextDouble(-1, 1));
            assertEquals(linearSearch(all, sky, coordinates, 0.5d),
                    sky.objectClosestTo(coordinates, 0.5d).orElse(null));
        }
    }

    @Disabled
    @Test
    void benchmark() {
        final List<CartesianCoordinates> coordinates = new ArrayList<>(TestRandomizer.RANDOM_ITERATIONS);
        final SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < 1000 * TestRandomizer.RANDOM_ITERATIONS; i++) {
            coordinates.add(CartesianCoordinates.of(random.nextDouble(-1, 1), random.nextDouble(-1, 1)));
        }
        final ObservedSky sky = new ObservedSky(ZonedDateTime.now(), GeographicCoordinates.ofDeg(0, 0),
                new StereographicProjection(HorizontalCoordinates.of(0, 0)),
                catalogue
        );

        Bench.printBench(() -> {
            for (CartesianCoordinates coordinate : coordinates) {
                sky.objectClosestTo(coordinate, 0.5d);
            }
        }, coordinates.size());
        final List<Pair> all = all(sky);
        Bench.printBench(() -> {
            for (CartesianCoordinates coordinate : coordinates) {
                linearSearch(all, sky, coordinate, 0.5d);
            }
        }, coordinates.size());
    }

}
