package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Loads an entire stars catalogue (in this case, an HYG one), using an {@code InputStream}.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 28/03/2020
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {

    INSTANCE;


    /*All column constants. The values are intentionally  decreased by 1 in comparison to what is given in the HYG catalogue doc,
     * as columns are numbered starting from 1 instead of 0.
     * This conflicts the usage we wish to make of the data, as it is stored in an array, whose elements are numbered starting from 0.
     */

    private static final int HIP = 1;
    private static final int PROPER = 6;
    private static final int MAG = 13;
    private static final int CI = 16;
    private static final int RARAD = 23;
    private static final int DECRAC = 24;
    private static final int BAYER = 27;
    private static final int CON = 29;


    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));
        String str;
        while (!(str = reader.readLine()).equals("")) {
            String[] dataLine = str.split(",");
            String properName = dataLine[PROPER].equals("")
                    ? (dataLine[BAYER].equals("") ? "?" : dataLine[BAYER]) + " " + dataLine[CON]
                    : dataLine[PROPER];
            builder.addStar(new Star(
                    (dataLine[HIP].equals("") ? 0 : Integer.parseInt(dataLine[HIP])), //Hipparcos Id
                    properName, //Proper name of the star
                    EquatorialCoordinates.of(Double.parseDouble(dataLine[RARAD]), Double.parseDouble(dataLine[DECRAC])), //Equatorial coordinates of the star
                    (dataLine[MAG].equals("") ? 0 : Float.parseFloat(dataLine[MAG])), //Magnitude of the star
                    (dataLine[CI].equals("") ? 0 : Float.parseFloat(dataLine[CI])) //G-B color index of the star
            ));
        }
    }
}
