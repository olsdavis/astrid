package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.coordinates.GeographicCoordinates;

import java.time.LocalTime;
import java.time.ZonedDateTime;

import static ch.epfl.rigel.astronomy.RiseSet.*;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 03/06/2020
 */
public class SunRiseSet {

    private SunRiseSet() {
    }

    /**
     * Determines the hour of the day at which the sun is at its zenith.
     * This method operates in total abstraction of daylight saving schemes and timezones interferences.
     * We therefore consider the zenith to be equidistant in time to the sunrise and sunset.
     *
     * @param sun      the Sun.
     * @param position the position at which we observe.
     * @param when     the date at which we observe.
     * @return the time of the zenith.
     */
    public static LocalTime zenithTime(Sun sun, GeographicCoordinates position, ZonedDateTime when) {
        final EquatorialCoordinates coordinates = sun.equatorialPos();
        LocalTime rise = RiseSet.rise(coordinates, position, when);
        LocalTime set = RiseSet.rise(coordinates, position, when);

        double diffHour = set.getHour() - rise.getHour();
        final double bonusMinutes = (int) diffHour % 2 == 0 ? 0 : 30;
        diffHour -= 0.5 * (diffHour % 2);
        diffHour /= 2;

        double diffMinutes = set.getMinute() - rise.getMinute() + bonusMinutes;
        final double bonusSeconds = (int) diffMinutes % 2 == 0 ? 0 : 30;
        diffMinutes -= 0.5 * (diffMinutes % 2);
        diffMinutes /= 2;

        double diffSeconds = set.getSecond() - rise.getSecond() + bonusSeconds;
        diffSeconds -= 0.5 * (diffSeconds % 2);
        diffSeconds /= 2;

        return LocalTime.of((int) diffHour, (int) diffMinutes, (int) diffSeconds);
    }

    /**
     * Gives the time at which the sun rises on a given day, at a given position.
     *
     * @param sun      the Sun.
     * @param position the position at which we observe.
     * @param when     the date at which we observe.
     * @return the time of the sunrise.
     */
    public static LocalTime sunrise(Sun sun, GeographicCoordinates position, ZonedDateTime when) {
        return RiseSet.rise(sun.equatorialPos(), position, when);
    }

    /**
     * Gives the time at which the sun sets on a given day, at a given position.
     *
     * @param sun      the Sun.
     * @param position the position at which we observe.
     * @param when     the date at which we observe.
     * @return the time of the sunset.
     */
    public static LocalTime sunset(Sun sun, GeographicCoordinates position, ZonedDateTime when) {
        return RiseSet.set(sun.equatorialPos(), position, when);
    }

}
