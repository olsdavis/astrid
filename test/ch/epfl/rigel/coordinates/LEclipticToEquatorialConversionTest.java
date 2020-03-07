package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 05/03/2020
 */
public class LEclipticToEquatorialConversionTest {
        @Test
        void applyWorks() {
            EclipticToEquatorialConversion eclipticToEquatorialConv =
                    new EclipticToEquatorialConversion(ZonedDateTime.of(2009,
                            Month.JULY.getValue(),
                            6,
                            14,
                            0,
                            0,
                            0,
                            ZoneOffset.UTC
                    ));
            EquatorialCoordinates ec =
                    eclipticToEquatorialConv.apply(EclipticCoordinates.of(Angle.ofDMS(139, 41, 10),
                            Angle.ofDMS(4, 52, 31)));
            assertEquals(Angle.ofDeg(143.722173d), ec.ra(), 10e-8);
            assertEquals(Angle.ofDMS(19, 32, 6.01d), ec.dec(), 10e-10d);
            EquatorialCoordinates ec2 =
                    eclipticToEquatorialConv.apply(EclipticCoordinates.of(Angle.ofDeg(0), Angle.ofDeg(0)));
            assertEquals(0, ec2.ra());
            assertEquals(0, ec2.dec());
        }
}
