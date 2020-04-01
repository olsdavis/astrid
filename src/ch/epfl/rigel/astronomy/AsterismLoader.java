package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
        final Map<Integer, Integer> indicesMap = new HashMap<>();
        for (int i = 0; i < builder.stars().size(); i++) {
            indicesMap.put(builder.stars().get(i).hipparcosId(), i);
        }
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                StandardCharsets.US_ASCII))) {
            String str;
            while ((str = reader.readLine()) != null && !str.equals("")) {
                // convert to a list of integers
                final List<Integer> hipIndicesStars = Arrays.stream(str.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toUnmodifiableList());
                final List<Star> asterismStars = hipIndicesStars.stream()
                        .map(indicesMap::get)
                        .map(builder.stars()::get)
                        .collect(Collectors.toList());
                builder.addAsterism(new Asterism(asterismStars));
            }
        }
    }

}
