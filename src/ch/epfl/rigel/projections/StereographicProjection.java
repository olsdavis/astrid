package ch.epfl.rigel.projections;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;

import java.util.function.Function;

/**
 * Represents and computes stereographic projections.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 06/03/2020
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {
    private final double centerLon;
    private final double centerLat;
    private final double centerLatSin;
    private final double centerLatCos;

    /**
     * @param center the center of the projection
     */
    public StereographicProjection(HorizontalCoordinates center) {
        centerLon = center.az();
        centerLat = center.alt();
        centerLatSin = Math.sin(centerLat);
        centerLatCos = Math.cos(centerLat);
    }

    /**
     * Computes the coordinates of the center of the projection circle associated with the parallel to be projected
     * (running through the point of coordinates given by {@code hor}).
     *
     * @param hor An instance of {@link HorizontalCoordinates} representing the coordinates of the point through which runs the parallel.
     * @return a new instance of {@link CartesianCoordinates} with coordinates associated with the center of the projection circle.
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        return (hor.alt() == 0d && centerLat == 0d)
                ? CartesianCoordinates.of(0, Double.POSITIVE_INFINITY)
                : CartesianCoordinates.of(0, centerLatCos / (Math.sin(hor.alt()) + centerLatSin));
    }

    /**
     * Computes the radius of the circle corresponding to the projection of the parallel going through the point of coordinates given by {@code hor}.
     *
     * @param hor An instance of {@link HorizontalCoordinates} representing the coordinates of the point through which runs the parallel.
     * @return the radius of the circle resulting from the projection of the parallel.
     */
    public double circleRadiusForParallel(HorizontalCoordinates hor) {
        return (hor.alt() == 0d && centerLat == 0d)
                ? Double.POSITIVE_INFINITY
                : Math.cos(hor.alt()) / (Math.sin(hor.alt()) + centerLatSin);
    }

    /**
     * Computes the diameter of the circle corresponding to the projection of a sphere
     * with angular size {@code rad}, assuming it is centered at the center of the projection,
     * whom we consider to be on the horizon.
     * @param rad the angular size of the sphere we wish to project on the plane.
     * @return the diameter of the projection circle.
     */
    public double applyToAngle(double rad) {
        return 2*Math.tan(rad/4);
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
        final double lambdaD = azAlt.az() - centerLon;
        final double d = 1d / (1 + Math.sin(azAlt.alt()) * centerLatSin + Math.cos(azAlt.alt()) * centerLatCos * Math.cos(lambdaD));
        return CartesianCoordinates.of(d * Math.cos(azAlt.alt()) * Math.sin(lambdaD),
                d * (Math.sin(azAlt.alt()) * centerLatCos - Math.cos(azAlt.alt()) * centerLatSin * Math.cos(lambdaD)));
    }

    /**
     *
     *Computes the {@link HorizontalCoordinates} of the projection point with
     * coordinates given by  {@code xy}.
     * @param xy the instance of {@link CartesianCoordinates} we wish to un-project.
     * @return an instance of {@link HorizontalCoordinates} with coordinates such that
     * the projection of these gives us {@code xy}.
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        double rho = Math.sqrt(xy.x() * xy.x() + xy.y() * xy.y());
        double sinC = 2 * rho / (rho * rho + 1);
        double cosC = (1 - rho) / (rho * rho + 1);
        double lambda = Math.atan2(xy.x() * sinC, rho * centerLatCos * cosC - xy.y() * centerLatSin * sinC) + centerLon;
        double phi = Math.asin(cosC * centerLatSin + (xy.y() * sinC * centerLatCos / rho));
        return HorizontalCoordinates.of(lambda, phi);
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("This operation is not supported.");
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("This operation is not supported.");
    }

    @Override
    public String toString() {
        return String.format("StereographicProjection instance, centered at coordinates: " +
                "(x=%.4f, y=%.4f)", centerLon, centerLat);
    }
}
