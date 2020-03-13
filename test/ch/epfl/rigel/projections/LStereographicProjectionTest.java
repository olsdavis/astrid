package ch.epfl.rigel.projections;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
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
            // Non-trivial tests with trivial center.
            final double lat = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            assertEquals(1 / Math.sin(lat), trivial.circleCenterForParallel(HorizontalCoordinates.of(0, lat)).y());
        }
        // Infinity y-coordinate for zero latitude in both inputs.
        assertEquals(Double.POSITIVE_INFINITY, trivial.circleCenterForParallel(HorizontalCoordinates.of(0, 0)).y());
        // Other edge cases
        assertEquals(1, trivial.circleCenterForParallel(HorizontalCoordinates.of(0, Math.PI / 2)).y());
        assertEquals(0d, new StereographicProjection(HorizontalCoordinates.of(0, Math.PI / 2))
                .circleCenterForParallel(HorizontalCoordinates.of(0, 0)).y(), 10e-16);
        // Non-trivial tests.
        StereographicProjection center = new StereographicProjection(HorizontalCoordinates.of(0, Math.PI / 3));
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final double lat = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            assertEquals(1 / (2 * (Math.sin(lat) + (Math.sqrt(3) / 2))),
                    center.circleCenterForParallel(HorizontalCoordinates.of(0, lat)).y(), 10e-11);
        }
    }

    @Test
    void circleRadiusForParallelWorks() {
        SplittableRandom random = TestRandomizer.newRandom();
        StereographicProjection trivial = new StereographicProjection(HorizontalCoordinates.of(0, 0));
        // Trivial test.
        assertEquals(Double.POSITIVE_INFINITY, trivial.circleRadiusForParallel(HorizontalCoordinates.of(0, 0)));
        // Non-trivial tests with trivial center.
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final double alt = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            assertEquals(1 / Math.tan(alt), trivial.circleRadiusForParallel(HorizontalCoordinates.of(0, alt)), 10e-11);
        }
        StereographicProjection nonTrivial = new StereographicProjection(HorizontalCoordinates.of(0, Math.PI / 6));
        // Non-trivial tests.
        assertEquals(Math.sqrt(3) / 2, nonTrivial.circleRadiusForParallel(HorizontalCoordinates.of(0, Math.PI / 6)), 10e-11);
        assertEquals(Math.sqrt(2) / (Math.sqrt(2) + 1), nonTrivial.circleRadiusForParallel(HorizontalCoordinates.of(0, Math.PI / 4)));
    }

    @Test
    void applyWorks() {
        SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            // Trivial tests for same coordinates as the center of the projection center.
            final double angle1 = random.nextDouble(0, 2 * Math.PI - 10e-10);
            final double angle2 = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            HorizontalCoordinates pointToProject = HorizontalCoordinates.of(angle1, angle2);
            StereographicProjection randomCenter = new StereographicProjection(pointToProject);
            assertEquals(0, randomCenter.apply(pointToProject).x());
            assertEquals(0, randomCenter.apply(pointToProject).y());
        }
        StereographicProjection nonTrivial = new StereographicProjection(HorizontalCoordinates.of(Math.PI / 2, Math.PI / 4));
        StereographicProjection nonTrivial2 = new StereographicProjection(HorizontalCoordinates.of(Math.PI / 4, 0));
        HorizontalCoordinates pointToProject1 = HorizontalCoordinates.of(0, Math.PI / 2);
        assertEquals(0, nonTrivial2.apply(pointToProject1).x(), 10e-15);
        assertEquals(1, nonTrivial2.apply(pointToProject1).y(), 10e-15);
        HorizontalCoordinates pointToProject2 = HorizontalCoordinates.of(Math.PI/4, 0);
        assertEquals(-Math.sqrt(2)/3d, nonTrivial.apply(pointToProject2).x(), 10e-14);
        assertEquals(-1d/3d, nonTrivial.apply(pointToProject2).y());
    }

    @Test
    void inverseApplyWorks() {
        StereographicProjection centerOfProj = new StereographicProjection(HorizontalCoordinates.of(Math.PI/2,  Math.PI/2));
        CartesianCoordinates xy = CartesianCoordinates.of(1, 0);
        HorizontalCoordinates unProject = centerOfProj.inverseApply(xy);
        assertEquals(0, unProject.alt());
        assertEquals(Math.PI, unProject.az());
        CartesianCoordinates xy2 = CartesianCoordinates.of(0, 1);
        HorizontalCoordinates unProject2 = centerOfProj.inverseApply(xy2);
        assertEquals(0, unProject2.az());
    }

    @Test
    void toStringWorks() {
        assertEquals("StereographicProjection(x=0.0000, y=0.0000)",
                new StereographicProjection(HorizontalCoordinates.of(0, 0)).toString());
        assertEquals("StereographicProjection(x=0.7854, y=0.7854)",
                new StereographicProjection(HorizontalCoordinates.of(Math.PI / 4, Math.PI / 4)).toString());
        assertEquals("StereographicProjection(x=1.5708, y=1.5708)",
                new StereographicProjection(HorizontalCoordinates.of(Math.PI / 2, Math.PI / 2)).toString());
    }
}