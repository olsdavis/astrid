package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 04/04/2020
 */
public class ObservedSkyTest {

    @Test
    void testStarsDistance() throws IOException {
        new ObservedSky(ZonedDateTime.now(), GeographicCoordinates.ofDeg(0, 0),
                new StereographicProjection(HorizontalCoordinates.of(0, 0)),
                new StarCatalogue.Builder()
                    .loadFrom(getClass().getResourceAsStream("/hygdata_v3.csv"), HygDatabaseLoader.INSTANCE)
                    .build()
        );
    }

}
