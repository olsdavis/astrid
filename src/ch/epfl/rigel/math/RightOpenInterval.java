package ch.epfl.rigel.math;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.*;

/**
 * Represents an interval closed on the left and open
 * on the right.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 17/02/2020
 */
public final class RightOpenInterval extends Interval {

    /**
     * @param x a double
     * @param y a double
     * @return the floor mod.
     */
    private static double floorMod(double x, double y) {
        return x - y * (Math.floor(x / y));
    }

    /**
     * @param low  the lower bound
     * @param high the higher bound
     * @return an interval open on the right and closed on the left.
     *
     * @throws IllegalArgumentException if the lower bound ({@code low}) is greater than or
     * equal to the upper bound ({@code high}).
     */
    public static RightOpenInterval of(double low, double high) {
        checkArgument(low < high);

        return new RightOpenInterval(low, high);
    }

    /**
     * @param size the size of the interval
     * @return an interval open on the right, closed on the left,
     * centered on zero and of size {@code size}.
     *
     * @throws IllegalArgumentException if {@code size} is not positive.
     */
    public static RightOpenInterval symmetric(double size) {
        checkArgument(size > 0);

        return of(-size / 2d, size / 2d);
    }

    /**
     * @param low  the lower bound of the interval
     * @param high the upper bound of the interval
     */
    private RightOpenInterval(double low, double high) {
        super(low, high);
    }

    /**
     * @param v the value to reduce
     * @return reduces the value {@code v}.
     */
    public double reduce(double v) {
        return low() + floorMod(v - low(), high() - low());
    }

    /**
     * @param d the double to verify
     * @return {@code true} if {@code d} is inside the open interval or equal
     * to the lower bound.
     */
    @Override
    public boolean contains(double d) {
        return d >= low() && d < high();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s[", low(), high());
    }

}
