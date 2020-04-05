package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * References the entire stars and asterisms catalogues into a single instance.
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
        return asterismMap.keySet();
    }

    /**
     * Finds the indices of the stars of an asterism in the {@code starCatalogue}.
     *
     * @param asterism of which we want to find the indices of the start composing it.
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
         * Adds the given {@link Star} instance to {@code starCatalogue}.
         *
         * @param star The star to add to the catalogue.
         * @return the current {@link Builder} instance ({@code this}).
         */
        public Builder addStar(Star star) {
            starCatalogue.add(star);
            return this;
        }

        /**
         * @return an unmodifiable view of {@code starCatalogue}.
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(starCatalogue);
        }

        /**
         * Adds the given {@link Asterism} instance to {@code asterismCatalogue}.
         *
         * @param asterism The asterism to add to the catalogue.
         * @return the current {@link Builder} instance ({@code this)}
         */
        public Builder addAsterism(Asterism asterism) {
            asterismCatalogue.add(asterism);
            return this;
        }

        /**
         * @return an unmodifiable view of the underlying list that holds the asterisms of the catalogue.
         */
        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterismCatalogue);
        }

        /**
         * Loads the builder data from the provided input stream.
         *
         * @param inputStream of data to be loaded.
         * @param loader      Loads the data into the {@link  Builder}.
         * @return the {@code Builder} that is loaded.
         * @throws IOException in case of I/O error.
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        /**
         * @return a new instance of {@link StarCatalogue} with built {@code starCatalogue} and {@code asterismCatalogue}.
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
         * @param inputStream Flow of data to be loaded.
         * @param builder     {@link Builder} in which we load the data from {@code inputStream}.
         * @throws IOException in case of I/O error.
         *
         * @see AsterismLoader
         * @see HygDatabaseLoader
         */
        void load(InputStream inputStream, Builder builder) throws IOException;
    }

}
