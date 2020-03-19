package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

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

    private final float phase;

    /**
     * @param equatorialPos the position of the Moon
     * @param angularSize   the angular size of the Moon
     * @param magnitude     the magnitude of the Moon
     * @param phase         the phase of the Moon
     */
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super("Lune", equatorialPos, angularSize, magnitude);
        checkInInterval(ClosedInterval.of(0, 1), phase);
        this.phase = phase;
    }

    @Override
    public String info() {
        return String.format(Locale.ROOT, "%s (%.1f%%)", name(), phase * 100);
    }

}
