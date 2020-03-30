package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * References the entire stars and asterisms catalogues into a single instance.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 28/03/2020
 */
public final class StarCatalogue {

    private final List<Star> starCatalogue;
    private final Map<Asterism, List<Integer>> asterismMap = new HashMap<>();

    /**
     * @param stars     the stars of the star catalogue
     * @param asterisms the asterisms of the star catalogue
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        Objects.requireNonNull(asterisms);
        Objects.requireNonNull(stars);
        for (Asterism ast : asterisms) {
            Preconditions.checkArgument(stars.containsAll(ast.stars()));
        }
        starCatalogue = List.copyOf(stars);
        for (Asterism ast : asterisms) {
            final List<Integer> indices = new ArrayList<>();
            final List<Star> starsOfAst = ast.stars();
            for (int i = 0; i < starCatalogue.size(); i++) {
                if (starsOfAst.contains(starCatalogue.get(i))) {
                    indices.add(i);
                }
            }
            asterismMap.put(ast, indices);
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
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        Preconditions.checkArgument(asterisms().contains(asterism));
        return asterismMap.get(asterism);
    }

    /**
     * Represents a catalogue builder.
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
         * @return an unmodifiable view of {@code asterismCatalogue}.
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
         * @see AsterismLoader
         * @see HygDatabaseLoader
         */
        void load(InputStream inputStream, Builder builder) throws IOException;
    }

}
