package ch.epfl.rigel.math;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Represents a closed interval.
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 17/02/2020
 */
public final class ClosedInterval extends Interval {

    /**
     * @param low  lower bound
     * @param high upper bound
     * @return a closed interval of the given bounds.
     */
    public static ClosedInterval of(double low, double high) {
        checkArgument(low < high);

        return new ClosedInterval(low, high);
    }

    /**
     * @param size the size of the interval
     * @return a closed interval of size {@code size}.
     */
    public static ClosedInterval symmetric(double size) {
        checkArgument(size > 0);

        return of(-size / 2d, size / 2d);
    }

    private ClosedInterval(double low, double high) {
        super(low, high);
    }

    /**
     * @param v the value to clip
     * @return clips the provided value.
     */
    public double clip(double v) {
        /*
        we could have also written:

        return Math.max(low(), Math.min(high(), v));

        yet, it is much clearer to write it explicitly with conditions
        */
        if (v >= high()) {
            return high();
        } else if (v <= low()) {
            return low();
        }

        return v;
    }

    @Override
    public boolean contains(double d) {
        return d >= low() && d <= high();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s]", low(), high());
    }

}
