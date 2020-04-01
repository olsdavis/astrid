package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 01/04/2020
 */
public class LHygDatabaseLoaderTest {

    private static final String HYG_CATALOGUE_NAME = "/hygdata_v3.csv";

    /**
     * @param catalogue the catalogue to search from
     * @param name      the name of the star to look up
     * @return the provided the star in the given catalogue, or {@code null} if not found.
     */
    private static Star findByName(StarCatalogue catalogue, String name) {
        return catalogue.stars().stream().filter(s -> s.name().equals(name)).findAny().orElse(null);
    }

    /**
     * @param str the String to cast to a float
     * @return the provided String cast to a float, or {@code 0} if the String is empty.
     */
    private static float toFloat(String str) {
        if (str.equals("")) {
            return 0f;
        }
        return Float.parseFloat(str);
    }

    /**
     * @param str the String to cast to a double
     * @return the provided String cast to a double, or {@code 0} if the String is empty.
     */
    private static double toDouble(String str) {
        if (str.equals("")) {
            return 0d;
        }
        return Double.parseDouble(str);
    }

    /**
     * @param str the String to cast to an integer
     * @return the provided String cast to an integer, or {@code 0} if the String is empty.
     */
    private static int toInt(String str) {
        if (str.equals("")) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    /**
     * @param proper the proper name of the star
     * @param bayer  the bayer designation of the star
     * @param con    the shortened name of the constellation
     * @return the the name of the star, as it should be built by {@link HygDatabaseLoader}.
     */
    private static String buildName(String proper, String bayer, String con) {
        return proper.equals("")
                ? (bayer.equals("") ? "?" : bayer) + " " + con
                : proper;
    }

    /**
     * @param colorIndex the color index of a star
     * @return the color temperature that a star of the given {@code colorIndex} should return.
     */
    private static int calculateTemperature(float colorIndex) {
        return new Star(0, "", EquatorialCoordinates.of(0, 0), 0, colorIndex).colorTemperature();
    }

    private StarCatalogue catalogue;

    private StarCatalogue loadWithBuilder() throws IOException {
        StarCatalogue.Builder builder = new StarCatalogue.Builder();
        return builder
                .loadFrom(getClass().getResourceAsStream(HYG_CATALOGUE_NAME), HygDatabaseLoader.INSTANCE)
                .build();
    }

    @BeforeEach
    void setUp() throws IOException {
        catalogue = loadWithBuilder();
    }

    @Test
    void betelgeuseCorrectData() {
        Star betelgeuse = findByName(catalogue, "Betelgeuse");
        assertNotNull(betelgeuse);
        assertEquals(27989, betelgeuse.hipparcosId());
        assertEquals(1.5497291183713153, betelgeuse.equatorialPos().ra());
        assertEquals(0.12927763169419373, betelgeuse.equatorialPos().dec());
        assertEquals(0.450f, (float) betelgeuse.magnitude());
    }

    @Test
    void rigelCorrectData() {
        Star rigel = findByName(catalogue, "Rigel");
        assertNotNull(rigel);
        assertEquals(24436, rigel.hipparcosId());
        assertEquals(1.3724303693276385, rigel.equatorialPos().ra());
        assertEquals(-0.143145630755865, rigel.equatorialPos().dec());
        assertEquals(0.180f, (float) rigel.magnitude());
    }

    @Test
    void firstStarCorrectData() {
        Star first = findByName(catalogue, "Tau Phe"); // default name: <bayer> (?) + " " + <con>
        assertNotNull(first);
        assertEquals(88, first.hipparcosId());
        assertEquals(0.004696959812148889, first.equatorialPos().ra());
        assertEquals(-0.8518930353430763, first.equatorialPos().dec());
        assertEquals(5.710f, (float) first.magnitude());
    }

    @Test
    void lastStarCorrectData() {
        Star last = catalogue.stars().stream()
                .filter(s -> s.name().equals("? Aqr") && s.hipparcosId() == 0).findFirst().orElse(null);
        assertNotNull(last);
        assertEquals(-0.3919549465551, last.equatorialPos().dec());
        assertEquals(6.064662769813043, last.equatorialPos().ra());
        assertEquals(5.900f, (float) last.magnitude());
    }

    @Test
    void randomStarsCorrectData() throws IOException, URISyntaxException {
        SplittableRandom random = TestRandomizer.newRandom();
        final List<String> lines = Files
                .readAllLines(Paths.get(getClass().getResource(HYG_CATALOGUE_NAME).toURI()),
                        StandardCharsets.US_ASCII);
        for (int i = 0; i < Math.min(TestRandomizer.RANDOM_ITERATIONS, lines.size()); i++) {
            final int x = random.nextInt(1, lines.size() + 1);
            final String[] data = lines.get(x).split(",");
            final Star star = catalogue.stars().stream()
                    .filter(s -> s.hipparcosId() == toInt(data[1])
                            && (float) s.magnitude() == toFloat(data[13])
                            && s.equatorialPos().ra() == toDouble(data[23])
                            && s.equatorialPos().dec() == toDouble(data[24])
                            && s.name().equals(buildName(data[6], data[27], data[29]))
                            && s.colorTemperature() == calculateTemperature(toFloat(data[16])))
                    .findAny().orElse(null);
            assertNotNull(star);
        }
    }

}
