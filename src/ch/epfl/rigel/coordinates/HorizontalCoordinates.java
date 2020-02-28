package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.*;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Implementation of SphericalCoordinates for the horizontal coordinates system.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 21/02/2020
 */
public final class HorizontalCoordinates extends SphericalCoordinates {

    /**
     * @param az  azimut in radians. Must be in the interval [0, 2*PI[.
     * @param alt altitude in radians. Must be in the interval [-PI/2, PI/2].
     * @return a new instance of HorizontalCoordinates based on provided parameters.
     */
    public static HorizontalCoordinates of(double az, double alt) {
        return new HorizontalCoordinates(checkInInterval(RightOpenInterval.of(0, 2 * Math.PI), az),
                checkInInterval(ClosedInterval.symmetric(Math.PI), alt));
    }

    /**
     * @param azDeg  azimut in degrees. Must be in interval [0, 360[.
     * @param altDeg altitude in degrees. Must be interval [-90, 90].
     * @return a new instance of HorizontalCoordinates based on provided parameters (converted to radians).
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        return of(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));
    }

    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    /**
     * @return azimut in radians.
     */
    public double az() {
        return lon();
    }

    /**
     * @return azimut in degrees.
     */
    public double azDeg() {
        return lonDeg();
    }

    /**
     * @return altitude in radians.
     */
    public double alt() {
        return lat();
    }

    /**
     * @return altitude in degrees.
     */
    public double altDeg() {
        return latDeg();
    }

    /**
     * @param n String indicating the North cardinal point.
     * @param e String indicating the East cardinal point.
     * @param s String indicating the South cardinal point.
     * @param w String indicating the West cardinal point.
     * @return the string corresponding to the octant in which lies the current instance's azimut.
     * Note that border coordinates are in the "next" octant (<i>e.g.</i> {@code 22.5} is {@code "NE"}).
     */
    public String azOctantName(String n, String e, String s, String w) {
        int marker = 0;
        for (int i = 0; i < 8; i++) {
            if (RightOpenInterval.of(-Math.PI / 8d + (Math.PI / 4d * i), Math.PI / 8d + (Math.PI / 4d * i)).contains(az())) {
                marker = i;
                break;
            }
        }
        switch (marker) {
            case 1:
                return n + e;
            case 2:
                return e;
            case 3:
                return s + e;
            case 4:
                return s;
            case 5:
                return s + w;
            case 6:
                return w;
            case 7:
                return n + w;
            default:
                return n;
        }
    }

    /**
     * Calculates the angular distance between the current instance and a given HorizontalCoordinates parameter.
     *
     * @param that HorizontalCoordinates object to which we measure the angular distance.
     * @return the value of the angular distance.
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        return Math.acos(Math.sin(lat()) * Math.sin(that.lat()) +
                Math.cos(lat()) * Math.cos(that.lat()) * Math.cos(lon() - that.lon()));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", lonDeg(), latDeg());
    }
}
