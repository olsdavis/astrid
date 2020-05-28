package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Star;

/**
 * Wraps stars for JavaFX.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public final class JFXStarWrapper extends JFXCelestialObjectWrapper<Star> {

    /**
     * @param wrapped the star to hold
     */
    public JFXStarWrapper(Star wrapped) {
        super(wrapped);
    }

}
