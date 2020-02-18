package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Class containing utils methods for angle manipulations.
 */
public final class Angle {
    public static final double TAU = 2 * Math.PI;
    public static final double DEG_PER_RAD = 360/TAU;
    public static final double RAD_PER_HR=TAU/24;

    private Angle() {
    }

    /**
     * Normalizes given angles in radians
     * @param rad the angle to normalize (in radians)
     * @return an angle in radians in the interval [0;TAU)
     */
    public static double normalizePositive(double rad) {
        double remainder = rad % TAU;
        return ((remainder < 0) ? TAU - remainder : remainder);
    }

    /**
     * Converts an angle from Arcsec to radians
     * @param sec the angle to convert
     * @return the angle in radians
     */
    public static double ofArcsec(double sec) {
        double res = sec / 3600; //to convert to degrees
        res *= TAU / 360; //to convert from degrees to radians
        return normalizePositive(res);
    }

    /**
     * Conversion method from degrees, arcmins, arcsecs to radians (normalized)
     * @param deg the degree component of the angle to convert
     * @param min the arcmin component of the angle to convert
     * @param sec the arcsec component of the angle to convert
     * @return the converted angle in radians
     */
    public static double ofDMS(int deg, int min, double sec) {
        RightOpenInterval interv = RightOpenInterval.of(0, 60);
        checkArgument(interv.contains(min));
        checkArgument(interv.contains(sec));
        double res = deg + min / 60. + sec / 3600.;
        return normalizePositive(ofDeg(res));
    }

    /**
     * Conversion method from degrees to radians (normalized)
     * @param deg the given angle in degrees
     * @return the angle converted in radians
     */
    public static double ofDeg(double deg) {
        return normalizePositive(Math.toRadians(deg)); //deg *1/DEG_PER_RAD;
        //Considering the Math.toRadians method doesn't normalize angle, we are to do it ourselves
    }

    /**
     * Conversion method from radians to degrees (normalized)
     * @param rad the given angle in radians
     * @return the angle converted in degrees
     */
    public static double toDeg(double rad) {
        return Math.toDegrees(rad); //rad * DEG_PER_RAD;
    }

    public static double ofHr(double hr) {
        return normalizePositive(hr*RAD_PER_HR);
    }

    public static double toHr(double rad){
        return normalizePositive(rad)*1/RAD_PER_HR;
    }

}
