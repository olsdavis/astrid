package ch.epfl.rigel.astronomy;

import java.util.List;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Collection of bright stars near to each other.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 10/03/2020
 */
public final class Asterism {

    private final List<Star> stars;

    /**
     * @param stars the stars constituting the Asterism
     * @throws IllegalArgumentException if {@code stars} is empty or null
     */
    public Asterism(List<Star> stars) {
        checkArgument(stars != null && !stars.isEmpty());
        this.stars = List.copyOf(stars);
    }

    /**
     * @return the list containing the stars composing the Asterism.
     */
    public List<Star> stars() {
        return stars;
    }

}
