package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * A Function allowing to convert {@link EclipticCoordinates} to {@link EquatorialCoordinates}.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 02/03/2020
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

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

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates e) {
        return EquatorialCoordinates.of(
                Angle.normalizePositive(Math.atan2((Math.sin(e.lon()) * cosObliqueness)
                        - (Math.tan(e.lat()) * sinObliqueness), Math.cos(e.lon()))),
                Math.asin((Math.sin(e.lat()) * cosObliqueness)
                        + (Math.cos(e.lat()) * sinObliqueness * Math.sin(e.lon())))
        );
    }

    /**
     * @throws UnsupportedOperationException this operation is forbidden.
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("unsupported operation");
    }

    /**
     * @throws UnsupportedOperationException this operation is forbidden.
     */
    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("unsupported operation");
    }

}
