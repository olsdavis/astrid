package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Holds the entire stars and asterisms catalogues.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 28/03/2020
 */
public final class StarCatalogue {

    private final List<Star> starCatalogue;
    private final Map<Asterism, List<Integer>> asterismMap;

    /**
     * @param stars     the stars of the star catalogue
     * @param asterisms the asterisms of the star catalogue
     *
     * @throws IllegalArgumentException if there is an asterism, from {@code asterisms}, that
     * has a star that is not in {@code star}
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        for (Asterism ast : asterisms) {
            Preconditions.checkArgument(stars.containsAll(ast.stars()));
        }
        starCatalogue = List.copyOf(stars);
        asterismMap = new HashMap<>(asterisms.size());
        final Map<Star, Integer> indices = new HashMap<>(stars.size());
        for (int i = 0; i < stars.size(); i++) {
            indices.put(stars.get(i), i);
        }
        for (Asterism asterism : asterisms) {
            asterismMap.put(asterism, asterism.stars().stream()
                    .map(indices::get)
                    .collect(Collectors.toUnmodifiableList()));
        }
    }

    /**
     * @return the stars of the star catalogue.
     */
    public List<Star> stars() {
        return starCatalogue;
    }

    /**
     * @return a {@link Set} of the asterisms contained in the current instance.
     */
    public Set<Asterism> asterisms() {
        return Set.copyOf(asterismMap.keySet());
    }

    /**
     * Finds the indices of the stars of an asterism in the {@code starCatalogue}.
     *
     * @param asterism the asterism of which we want to find the indices of the stars composing it.
     * @return a {@code List} of integers corresponding to the positions in the {@code starCatalogue}
     * of the stars of the {@code asterism}.
     *
     * @throws IllegalArgumentException if there is no such asterism in this catalogue
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        Preconditions.checkArgument(asterismMap.containsKey(asterism));
        return asterismMap.get(asterism);
    }

    /**
     * Builder for star catalogues.
     */
    public final static class Builder {

        private List<Star> starCatalogue = new ArrayList<>();
        private List<Asterism> asterismCatalogue = new ArrayList<>();

        /**
         * Adds the given Star {@code star} to the list that holds the stars of the catalogue to build.
         *
         * @param star the star to add to the catalogue
         * @return the current {@link Builder} instance.
         */
        public Builder addStar(Star star) {
            starCatalogue.add(star);
            return this;
        }

        /**
         * @return an unmodifiable view of the list of stars.
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(starCatalogue);
        }

        /**
         * Adds the given {@link Asterism} instance to the list of asterisms.
         *
         * @param asterism the asterism to add to the catalogue
         * @return the current {@link Builder} instance.
         */
        public Builder addAsterism(Asterism asterism) {
            asterismCatalogue.add(asterism);
            return this;
        }

        /**
         * @return an unmodifiable view of the list that holds the asterisms of the catalogue to build.
         */
        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterismCatalogue);
        }

        /**
         * Loads the builder data from the provided input stream.
         *
         * @param inputStream of data to be loaded
         * @param loader      loads the data into the {@link  Builder}
         * @return the current {@code Builder} instance.
         * @throws IOException if an exception is thrown during the reading of the {@code inputStream}.
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        /**
         * @return a new instance of {@link StarCatalogue} with the stars that have been added through {@link #addStar(Star)}}
         * and the asterisms that have been added through {@link #addAsterism(Asterism)}.
         */
        public StarCatalogue build() {
            return new StarCatalogue(starCatalogue, asterismCatalogue);
        }

    }

    /**
     * One-method interface created for data-loading purposes.
     */
    public interface Loader {
        /**
         * Loads data from an input stream into a builder.
         *
         * @param inputStream an InputStream from which the loader must read data
         * @param builder     {@link Builder} in which the data is loaded from the provided InputStream {@code inputStream}
         * @throws IOException if an exception is thrown during the reading of the provided InputStream {@code inputStream}
         *
         * @see AsterismLoader
         * @see HygDatabaseLoader
         */
        void load(InputStream inputStream, Builder builder) throws IOException;
    }

}
