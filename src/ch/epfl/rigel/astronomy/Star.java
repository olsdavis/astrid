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
    private static final Interval COLOR_INTERVAL = ClosedInterval.of(-0.5, 5.5);

    private final int hipparcosId;
    private final float colorIndex;

    /**
     * @param hipparcosId   the Hipparcos identification code
     * @param name          the name
     * @param equatorialPos the position represented by EquatorialCoordinates
     * @param magnitude     the magnitude
     * @param colorIndex    the color index
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex) {
        super(name, equatorialPos, 0, magnitude);
        checkArgument(hipparcosId >= 0);
        // here, we do not write: this.colorIndex = checkInInterval(...)
        // to avoid casting the value of colorIndex twice (first to a double and then back to a float)
        checkInInterval(COLOR_INTERVAL, colorIndex);
        this.colorIndex = colorIndex;
        this.hipparcosId = hipparcosId;
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
        return (int) Math.floor(4600 * (1 / (0.92d * colorIndex + 1.7d) + 1 / (0.92d * colorIndex + 0.62d)));
    }
}
