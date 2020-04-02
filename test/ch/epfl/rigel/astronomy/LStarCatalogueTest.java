package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 01/04/2020
 */
public class LStarCatalogueTest {

    private static final String HYG_CATALOGUE_NAME = "/hygdata_v3.csv";
    private static final String ASTERISMS_FILE = "/asterisms.txt";

    @Test
    void constructorFails() {
        Star s1 = new Star(1, "Hello", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Star s2 = new Star(2, "World", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Star s3 = new Star(3, "Gros", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Asterism asterism = new Asterism(Arrays.asList(s1, s2, s3));
        assertThrows(IllegalArgumentException.class, () -> new StarCatalogue(Arrays.asList(s1, s3),
                Collections.singletonList(asterism)));
        assertThrows(IllegalArgumentException.class, () -> new StarCatalogue(List.of(),
                Collections.singletonList(asterism)));
    }

    @Test
    void gettersWork() {
        Star s1 = new Star(1, "Hello", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Star s2 = new Star(2, "World", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Star s3 = new Star(3, "Gros", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Asterism a1 = new Asterism(Arrays.asList(s2, s3));
        Asterism a2 = new Asterism(Arrays.asList(s1, s3));
        StarCatalogue catalogue = new StarCatalogue(Arrays.asList(s1, s2, s3),
                Arrays.asList(a1, a2));
        assertEquals(Arrays.asList(s1, s2, s3), catalogue.stars());
        List<Asterism> asterisms = Arrays.asList(a1, a2);
        assertTrue(asterisms.containsAll(catalogue.asterisms())
                && catalogue.asterisms().containsAll(asterisms));
    }

    @Test
    void asterismIndicesFails() {
        Star s1 = new Star(1, "Hello", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Star s2 = new Star(2, "World", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Star s3 = new Star(3, "Gros", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Asterism a1 = new Asterism(Arrays.asList(s2, s3));
        Asterism a2 = new Asterism(Arrays.asList(s1, s3));
        StarCatalogue catalogue = new StarCatalogue(Arrays.asList(s1, s2, s3),
                Collections.singletonList(a1));
        assertThrows(IllegalArgumentException.class, () -> catalogue.asterismIndices(a2));
    }

    @Test
    void asterismIndicesWorksOnTrivialCases() {
        Star s1 = new Star(1, "Hello", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Star s2 = new Star(2, "World", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Star s3 = new Star(3, "Gros", EquatorialCoordinates.of(0, 0), 0f, 0f);
        Asterism a1 = new Asterism(Arrays.asList(s2, s3));
        Asterism a2 = new Asterism(Arrays.asList(s1, s3));
        StarCatalogue catalogue = new StarCatalogue(Arrays.asList(s1, s2, s3),
                Arrays.asList(a1, a2));

        assertEquals(Arrays.asList(1, 2), catalogue.asterismIndices(a1));
        assertEquals(Arrays.asList(0, 2), catalogue.asterismIndices(a2));

        Asterism trivial = new Asterism(Collections.singletonList(s1));
        catalogue = new StarCatalogue(Collections.singletonList(s1), Collections.singletonList(trivial));
        assertEquals(Collections.singletonList(0), catalogue.asterismIndices(trivial));
    }

    @Test
    void asterismIndicesWorks() {
        Map<Object, Object> stars = new HashMap<>();
        StarCatalogue.Builder builder = new StarCatalogue.Builder();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            Star s = new Star(10 * i + 1, "HoHoHoHo" + i, EquatorialCoordinates.of(0, 0), 0f, 0f);
            stars.put(i, s);
            stars.put(s, i);
            builder.addStar(s);
        }
        SplittableRandom random = TestRandomizer.newRandom();
        List<Star> currentStars = new ArrayList<>();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            if (random.nextInt(10) % 4 == 0 && !currentStars.isEmpty()) {
                builder.addAsterism(new Asterism(currentStars));
                currentStars.clear();
            } else {
                currentStars.add((Star) stars.get(i));
            }
        }
        if (!currentStars.isEmpty()) {
            builder.addAsterism(new Asterism(currentStars));
        }

        StarCatalogue catalogue = builder.build();
        for (Asterism asterism : catalogue.asterisms()) {
            final List<Integer> indices = catalogue.asterismIndices(asterism);
            final List<Integer> trueIndices = new ArrayList<>(asterism.stars().size());
            for (Star star : asterism.stars()) {
                trueIndices.add((int) stars.get(star));
            }
            assertEquals(trueIndices, indices);
        }
    }

    @Test
    void asterismIndicesIsOrderedCorrectly() throws IOException {
        StarCatalogue catalogue = new StarCatalogue.Builder()
                .loadFrom(getClass().getResourceAsStream(HYG_CATALOGUE_NAME), HygDatabaseLoader.INSTANCE)
                .loadFrom(getClass().getResourceAsStream(ASTERISMS_FILE), AsterismLoader.INSTANCE)
                .build();
        for (Asterism asterism : catalogue.asterisms()) {
            final List<Integer> indices = catalogue.asterismIndices(asterism);
            assertNotEquals(List.of(), indices);
            for (int i = 0; i < indices.size(); i++) {
                assertEquals(asterism.stars().get(i), catalogue.stars().get(indices.get(i)));
            }
        }
    }

    @Test
    void builderReturnsView() {
        assertThrows(UnsupportedOperationException.class,
                () -> new StarCatalogue.Builder().stars().add(null));
        assertThrows(UnsupportedOperationException.class,
                () -> new StarCatalogue.Builder().asterisms().add(null));
    }

}
