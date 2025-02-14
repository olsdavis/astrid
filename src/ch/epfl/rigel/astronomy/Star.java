package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Interval;

import static ch.epfl.rigel.Preconditions.checkArgument;
import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * Represents a star.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 10/03/2020
 */
public final class Star extends CelestialObject {
    /**
     * This interval represents the possible values that color indices take.
     */
    private static final Interval COLOR_INTERVAL = ClosedInterval.of(-0.5d, 5.5d);

    private final int hipparcosId;
    private final int colorTemperature;

    /**
     * @param hipparcosId   the Hipparcos identification code
     * @param name          the name
     * @param equatorialPos the position represented by EquatorialCoordinates
     * @param magnitude     the magnitude
     * @param colorIndex    the color index
     * @throws IllegalArgumentException if {@code hipparcosId} is negative
     * @throws IllegalArgumentException if {@code colorIndex} is not between -0.5 and 5.5, inclusive.
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex) {
        super(name, equatorialPos, 0, magnitude);
        checkArgument(hipparcosId >= 0);
        // Here, we do not write: this.colorIndex = checkInInterval(...)
        // to avoid casting the value of colorIndex twice (first to a double and then back to a float)
        checkInInterval(COLOR_INTERVAL, colorIndex);
        this.hipparcosId = hipparcosId;
        colorTemperature = (int) (4600 * (1 / (0.92d * colorIndex + 1.7d) + 1 / (0.92d * colorIndex + 0.62d)));
    }

    /**
     * @return the Hipparcos identification code of the star.
     */
    public int hipparcosId() {
        return hipparcosId;
    }

    /**
     * Computes the color temperature (in Kelvins) according to the B-V color index of the star.
     *
     * @return the color temperature associated with the B-V color index of the star.
     */
    public int colorTemperature() {
        return colorTemperature;
    }

    @Override
    public Type getType() {
        return Type.STAR;
    }

}
