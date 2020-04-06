package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Interval;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * Represents the moon.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 08/03/2020
 * @see CelestialObject
 */
public final class Moon extends CelestialObject {

    /**
     * This interval represents the possible values for Moon's phase.
     */
    private static final Interval PHASE_INTERVAL = ClosedInterval.of(0, 1);

    private final float phase;

    /**
     * @param equatorialPos the position of the Moon
     * @param angularSize   the angular size of the Moon
     * @param magnitude     the magnitude of the Moon
     * @param phase         the phase of the Moon
     * @throws IllegalArgumentException if {@code phase} is not in between 0 and 1, inclusive
     */
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super("Lune", equatorialPos, angularSize, magnitude);
        // Here, we do not write: this.phase = checkInInterval(...)
        // to avoid casting the value of phase twice (first to a double and then back to a float).
        checkInInterval(PHASE_INTERVAL, phase);
        this.phase = phase;
    }

    @Override
    public String info() {
        return String.format(Locale.ROOT, "%s (%.1f%%)", name(), phase * 100f);
    }

}
