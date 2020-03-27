package ch.epfl.rigel.math;

/**
 * Represents an interval of real numbers.
 *
 * @see ClosedInterval
 * @see RightOpenInterval
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 17/02/2020
 */
public abstract class Interval {

    private final double low;
    private final double high;

    /**
     * @param low  the lower bound of the interval
     * @param high the upper bound of the interval
     */
    protected Interval(double low, double high) {
        this.low = low;
        this.high = high;
    }

    /**
     * @return the upper bound of the interval.
     */
    public final double low() {
        return low;
    }

    /**
     * @return the lower bound of the interval.
     */
    public final double high() {
        return high;
    }

    /**
     * @return the length of the interval.
     */
    public final double size() {
        return high - low;
    }

    /**
     * @param d the double to verify
     * @return {@code true} if the given double {@code d} is inside the interval.
     */
    public abstract boolean contains(double d);

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException("unsupported operation");
    }

}
