package ch.epfl.rigel.gui;

import java.time.Duration;

/**
 * This enum holds the time accelerators that will be in use
 * for the time simulations.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 21/04/2020
 */
public enum NamedTimeAccelerator {

    /**
     * Real time continuous accelerator.
     */
    TIMES_1(
            "1×",
            TimeAccelerator.continuous(1L)
    ),
    /**
     * 30 times faster continuous accelerator.
     */
    TIMES_30(
            "30×",
            TimeAccelerator.continuous(30L)
    ),
    /**
     * 300 times faster continuous accelerator.
     */
    TIMES_300(
            "300×",
            TimeAccelerator.continuous(300L)
    ),
    /**
     * 3000 times faster continuous accelerator.
     */
    TIMES_3000(
            "3000×",
            TimeAccelerator.continuous(3000L)
    ),
    /**
     * Day per day discrete accelerator.
     */
    DAY(
            "Jour",
            TimeAccelerator.discrete(
                    60,
                    Duration.ofDays(1)
            )
    ),
    /**
     * Sidereal day per sidereal day discrete accelerator.
     */
    SIDEREAL_DAY(
            "Jour sidéral",
            TimeAccelerator.discrete(
                    60,
                    // 23h56m04s in seconds
                    Duration.ofSeconds(23 * 3600 + 56 * 60 + 4)
            )
    );

    private final String name;
    private final TimeAccelerator accelerator;

    /**
     * @param name        the name of the accelerator
     * @param accelerator the current accelerator
     */
    NamedTimeAccelerator(String name, TimeAccelerator accelerator) {
        this.name = name;
        this.accelerator = accelerator;
    }

    /**
     * @return the name of the time accelerator.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the time accelerator.
     */
    public TimeAccelerator getAccelerator() {
        return accelerator;
    }

    @Override
    public String toString() {
        return getName();
    }

}
