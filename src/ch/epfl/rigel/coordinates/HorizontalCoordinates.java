package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.*;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Interval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Implementation of SphericalCoordinates for the horizontal system of coordinates.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 21/02/2020
 */
public final class HorizontalCoordinates extends SphericalCoordinates {

    /**
     * This interval represents the possible values for an azimuth (in radians).
     */
    private static final Interval AZ_INTERVAL = RightOpenInterval.of(0, 2 * Math.PI);
    /**
     * This interval represents the possible values for an altitude (in radians).
     */
    private static final Interval ALT_INTERVAL = ClosedInterval.symmetric(Math.PI);

    /**
     * @param az  azimuth in radians. Must be in the interval [0, 2*PI[.
     * @param alt altitude in radians. Must be in the interval [-PI/2, PI/2].
     * @return a new instance of HorizontalCoordinates based on provided parameters.
     * @throws IllegalArgumentException if {@code az} or {@code alt} is not
     *                                  in the right interval
     */
    public static HorizontalCoordinates of(double az, double alt) {
        return new HorizontalCoordinates(checkInInterval(AZ_INTERVAL, az),
                checkInInterval(ALT_INTERVAL, alt));
    }

    /**
     * @param azDeg  the azimuth in degrees. Must be in interval [0, 360[.
     * @param altDeg the altitude in degrees. Must be interval [-90, 90].
     * @return a new instance of HorizontalCoordinates based on provided parameters (converted to radians).
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        return of(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));
    }

    /**
     * @param az the azimuth (in radians)
     * @param alt the altitude (in radians)
     */
    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    /**
     * @return the azimuth in radians.
     */
    public double az() {
        return lon();
    }

    /**
     * @return the azimuth in degrees.
     */
    public double azDeg() {
        return lonDeg();
    }

    /**
     * @return the altitude in radians.
     */
    public double alt() {
        return lat();
    }

    /**
     * @return the altitude in degrees.
     */
    public double altDeg() {
        return latDeg();
    }

    /**
     * @param n a String representing the North cardinal point
     * @param e a String representing the East cardinal point
     * @param s a String representing the South cardinal point
     * @param w a String representing the West cardinal point
     * @return the String corresponding to the octant in which lies the current instance's azimuth.
     * Note that border coordinates are in the "next" octant (<i>e.g.</i> {@code 22.5}° is {@code "NE"}).
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
     * Calculates the angular distance between the current coordinates and the provided ones.
     *
     * @param that the HorizontalCoordinates of the object to which we measure the angular distance.
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
