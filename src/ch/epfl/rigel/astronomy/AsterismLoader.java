package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A loader for catalogues of asterisms.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 28/03/2020
 */
public enum AsterismLoader implements StarCatalogue.Loader {

    /**
     * The single instance of the AsterismLoader.
     */
    INSTANCE;

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                StandardCharsets.US_ASCII));
        String str;
        while ((str = reader.readLine()) != null && !str.equals("")) {
            // convert to a list of integers
            final List<Integer> hipIndicesStars = Arrays.stream(str.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toUnmodifiableList());
            // allocate directly the maximal (and the very likely) size of the List
            final List<Star> asterismStars = new ArrayList<>(hipIndicesStars.size());
            // allows to exit without iterating over all the stars, in most cases
            int added = 0;
            for (Star star : builder.stars()) {
                // if the indices to add hold the star that's currently being iterated
                // add it to the stars of the asterism
                if (hipIndicesStars.contains(star.hipparcosId())) {
                    asterismStars.add(star);
                    added++;
                }
                // add all stars: done!
                if (added == hipIndicesStars.size()) {
                    break;
                }
            }
            builder.addAsterism(new Asterism(asterismStars));
        }
    }

}
