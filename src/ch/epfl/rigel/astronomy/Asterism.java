package ch.epfl.rigel.astronomy;

import java.util.List;
import java.util.Objects;

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
    private List<Integer> indices;

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

    /**
     * If the value has never been accessed before, this method obtains it
     * and caches it, in order to avoid too much calls on the {@link ObservedSky#asterismIndices(Asterism)}
     * method, which uses a Map.
     * <p>
     * This method uses the assumption that all the {@link ObservedSky} instances
     * contain the same single {@link StarCatalogue} instance used through the whole
     * program.
     *
     * @param sky the observed sky from which we want to obtain the indices
     * @return the indices of the stars composing this asterism.
     *
     * @throws NullPointerException if {@code sky} is {@code null}
     */
    public List<Integer> indices(ObservedSky sky) {
        return indices == null ? indices = Objects.requireNonNull(sky).asterismIndices(this) : indices;
    }

}
