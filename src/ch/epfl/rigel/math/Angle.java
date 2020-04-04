package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * Class containing utils methods for angle manipulations.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 */
public final class Angle {

    /**
     * This interval represents the possible values for minutes and seconds.
     */
    private static final Interval MIN_SEC_INTERVAL = RightOpenInterval.of(0, 60);
    /**
     * The double of {@code PI}.
     */
    public static final double TAU = 2d * Math.PI;
    /**
     * The number of radians per hour.
     */
    public static final double RAD_PER_HR = TAU / 24d;

    private Angle() {
    }

    /**
     * Normalizes given angles in radians.
     *
     * @param rad the angle to normalize (in radians)
     * @return an angle in radians in the interval {@code [0;TAU[}.
     */
    public static double normalizePositive(double rad) {
        return RightOpenInterval.of(0, TAU).reduce(rad);
    }

    /**
     * Converts an angle from Arcsec to radians.
     *
     * @param sec the angle to convert
     * @return the angle in radians.
     */
    public static double ofArcsec(double sec) {
        double res = sec / 3600d; // to convert to degrees
        res *= TAU / 360d; // to convert from degrees to radians
        return res;
    }

    /**
     * Conversion method from degrees, arcmins, arcsecs to radians (normalized).
     *
     * @param deg the degree component of the angle to convert
     * @param min the arcmin component of the angle to convert
     * @param sec the arcsec component of the angle to convert
     * @return the converted angle in radians.
     */
    public static double ofDMS(int deg, int min, double sec) {
        return ofDeg(
                deg + (checkInInterval(MIN_SEC_INTERVAL, min) / 60d) +
                        (checkInInterval(MIN_SEC_INTERVAL, sec) / 3600d)
        );
    }

    /**
     * Conversion method from degrees to radians (normalized).
     *
     * @param deg the given angle in degrees
     * @return the angle converted in radians.
     */
    public static double ofDeg(double deg) {
        return Math.toRadians(deg);
    }

    /**
     * Conversion method from radians to degrees (normalized).
     *
     * @param rad the given angle in radians
     * @return the angle converted in degrees
     */
    public static double toDeg(double rad) {
        return Math.toDegrees(rad);
    }

    /**
     * @param hr the angle to convert
     * @return the given angle in hours converted in {@code rad}.
     */
    public static double ofHr(double hr) {
        return hr * RAD_PER_HR;
    }

    /**
     * @param rad the angle to convert
     * @return the given angle in {@code rad} converted in hours.
     */
    public static double toHr(double rad) {
        return rad / RAD_PER_HR;
    }

}
