package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * Represents simple 2D cartesian coordinates.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 06/03/2020
 */
public final class CartesianCoordinates {
    private final double x;
    private final double y;

    /**
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return an instance of CartesianCoordinates representing the given {@code (x,y)} 2D point.
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }

    /**
     * @return the x-coordinate of the current instance.
     */
    public double x() {
        return x;
    }

    /**
     * @return the y-coordinate of the current instance.
     */
    public double y() {
        return y;
    }

    /**
     * @param other another point
     * @return the square of the distance between this point and the other provided point.
     */
    public double distSquared(CartesianCoordinates other) {
        return distSquared(other.x, other.y);
    }

    /**
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the square of the distance between this point and the point of coordinates {@code (x,y)}.
     */
    public double distSquared(double x, double y) {
        final double diffX = this.x - x;
        final double diffY = this.y - y;
        return diffX * diffX + diffY * diffY;
    }

    /**
     * @param other another point
     * @return the distance between this point and the other provided point.
     */
    public double dist(CartesianCoordinates other) {
        return Math.sqrt(distSquared(other));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f, y=%.4f)", x, y);
    }

    /**
     * @throws UnsupportedOperationException this operation is forbidden.
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("tried to call hashCode on CartesianCoordinates");
    }

    /**
     * @throws UnsupportedOperationException this operation is forbidden.
     */
    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("tried to call equals on CartesianCoordinates");
    }
}
