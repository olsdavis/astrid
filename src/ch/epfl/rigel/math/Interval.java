package ch.epfl.rigel.math;

/**
 * @author Oscar Davis
 * Creation date: 17/02/2020
 */
public abstract class Interval {

    private final double low;
    private final double high;

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
    public double high() {
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

}
