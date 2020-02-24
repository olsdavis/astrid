package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Implementation of SphericalCoordinates for the horizontal coordinates system.
 *
 * @author Alexandre Doukhan (SCIPER : 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 21/02/2020
 **/
public final class HorizontalCoordinates extends SphericalCoordinates {

    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    /**
     * Public method for initialization.
     *
     * @param az  azimut (longitude) in radians.
     * @param alt (latitude) in radians.
     * @return a new instance of HorizontalCoordinates with given parameters.
     */
    public static HorizontalCoordinates of(double az, double alt) {
        Preconditions.checkInInterval(RightOpenInterval.of(0, 2 * Math.PI), az);
        Preconditions.checkInInterval(RightOpenInterval.symmetric(Math.PI), alt);
        return new HorizontalCoordinates(az, alt);
    }

    /**
     * Public method for initialization.
     *
     * @param azDeg  Azimut (longitude) in degrees
     * @param altDeg Altitude (latitude) in degrees
     * @return a new instance of HorizontalCoordinates with given parameters converted to radians.
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        return HorizontalCoordinates.of(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));
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
     */
    public String azOctantName(String n, String e, String s, String w) {
        double redAz = this.az() * 8 / Math.PI;
        int marker = 0;
        for (int i = 1; i < 8; i++) {
            if (RightOpenInterval.of(i, i + 1).contains(redAz)) {
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
        return String.format(Locale.ROOT, "(az=%.6f° , alt=%.6f°)", lonDeg(), latDeg());
    }
}
