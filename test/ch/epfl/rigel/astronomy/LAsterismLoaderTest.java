package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 01/04/2020
 */
public class LAsterismLoaderTest {

    private static final String ASTERISMS_FILE = "/asterisms.txt";
    private static final String HYG_CATALOGUE_NAME = "/hygdata_v3.csv";

    // see LHygDatabaseLoaderTest for information about the static modifier
    private static StarCatalogue catalogue;

    /**
     * @param hipparcos the hipparcos id of the star to look up
     * @return the star of the given {@code hipparcos} Hipparcos ID, or {@code null} if not found.
     */
    private static Star findByHipparcos(int hipparcos) {
        return catalogue.stars().stream().filter(s -> s.hipparcosId() == hipparcos).findAny().orElse(null);
    }

    // see LHygDatabaseLoaderTest for information about the static modifier
    @BeforeAll
    static void setUp() throws IOException {
        catalogue = new StarCatalogue.Builder()
                .loadFrom(LAsterismLoaderTest.class.getResourceAsStream(HYG_CATALOGUE_NAME), HygDatabaseLoader.INSTANCE)
                .loadFrom(LAsterismLoaderTest.class.getResourceAsStream(ASTERISMS_FILE), AsterismLoader.INSTANCE)
                .build();
    }

    @Test
    void allStarsInAsterismsExist() {
        catalogue.asterisms()
                .stream()
                .map(a -> catalogue.asterismIndices(a))
                .forEach(s -> s.stream().map(catalogue.stars()::get).forEach(Assertions::assertNotNull));
    }

    @Test
    void bigQuestion() throws IOException, URISyntaxException {
        final List<String> lines = Files
                .readAllLines(Paths.get(getClass().getResource(ASTERISMS_FILE).toURI()),
                        StandardCharsets.US_ASCII);
        Set<Integer> asterismsHipparcos = new HashSet<>();
        for (String line : lines) {
            Arrays.stream(line.split(",")).map(Integer::parseInt).forEach(asterismsHipparcos::add);
        }
        Set<Integer> available = catalogue.stars().stream().map(Star::hipparcosId).collect(Collectors.toSet());
        assertTrue(available.containsAll(asterismsHipparcos));
    }

    @Test
    void allAsterismsExistAndHaveTheRightStars() throws IOException, URISyntaxException {
        final List<String> lines = Files
                .readAllLines(Paths.get(getClass().getResource(ASTERISMS_FILE).toURI()),
                        StandardCharsets.US_ASCII);
        Map<Star, Integer> indicesMap = new HashMap<>();
        for (int i = 0; i < catalogue.stars().size(); i++) {
            indicesMap.put(catalogue.stars().get(i), i);
        }
        for (String line : lines) {
            final List<Integer> hipIndices = Arrays.stream(line.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toUnmodifiableList());
            final List<Star> allStars = hipIndices.stream()
                    .map(LAsterismLoaderTest::findByHipparcos)
                    .collect(Collectors.toList());
            final List<Integer> allIds = allStars.stream()
                    .map(indicesMap::get)
                    .collect(Collectors.toList());
            Asterism asterism = catalogue.asterisms().stream()
                    .filter(s -> s.stars().equals(allStars))
                    .findAny()
                    .orElse(null);
            assertNotNull(asterism);
            assertEquals(hipIndices.size(), catalogue.asterismIndices(asterism).size());
            assertEquals(allIds, catalogue.asterismIndices(asterism));
        }
    }

}
