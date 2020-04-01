package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Loads an entire stars catalogue (in this case, an HYG one), using an {@code InputStream}.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 28/03/2020
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {

    /**
     * The single instance of the HygDatabaseLoader.
     */
    INSTANCE;

    /*
     * The following constants hold the indices of the columns of properties.
     * The values are intentionally decreased by 1 in comparison to what is given in the HYG catalogue doc,
     * as columns are numbered starting from 1 instead of 0.
     */

    /**
     * Column of the Hipparcos ID.
     */
    private static final int HIP = 1;
    /**
     * Column of the proper name.
     */
    private static final int PROPER = 6;
    /**
     * Column of the magnitude
     */
    private static final int MAG = 13;
    /**
     * Column of the color index.
     */
    private static final int CI = 16;
    /**
     * Column of the right ascension's value (in radians).
     */
    private static final int RARAD = 23;
    /**
     * Column of the declination's value (in radians).
     */
    private static final int DECRAD = 24;
    /**
     * Column of the Bayer designation.
     */
    private static final int BAYER = 27;
    /**
     * Column of the shortened name of the constellation.
     */
    private static final int CON = 29;

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                StandardCharsets.US_ASCII));
        String str;
        // we skip the first line, since it provides the names of the columns
        reader.readLine();
        while ((str = reader.readLine()) != null && !str.equals("")) {
            final String[] dataLine = str.split(",");
            final String properName = dataLine[PROPER].equals("")
                    ? ((dataLine[BAYER].equals("") ? "?" : dataLine[BAYER]))  // default bayer value = '?'
                        + " " + dataLine[CON]
                    : dataLine[PROPER];
            builder.addStar(new Star(
                    // Hipparcos ID
                    (dataLine[HIP].equals("") ? 0 : Integer.parseInt(dataLine[HIP])),
                    // proper name
                    properName,
                    // coordinates
                    EquatorialCoordinates.of(Double.parseDouble(dataLine[RARAD]), Double.parseDouble(dataLine[DECRAD])),
                    // magnitude
                    (dataLine[MAG].equals("") ? 0f : Float.parseFloat(dataLine[MAG])),
                    // color index
                    (dataLine[CI].equals("") ? 0f : Float.parseFloat(dataLine[CI]))
            ));
        }
    }

}
