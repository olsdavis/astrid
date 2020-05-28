package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;

/**
 * Wraps celestial objects for JavaFX.
 *
 * @param <T> the celestial object type
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public abstract class JFXCelestialObjectWrapper<T extends CelestialObject> {

    private final T wrapped;

    /**
     *
     * @param wrapped the celestial object to hold
     */
    public JFXCelestialObjectWrapper(T wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * @return the celestial object that is wrapped.
     */
    public final T unwrap() {
        return wrapped;
    }

}
