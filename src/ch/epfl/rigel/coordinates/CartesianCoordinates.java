package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 06/03/2020
 */
public final class CartesianCoordinates {
    private final double x;
    private final double y;

    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }

    /**
     * @return the abscissa of the current instance.
     */
    public final double x() {
        return x;
    }

    /**
     * @return the ordinate of the current instance.
     */
    public final double y() {
        return y;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f, y=%.4f)", x, y);
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException("This operation is not supported.");
    }

    public final int equals() {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
}
