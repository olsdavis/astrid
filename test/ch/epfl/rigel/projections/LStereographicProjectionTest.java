package ch.epfl.rigel.projections;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 07/03/2020
 */
class LStereographicProjectionTest {

    @Test
    void circleCenterForParallelWorks() {
        SplittableRandom random = TestRandomizer.newRandom();
        StereographicProjection trivial = new StereographicProjection(HorizontalCoordinates.of(0, 0));
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            //Non-trivial tests with trivial center.
            final double lat = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            assertEquals(1 / Math.sin(lat), trivial.circleCenterForParallel(HorizontalCoordinates.of(0, lat)).y());
        }
        //Infinity y-coordinate for zero latitude in both inputs.
        assertEquals(Double.POSITIVE_INFINITY, trivial.circleCenterForParallel(HorizontalCoordinates.of(0, 0)).y());
        //Other edge cases
        assertEquals(1, trivial.circleCenterForParallel(HorizontalCoordinates.of(0, Math.PI / 2)).y());
        assertEquals(0d, new StereographicProjection(HorizontalCoordinates.of(0, Math.PI / 2))
                .circleCenterForParallel(HorizontalCoordinates.of(0, 0)).y(), 10e-16);
        //Non-trivial tests.
        StereographicProjection center = new StereographicProjection(HorizontalCoordinates.of(0, Math.PI / 3));
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final double lat = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            assertEquals(1 / (2 * (Math.sin(lat) + (Math.sqrt(3) / 2))),
                    center.circleCenterForParallel(HorizontalCoordinates.of(0, lat)).y(), 10e-11);
        }
    }

    @Test
    void circleRadiusForParallelWorks() {
        StereographicProjection trivial = new StereographicProjection(HorizontalCoordinates.of(0, 0));
        assertEquals(Double.POSITIVE_INFINITY, trivial.circleRadiusForParallel(HorizontalCoordinates.of(0, 0)));
        //TODO radius tests for non infinite radius.
    }

    @Test
    void applyWorks() {
    }

    @Test
    void inverseApplyWorks() {
    }
}