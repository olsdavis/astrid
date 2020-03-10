package ch.epfl.rigel.astronomy;

import java.util.List;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 10/03/2020
 */
public final class Asterism {
    private final List<Star> stars;

    public Asterism(List<Star> stars) {
        this.stars = stars;
    }

    /**
     * @return the list containing the stars composing the asterism.
     */
    public List<Star> stars() {
        return stars;
    }
}
