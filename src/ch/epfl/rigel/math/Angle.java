package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Class containing utils methods for angle manipulations.
 */
public final class Angle {
    public static final double TAU = 2 * Math.PI;
    public static final double RAD_PER_HR = TAU / 24;

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
        double res = sec / 3600; // to convert to degrees
        res *= TAU / 360; // to convert from degrees to radians
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
        RightOpenInterval interv = RightOpenInterval.of(0, 60);
        checkArgument(interv.contains(min));
        checkArgument(interv.contains(sec));
        double res = deg + (min / 60d) + (sec / 3600d);
        return ofDeg(res);
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
