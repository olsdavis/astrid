package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.Locale;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * Represents and computes stereographic projections.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 06/03/2020
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {
    private final double lambda0;
    private final double phi1;
    private final double sinPhi1;
    private final double cosPhi1;

    /**
     * @param center The center of the projection
     */
    public StereographicProjection(HorizontalCoordinates center) {
        lambda0 = center.az();
        phi1 = center.alt();
        sinPhi1 = sin(phi1);
        cosPhi1 = cos(phi1);
    }

    /**
     * Computes the coordinates of the center of the projection circle associated
     * with the parallel to be projected (running through the point of coordinates
     * given by {@code hor}).
     *
     * @param hor An instance of {@link HorizontalCoordinates} representing the coordinates
     *            of the point through which runs the parallel.
     * @return a new instance of {@link CartesianCoordinates} with coordinates associated
     * with the center of the projection circle.
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        return CartesianCoordinates.of(0, cosPhi1 / (sin(hor.alt()) + sinPhi1));
    }

    /**
     * Computes the radius of the circle corresponding to the projection of the parallel
     * going through the point of coordinates given by {@code hor}.
     *
     * @param hor An instance of {@link HorizontalCoordinates} representing the coordinates
     *            of the point through which runs the parallel.
     * @return the radius of the circle resulting from the projection of the parallel.
     */
    public double circleRadiusForParallel(HorizontalCoordinates hor) {
        return cos(hor.alt()) / (sin(hor.alt()) + sinPhi1);
    }

    /**
     * Computes the diameter of the circle corresponding to the projection of a sphere
     * with angular size {@code rad}, assuming it is centered at the center of the projection,
     * whom we consider to be on the horizon.
     *
     * @param rad the angular size of the sphere we wish to project on the plane.
     * @return the diameter of the projection circle.
     */
    public double applyToAngle(double rad) {
        return 2 * tan(rad / 4d);
    }

    /**
     * Applies the stereographic projection to a point with coordinates given by {@code azAlt}.
     *
     * @param azAlt The instance of {@link HorizontalCoordinates} representing the coordinates
     *              which we want to project on a plane.
     * @return a new instance of {@link CartesianCoordinates} with coordinates corresponding to the projection.
     */
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        final double lambdaD = azAlt.az() - lambda0;
        final double sinAzAlt = sin(azAlt.alt());
        final double cosAzAlt = cos(azAlt.alt());
        final double cosLambdaD = cos(lambdaD);
        final double d = 1d / (1 + sinAzAlt * sinPhi1
                + cosAzAlt * cosPhi1 * cosLambdaD);
        return CartesianCoordinates.of(d * cosAzAlt * sin(lambdaD),
                d * (sinAzAlt * cosPhi1 - cosAzAlt * sinPhi1 * cosLambdaD));
    }

    /**
     * Computes the {@link HorizontalCoordinates} of the projection point with
     * coordinates given by  {@code xy}.
     *
     * @param xy The instance of {@link CartesianCoordinates} we wish to project back to HorizontalCoordinates.
     * @return an instance of {@link HorizontalCoordinates} with coordinates such that
     * the projection of these gives us {@code xy}.
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        if (xy.x() == 0 && xy.y() == 0) {
            return HorizontalCoordinates.of(lambda0, phi1);
        }
        // rho squared
        final double rhoS = xy.x() * xy.x() + xy.y() * xy.y();
        // the square root of rho
        final double rho = sqrt(rhoS);
        // applying the formulas
        final double sinC = 2 * rho / (rhoS + 1);
        final double cosC = (1 - rhoS) / (rhoS + 1);
        final double lambda = Angle.normalizePositive(Math.atan2(xy.x() * sinC, rho * cosPhi1 * cosC
                - xy.y() * sinPhi1 * sinC) + lambda0);
        final double phi = asin(cosC * sinPhi1 + (xy.y() * sinC * cosPhi1 / rho));
        return HorizontalCoordinates.of(lambda, phi);
    }

    /**
     * @throws UnsupportedOperationException this operation is forbidden.
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("tried to call hashCode on StereographicProjection");
    }

    /**
     * @throws UnsupportedOperationException this operation is forbidden.
     */
    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("tried to call equals on StereographicProjection");
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "StereographicProjection(x=%.4f, y=%.4f)",
                lambda0, phi1);
    }
}
