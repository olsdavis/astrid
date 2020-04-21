package ch.epfl.rigel.gui;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * This enum holds the time accelerators that will be in use
 * for the time simulations.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 21/04/2020
 */
public enum NamedTimeAccelerator {

    TIMES_1(
            "1×",
            TimeAccelerator.continuous(1L)
    ),
    TIMES_30(
            "30×",
            TimeAccelerator.continuous(30L)
    ),
    TIMES_300(
            "300×",
            TimeAccelerator.continuous(300L)
    ),
    TIMES_3000(
            "3000×",
            TimeAccelerator.continuous(3000L)
    ),
    DAY(
            "jour",
            TimeAccelerator.discrete(TimeUnit.SECONDS.toNanos(60),
                    Duration.ofNanos(TimeUnit.DAYS.toNanos(1)))
    ),
    SIDEREAL_DAY(
            "jour sidéral",
            TimeAccelerator.discrete(TimeUnit.SECONDS.toNanos(60),
                    // 23h56m04s in seconds
                    Duration.ofNanos(TimeUnit.SECONDS.toNanos(23 * 3600 + 56 * 60 + 4)))
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

}
