package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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

    /**
     * Represents the average number of days per year (according to <a href="https://pumas.nasa.gov/files/04_21_97_1.pdf">NASA</a>).
     */
    private static final double DAYS_PER_YEAR = 365.2425;

    /**
     * Represents the average number of days per month {@code DAYS_PER_YEAR /12};
     */
    private static final double DAYS_PER_MONTH = 30.44;

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
     * Utility method to convert from Local Sidereal Time to a Universal Time.
     *
     * @param when the local time to be converted
     * @return the universal time to be used for {@code rise} and {@code set}.
     */
    private static double lstToUt(ZonedDateTime when) {
        double JD = localDateToJulianDate(when);
        double S = Epoch.J2010.daysUntil(when) - DAYS_TO_J2010;
        double T = S / Epoch.DAYS_PER_JULIAN_CENTURY;
        double T_0 = HOUR_INTERVAL.reduce(P_O.at(T));
        double B = HOUR_INTERVAL.reduce(JD - T_0);
        return B * SIDEREAL_DAYS_PER_DAY;
    }

    private static double localDateToJulianDate(ZonedDateTime when) {
        ZonedDateTime gmt = localDateToGMT(when);
        int year = gmt.getYear();
        int month = gmt.getMonthValue();
        int day = gmt.getDayOfMonth();
        int A = year/100;
        int B;
        int C;
        int D;


        if(month==1 || month==2) {
            year = year -1;
            month = month + 12;
        }

        B = (year>=1582 && month >= 8 && day>15) ? 2-A + (A/4) : 0;

        C = year<0 ? (int) ((DAYS_PER_YEAR * year) - 0.75) : (int) (DAYS_PER_YEAR * year);

        D = (int) (30.60001 * month+1);

        return B + C + D + day + DAYS_TO_J2010;
    }


    private static ZonedDateTime localDateToGMT(ZonedDateTime when) {
        LocalTime offset = getHourOffset(when.getOffset());
        LocalDate date = when.toLocalDate();
        //Note this is an average based on years. There might be some small errors due to leap years.
        double hours = date.getYear() * DAYS_PER_YEAR + date.getMonthValue() * DAYS_PER_MONTH + date.getDayOfMonth() * 24
                + when.getHour() + when.getMinute() / 60d + when.getSecond() / 3600d;
        //Subtract the offset in hours, minutes, seconds, from the initial date.
        hours -= (offset.getHour() + offset.getMinute()/60d + offset.getSecond() / 3600d);
        double totalDays = hours / 24d;

        int year = (int) (totalDays/DAYS_PER_YEAR);
        int month = (int) ((totalDays - (int) (year*DAYS_PER_YEAR))  /DAYS_PER_MONTH) ;
        int day = (int) (totalDays - (int) (year*DAYS_PER_YEAR) -(int) (month * DAYS_PER_MONTH));
        double fracHour = (totalDays - (int) totalDays) * 24;
        LocalTime time = fromDouble(fracHour);
        //The choice here to put UTC as the time zone comes from the JavaDoc of the class ZoneId (GMT is equivalent to UTC).
        return ZonedDateTime.of(LocalDate.of(year, month, day), time, ZoneOffset.UTC);
    }

    private static LocalTime getHourOffset(ZoneOffset offset) {
        int totalSeconds = offset.getTotalSeconds();
        double totalHours = totalSeconds/3600d;
        return  fromDouble(totalHours);
    }


}
