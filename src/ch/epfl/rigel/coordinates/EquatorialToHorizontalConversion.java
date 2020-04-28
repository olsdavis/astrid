package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;

import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * A Function allowing converting {@link EquatorialCoordinates} to {@link HorizontalCoordinates} with regard
 * to a certain moment in time and a position on Earth.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 02/03/2020
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double sidereal;
    private final double sinLat;
    private final double cosLat;

    /**
     * @param when  the date of reference for the conversion
     * @param where the {@link GeographicCoordinates} of the observer
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        sinLat = Math.sin(where.lat());
        cosLat = Math.cos(where.lat());
        sidereal = SiderealTime.local(when, where);
    }

    /**
     * @param e The coordinates to convert
     * @return the converted coordinates in {@link HorizontalCoordinates} according to the
     * moment in time and the position, provided in the constructor.
     *
     * @see EquatorialToHorizontalConversion#EquatorialToHorizontalConversion(ZonedDateTime, GeographicCoordinates)
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates e) {
        final double hourAngle = sidereal - e.ra();
        final double sinDec = Math.sin(e.dec());
        final double cosDec = Math.cos(e.dec());
        final double alt = Math.asin(sinDec * sinLat + cosDec * cosLat * Math.cos(hourAngle));
        return HorizontalCoordinates.of(
                Angle.normalizePositive(Math.atan2(-cosDec * cosLat * Math.sin(hourAngle), sinDec - sinLat * Math.sin(alt))),
                alt
        );
    }

    /**
     * @throws UnsupportedOperationException this operation is forbidden.
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("tried to call hashCode on EquatorialToHorizontalConversion");
    }

    /**
     * @throws UnsupportedOperationException this operation is forbidden.
     */
    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("tried to call equals on EquatorialToHorizontalConversion");
    }

}
