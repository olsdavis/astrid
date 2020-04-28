package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * A Function allowing converting {@link EclipticCoordinates} to {@link EquatorialCoordinates} with
 * regard to a certain moment in time.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 02/03/2020
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    /**
     * This polynomial allows the calculation of the obliqueness, used in calculations for
     * {@link #apply(EclipticCoordinates)}.
     */
    private static final Polynomial OBLIQUENESS_POLYNOMIAL = Polynomial.of(Angle.ofArcsec(0.00181d),
            Angle.ofArcsec(-0.0006d),
            Angle.ofArcsec(-46.815d),
            Angle.ofDMS(23, 26, 21.45d)
    );

    private final double sinObliqueness;
    private final double cosObliqueness;

    /**
     * @param when the date of reference for the conversions
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        final double obliqueness = OBLIQUENESS_POLYNOMIAL.at(Epoch.J2000.julianCenturiesUntil(when));
        cosObliqueness = Math.cos(obliqueness);
        sinObliqueness = Math.sin(obliqueness);
    }

    /**
     * @param e the coordinates to convert
     * @return the provided coordinates {@code e} converted to {@link EquatorialCoordinates},
     * according to the moment of time provided in the constructor.
     *
     * @see EclipticToEquatorialConversion#EclipticToEquatorialConversion(ZonedDateTime)
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates e) {
        final double lonSin = Math.sin(e.lon());
        return EquatorialCoordinates.of(
                Angle.normalizePositive(Math.atan2((lonSin * cosObliqueness)
                        - (Math.tan(e.lat()) * sinObliqueness), Math.cos(e.lon()))),
                Math.asin((Math.sin(e.lat()) * cosObliqueness)
                        + (Math.cos(e.lat()) * sinObliqueness * lonSin))
        );
    }

    /**
     * @throws UnsupportedOperationException this operation is forbidden.
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("tried to call hashCode on EclipticToEquatorialConversion");
    }

    /**
     * @throws UnsupportedOperationException this operation is forbidden.
     */
    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("tried to call equals on EclipticToEquatorialConversion");
    }

}
