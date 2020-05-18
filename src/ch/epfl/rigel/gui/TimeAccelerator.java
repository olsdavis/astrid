package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Represents time accelerators for time simulations.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 21/04/2020
 */
@FunctionalInterface
public interface TimeAccelerator {

    /**
     * @param acc the acceleration factor
     * @return a continuous TimeAccelerator with the provided {@code acc}
     * time acceleration.
     */
    static TimeAccelerator continuous(long acc) {
        return (initial, elapsed) -> initial.plus(Duration.ofNanos(acc * elapsed));
    }

    /**
     * @param freq the frequency per second
     * @param step the step of the discrete accelerator
     * @return a discrete accelerator of frequency {@code freq} and step {@code step}.
     */
    static TimeAccelerator discrete(long freq, Duration step) {
        // TODO: verify
        return (initial, elapsed) -> initial.plus(step.multipliedBy((long) Math.floor(freq * elapsed / 1_000_000_000f)));
    }

    /**
     * Calculates the new simulated time.
     *
     * @param initial the beginning time of the simulation
     * @param elapsed the time elapsed from the beginning of the simulation, in nanoseconds
     * @return the new simulated time in the form of a {@link ZonedDateTime}.
     */
    ZonedDateTime adjust(ZonedDateTime initial, long elapsed);

}
