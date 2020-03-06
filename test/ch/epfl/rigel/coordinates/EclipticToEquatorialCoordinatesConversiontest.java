package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 05/03/2020
 */
public class EclipticToEquatorialCoordinatesConversiontest {
        @Test
        void applyWorks() {
            EclipticToEquatorialConversion eclipticToEquatorialConv =
                    new EclipticToEquatorialConversion(ZonedDateTime.of(2009, 6, 6, 14, 00, 00, 00, ZoneId.of("UTC")));
            EquatorialCoordinates ec =
                    eclipticToEquatorialConv.apply(EclipticCoordinates.of(Angle.ofDeg(139.686111), Angle.ofDeg(4.875278)));
            assertEquals(Angle.ofHr(9.581478), ec.ra(), 10E-5);
            assertEquals(Angle.ofDMS(19, 32, 6.01), ec.dec(), 0.1);
            EquatorialCoordinates ec2 =
                    eclipticToEquatorialConv.apply(EclipticCoordinates.of(Angle.ofDeg(0), Angle.ofDeg(0)));
            assertEquals(0, ec2.ra(), 10E-5);
            assertEquals(0, ec2.dec(), 0.1);
        }
}
