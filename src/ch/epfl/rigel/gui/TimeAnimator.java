package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Represents an animator for animations, that uses the created
 * TimeAccelerator simulator.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/04/2020
 */
public final class TimeAnimator extends AnimationTimer {

    // true iff the TimeAnimator is running
    private final SimpleBooleanProperty running = new SimpleBooleanProperty(false);
    // the used accelerator
    private TimeAccelerator accelerator;
    // the beginning date of the simulation
    private final DateTimeBean date;
    // the initial handle(long) argument value
    private long start = -1L;
    // the previous handle(long) argument value
    private long previous;

    /**
     * @param date the beginning date of the time animator (will be updated)
     */
    public TimeAnimator(DateTimeBean date) {
        this.date = date;
    }

    @Override
    public void start() {
        super.start();
        running.set(true);
    }

    @Override
    public void handle(long now) {
        if (start == -1L) {
            start = now;
        } else if (start != now) {
            date.setZonedDateTime(accelerator.adjust(date.getZonedDateTime(), now - previous));
        }
        previous = now;
    }

    @Override
    public void stop() {
        super.stop();
        running.set(false);
    }

    /**
     * @return a property holding {@code true} if and only if the TimeAnimator
     * is running.
     */
    public ReadOnlyBooleanProperty running() {
        return running;
    }

    /**
     * Changes the accelerator in use, and therefore the way
     * time is simulated.
     *
     * @param accelerator the new accelerator to use
     */
    public void setAccelerator(TimeAccelerator accelerator) {
        this.accelerator = accelerator;
    }

}
