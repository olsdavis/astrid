package ch.epfl.rigel.math;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.*;

/**
 * Represents an interval closed on the left and open
 * on the right.
 *
 * @author Oscar Davis
 * Creation date: 17/02/2020
 */
public class RightOpenInterval extends Interval {

    private static double floorMod(double x, double y) {
        return x - y * (Math.floor(x / y));
    }

    /**
     * @param low  the lower bound
     * @param high the higher bound
     * @return an interval open on the right and closed on the left.
     */
    public static RightOpenInterval of(double low, double high) {
        checkArgument(low < high);

        return new RightOpenInterval(low, high);
    }

    /**
     * @param size the size of the interval
     * @return an interval open on the right, closed on the left,
     * centered on zero and of size {@code size}.
     */
    public static RightOpenInterval symmetric(double size) {
        checkArgument(size != 0);

        return of(-size / 2d, size / 2d);
    }

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

    @Override
    public boolean contains(double d) {
        return d >= low() && d < high();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s[", String.valueOf(low()), String.valueOf(high()));
    }

}
