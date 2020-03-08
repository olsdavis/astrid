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
                d * Math.sin(azAlt.alt()) * centerLatCos - Math.cos(azAlt.az()) * centerLatSin * Math.cos(lambdaD));
    }

    /**
     * TODO
     *
     * @param point
     * @return
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates point) {
        double rho = Math.sqrt(point.x() * point.x() + point.y() * point.y());
        double sinC = 2 * rho / (rho * rho + 1);
        double cosC = (1 - rho) / (rho * rho + 1);
        double lambda = Math.atan2(point.x() * sinC, rho * centerLatCos * cosC - point.y() * centerLatSin * sinC) + centerLon;
        double phi = Math.asin(cosC * centerLatSin + (point.y() * sinC * centerLatCos / rho));
        return HorizontalCoordinates.of(lambda, phi);
    }
}
