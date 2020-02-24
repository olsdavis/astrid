package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 17/02/2020
 */
public class Preconditions {

    /**
     * Throws an {@link IllegalArgumentException} if {@code isTrue} is {@code false}.
     *
     * @param isTrue indicates whether the condition is satisfied or not
     */
    public static void checkArgument(boolean isTrue) {
        if (!isTrue) {
            throw new IllegalArgumentException("condition not satisfied");
        }
    }

    /**
     * @param interval the interval
     * @param value    the value to check
     * @return {@code value} if it is inside the provided interval; otherwise
     * throws an {@link IllegalArgumentException}.
     */
    public static double checkInInterval(Interval interval, double value) {
        if (!interval.contains(value)) {
            throw new IllegalArgumentException("value out of bounds: " + value + " not in " + interval.toString());
        }
        return value;
    }

    private Preconditions() {
    }

}
