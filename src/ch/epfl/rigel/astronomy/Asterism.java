package ch.epfl.rigel.astronomy;

import java.util.List;

/**
 * Collection of bright stars near to each other.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 10/03/2020
 */
public final class Asterism {
    private final List<Star> stars;

    /**
     * @param stars the stars constituting the Asterism
     */
    public Asterism(List<Star> stars) {
        this.stars = stars;
    }

    /**
     * @return the list containing the stars composing the Asterism.
     */
    public List<Star> stars() {
        return stars;
    }
}
