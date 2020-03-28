package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * loads the entire catalogue of asterisms from an {@code InputStream}.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 28/03/2020
 */


public enum AsterismLoader implements StarCatalogue.Loader {

    INSTANCE;

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));
        String str;
        while (!(str = reader.readLine()).equals("")) {
            List<Star> asterismStars = new ArrayList<>();
            List<Integer> hipIndicesStars = parseOnArray(str.split(","));
            int count = 0;
            for (Star star :
                    builder.stars()) {
                if (hipIndicesStars.contains(star.hipparcosId())) {
                    asterismStars.add(star);
                    count++;
                }
                if (count == hipIndicesStars.size()) break;
            }
            builder.addAsterism(new Asterism(asterismStars));
        }
    }

    /**
     * Utility method to get a list of HipparcosId from a String array.
     *
     * @param stringArray the array from which we extract the list of hipparcosId.
     * @return a list of HipparcosId.
     */
    private List<Integer> parseOnArray(String[] stringArray) {
        List<Integer> hipparcosList = new ArrayList<>();
        for (String s :
                stringArray) {
            hipparcosList.add(Integer.parseInt(s));
        }
        return hipparcosList;
    }

}
