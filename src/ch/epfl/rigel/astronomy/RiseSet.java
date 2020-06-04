package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

import java.time.*;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 01/06/2020
 */
public class RiseSet {

    /**
     * Represents the interval in which our cosinus values are supposed to lie in.
     */
    private static final ClosedInterval COS_INTERVAL = ClosedInterval.of(-1, 1);

    /*
     * Represents the interval in which the hours has to lie.
     */
    private static final RightOpenInterval HOUR_INTERVAL = RightOpenInterval.of(0, 24);

    /**
     * Represents the interval in which the months have to lie.
     */
    private static final ClosedInterval MONTH_INTERVAL = ClosedInterval.of(0, 12);


    /**
     * Represents the number of days from the start of the julian calendar and the {@code Epoch.J2010}.
     */
    private static final double DAYS_TO_J2010 = 1720994.5;

    /**
     * Represents the number of sidereal days in a day.
     */
    private static final double SIDEREAL_DAYS_PER_DAY = 0.9972695663;


    /**
     * Represents the polynomial used in GST to UT conversion.
     */
    private static final Polynomial P_O = Polynomial.of(0.000025862, 2400.051336, 6.697374558);


    private RiseSet() {
    }


    /**
     * Method to find the hour at which a celestial object rises given its coordinates, the position of the observer, and a date.
     *
     * @param coordinates the coordinates of the celestial object.
     * @param position    the position of the observer.
     * @param when        the date and hour at which we want to observe
     * @return an instance of {@code LocalTime} which corresponds to the hour at which the object rises.
     */
    static LocalTime rise(EquatorialCoordinates coordinates, GeographicCoordinates position, ZonedDateTime when) {
        final double cosH = RiseSet.cosH(coordinates, position);
        //Specific unreachable dates are chosen to manage exceptions in each case.
        if (!(COS_INTERVAL.contains(cosH))) {
            if (cosH > 1) {
                return LocalTime.of(23, 59, 59);
            } else if (cosH < -1) {
                return LocalTime.of(0, 0, 0);
            }
        }
        final double H = Math.acos(cosH);
        final double LST = HOUR_INTERVAL.reduce(coordinates.ra() - H);
        ZonedDateTime GST = ZonedDateTime.of(when.toLocalDate(), fromDouble(LST), when.getZone());
        return fromDouble(lstToUt(GST));
    }

    /**
     * Method to find the hour at which a celestial object sets given its coordinates, the position of the observer, and a date.
     *
     * @param coordinates the coordinates of the celestial object.
     * @param position    the position of the observer.
     * @param when        the date and hour at which we want to observe
     * @return an instance of {@code LocalTime} which corresponds to the hour at which the object sets.
     */
    static LocalTime set(EquatorialCoordinates coordinates, GeographicCoordinates position, ZonedDateTime when) {
        double cosH = RiseSet.cosH(coordinates, position);
        if (!(COS_INTERVAL.contains(cosH))) {
            if (cosH > 1) {
                return LocalTime.of(23, 59, 59);
            } else if (cosH < -1) {
                return LocalTime.of(0, 0, 0);
            }
        }
        double H = Math.acos(cosH);
        final double LST = HOUR_INTERVAL.reduce(coordinates.ra() + H);
        ZonedDateTime UT = ZonedDateTime.of(when.toLocalDate(), fromDouble(LST), when.getZone());
        return fromDouble(lstToUt(UT));
    }


    /**
     * @param coordinates the coordinates of the celestial object we wish to find the cosH for.
     * @param position    the coordinates of the observer on Earth.
     * @return the cosinus of the hour angle.
     */
    private static double cosH(EquatorialCoordinates coordinates, GeographicCoordinates position) {
        return -(Math.tan(position.lat()) * Math.tan(coordinates.dec()));
    }


    /**
     * Utility method to convert from Local Sidereal Time to a Universal Time.
     *
     * @param when the local time to be converted
     * @return the universal time to be used for {@code rise} and {@code set}.
     */
    private static double lstToUt(ZonedDateTime when) {
        double JD = (int) (Epoch.J2010.daysUntil(when)) + 0.5d + DAYS_TO_J2010;
        double S = JD - DAYS_TO_J2010;
        double T = S / Epoch.DAYS_PER_JULIAN_CENTURY;
        double T_0 = HOUR_INTERVAL.reduce(P_O.at(T));
        double B = HOUR_INTERVAL.reduce(toDecimalHour(when.toLocalTime()) - T_0);
        return B * SIDEREAL_DAYS_PER_DAY;
    }

    /**
     * Utility method to convert to a {@code LocalTime} from a decimal hour.
     *
     * @param fracHour the fractional / decimal hour we wish to convert
     * @return a new instance of {@code LocalTime} which corresponds to the parameter.
     */
    private static LocalTime fromDouble(double fracHour) {
        final int hours = (int) fracHour;
        final double fracMinutes = fracHour - hours;
        final int minutes = (int) (60 * fracMinutes);
        final int seconds = (int) (60 * (60 * fracMinutes - minutes));
        return LocalTime.of(hours, minutes, seconds);
    }

    /**
     * Utility method to convert from a real hh mm ss to a decimal hour.
     * @param time the hour to convert
     * @return the decimal hour
     */
    private static double toDecimalHour(LocalTime time) {
        return time.getHour() + time.getMinute() / 60d + time.getSecond() / 3600d;
    }


}
