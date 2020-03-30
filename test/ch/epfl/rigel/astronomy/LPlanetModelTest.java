package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 26/03/2020
 */
public class LPlanetModelTest {

    @Test
    void testAt() {
        assertEquals(11.18715493470968, PlanetModel.JUPITER.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).equatorialPos().raHr(), 1e-14);

        assertEquals(6.35663550668575, PlanetModel.JUPITER.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).equatorialPos().decDeg(), 1e-14);
        assertEquals(35.11141185362771, Angle.toDeg(PlanetModel.JUPITER.at(-2231.0, new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003,
                Month.NOVEMBER, 22), LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).angularSize()) * 3600);

        assertEquals(16.8200745658971, PlanetModel.MERCURY.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).equatorialPos().raHr(), 1e-13);
        assertEquals(-24.500872462861, PlanetModel.MERCURY.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).equatorialPos().decDeg(), 1e-12);

        assertEquals(-1.9885659217834473, PlanetModel.JUPITER.at(-2231.0,
                new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                        LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).magnitude());
    }

    @Test
    void testValues() {
        assertEquals(List.of(PlanetModel.MERCURY, PlanetModel.VENUS,
                PlanetModel.EARTH, PlanetModel.MARS, PlanetModel.JUPITER,
                PlanetModel.SATURN, PlanetModel.URANUS, PlanetModel.NEPTUNE), PlanetModel.ALL);
    }

}
