package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the sky at a certain moment in time, containing
 * celestial objects projected on a plan with stereographic projection.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 04/04/2020
 */
public class BlackBodyColor {

    /**
     * This constant allows us to skip lines of the file that are not in "2deg".
     *
     * @see #loadFile()
     */
    private static final String DEG_10 = "10deg";
    /**
     * This is the mapping of temperatures to their colors.
     *
     * @see #fromTemperature(int)
     */
    private static final Map<Integer, Color> COLOR_MAP = new HashMap<>();

    static {
        loadFile();
    }

    /**
     * @param temperature the temperature, in Kelvins, between 1000K and 40_000K
     * @return the color associated to the provided {@code temperature} if there is such
     * a map for the provided color; otherwise, rounds to the closest 100 multiple.
     */
    public static Color fromTemperature(int temperature) {
        Preconditions.checkArgument(temperature >= 1000 && temperature <= 40_000);

        if (COLOR_MAP.containsKey(temperature)) {
            return COLOR_MAP.get(temperature);
        } else {
            return COLOR_MAP.get(100 * Math.round(temperature / 100f));
        }
    }

    /**
     * Loads the file containing the color mapping. Can throw an {@link UncheckedIOException}.
     */
    private static void loadFile() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(BlackBodyColor.class.getResourceAsStream("/bbr_color.txt")))) {
            String line;
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                // if it is a comment or not in 10deg mode
                if (line.charAt(0) == '#' || !DEG_10.equals(line.substring(10, 15))) {
                    continue;
                }
                final int temperature = Integer.parseInt(line.substring(1, 6).trim()); // remove trailing white spaces
                final String hex = line.substring(80, 87);
                COLOR_MAP.put(temperature, Color.web(hex));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private BlackBodyColor() {
    }

}
