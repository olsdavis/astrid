package ch.epfl.rigel.gui;

import ch.epfl.test.TestRandomizer;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 04/04/2020
 */
public class BlackBodyColorTest {

    @Test
    void randomColorsWork() throws IOException, URISyntaxException {
        final List<String> lines = Files.readAllLines(Paths.get(getClass().getResource("/bbr_color.txt").toURI()))
                .stream().filter(s -> !s.startsWith("#") && s.contains("10deg")).collect(Collectors.toList());
        final SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final int r = random.nextInt(lines.size() - 1); // do not take the last line
            final String line = lines.get(r);
            final String nextLine = lines.get(r + 1);
            final int temperature = Integer.parseInt(line.substring(1, 6).trim());
            final Color color = Color.web(line.substring(80, 87));
            final Color nextColor = Color.web(nextLine.substring(80, 87));

            assertEquals(color, BlackBodyColor.fromTemperature(temperature));
            for (int j = 0; j < 49; j++) {
                assertEquals(color, BlackBodyColor.fromTemperature(temperature + j));
                assertEquals(nextColor, BlackBodyColor.fromTemperature(temperature + 50 + j));
            }
        }
    }

}
