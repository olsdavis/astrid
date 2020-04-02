package ch.epfl.rigel;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * @author Oscar Davis
 * Creation date: 02/04/2020
 */
public class ShareTest {

    private static final String HYG_CATALOGUE_NAME = "/hygdata_v3.csv";
    private static final String ASTERISMS_FILE = "/asterisms.txt";

    private static BufferedWriter writer;

    @BeforeAll
    static void setUp() throws IOException {
        final File file = new File("out.txt");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        writer = new BufferedWriter(new FileWriter(file));
    }

    @AfterAll
    static void finish() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }

    @Test
    void testMoonModel() throws IOException {
        writer.write("\t\t--- MOON MODEL ---\n");
        for (int i = 0; i < 2000; i++) {
            ZonedDateTime date = ZonedDateTime.of(
                    LocalDate.of(i, (i % 12) + 1, i % 28 + 1),
                    LocalTime.of(i % 24, i % 60, i % 60),
                    ZoneOffset.UTC
            );
            Moon moon = MoonModel.MOON.at(Epoch.J2010.daysUntil(date), new EclipticToEquatorialConversion(date));
            writer.write(moon.info() + "," + moon.angularSize() + "," + moon.equatorialPos().ra() + "," + moon.equatorialPos().dec());
            writer.newLine();
        }
    }

    @Test
    void testLoaders() throws IOException {
        writer.write("\t\t--- STARS OF CATALOGUE ---\n");
        StarCatalogue catalogue = new StarCatalogue.Builder()
                .loadFrom(getClass().getResourceAsStream(HYG_CATALOGUE_NAME), HygDatabaseLoader.INSTANCE)
                .loadFrom(getClass().getResourceAsStream(ASTERISMS_FILE), AsterismLoader.INSTANCE)
                .build();
        for (Star star : catalogue.stars()) {
            writer.write(String.format("%s,%d,%d,%f,%f,%f\n",
                    star.name(),
                    star.hipparcosId(),
                    star.colorTemperature(),
                    star.angularSize(),
                    star.equatorialPos().ra(),
                    star.equatorialPos().dec()));
        }
        writer.write("\t\t--- ASTERISMS OF CATALOGUE ---\n");
        for (Asterism asterism : catalogue.asterisms()) {
            writer.write(asterism.stars().toString() + "\n");
        }
        writer.write("\t\t--- asterismIndices ---\n");
        for (Asterism asterism : catalogue.asterisms()) {
            writer.write(catalogue.asterismIndices(asterism).toString() + "\n");
        }
    }

}
