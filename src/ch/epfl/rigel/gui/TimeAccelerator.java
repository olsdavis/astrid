package ch.epfl.rigel.gui;

import java.time.ZonedDateTime;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 21/04/2020
 */
@FunctionalInterface
public interface TimeAccelerator {

    /**
     * Calculates the new simulated time.
     *
     * @param initial The initial simulation time.
     * @param elapsed The real time elapsed from the beginning of the simulation until now.
     * @return the new simulated time in the form of a {@link ZonedDateTime}.
     */
    ZonedDateTime newTime(ZonedDateTime initial, long elapsed);

}
