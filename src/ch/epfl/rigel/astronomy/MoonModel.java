package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.*;

/**
 * Solely holds the model of the moon.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 28/03/2020
 */
public enum MoonModel implements CelestialObjectModel<Moon> {

    MOON;

    /**
     * The mean longitude.
     */
    private static final double L_0 = Angle.ofDeg(91.929336);
    /**
     * The mean longitude at the perigee.
     */
    private static final double P_0 = Angle.ofDeg(130.143076);
    /**
     * The longitude of the ascending node.
     */
    private static final double N_0 = Angle.ofDeg(291.682547);
    /**
     * The inclination of the orbital.
     */
    private static final double I = Angle.ofDeg(5.145396);
    /**
     * The eccentricity of the orbital.
     */
    private static final double E = 0.0549d;

    /*
     * The following values are unnamed constants provided in the instructions.
     * Since no name was provided, they are here stored with an arbitrary name
     * that only keeps track of their order of appearance.
     */

    private static final double C_1 = Angle.ofDeg(13.1763966);
    private static final double C_2 = Angle.ofDeg(0.1114041);
    private static final double C_3 = Angle.ofDeg(1.2739);
    private static final double C_4 = Angle.ofDeg(0.1858);
    private static final double C_5 = Angle.ofDeg(0.37);
    private static final double C_6 = Angle.ofDeg(6.2886);
    private static final double C_7 = Angle.ofDeg(0.214);
    private static final double C_8 = Angle.ofDeg(0.6583);
    private static final double C_9 = Angle.ofDeg(0.0529539);
    private static final double C_10 = Angle.ofDeg(0.16);

    /*
     * Hereunder, pre-calculated values.
     */

    /**
     * The cosine of the inclination.
     */
    private static final double COS_I = cos(I);
    /**
     * The sine of the inclination.
     */
    private static final double SIN_I = sin(I);
    /**
     * The square of the eccentricity.
     */
    private static final double E_SQUARED = E * E;

    /**
     * Angular size of the moon as seen from Earth.
     */
    private static final double THETA_0 = Angle.ofDeg(0.5181);

    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion conversion) {
        final Sun sun = SunModel.SUN.at(daysSinceJ2010, conversion);
        final double sunSinMeanAnomaly = sin(sun.meanAnomaly());
        // l in the formulas
        final double longitude = C_1 * daysSinceJ2010 + L_0;
        // M_m in the formulas
        final double meanAnomaly = longitude - C_2 * daysSinceJ2010 - P_0;
        // E_v
        final double evection = C_3 * sin(2 * (longitude - sun.eclipticPos().lon()) - meanAnomaly);
        // A_e
        final double aE = C_4 * sunSinMeanAnomaly;
        // A_3
        final double a3 = C_5 * sunSinMeanAnomaly;

        // (M_m)'
        final double correctedAnomaly = meanAnomaly + evection - aE - a3;
        // E_C
        final double eC = C_6 * sin(correctedAnomaly);
        // A_4
        final double a4 = C_7 * sin(2 * correctedAnomaly);
        // l'
        final double correctedLongitude = longitude + evection + eC - aE + a4;
        // V
        final double variation = C_8 * sin(2 * (correctedLongitude - sun.eclipticPos().lon()));
        // l''
        final double trueLongitude = correctedLongitude + variation;
        // N
        final double meanLongitudeAscendingNode = N_0 - C_9 * daysSinceJ2010;
        // N'
        final double correctedLongitudeAscendingNode = meanLongitudeAscendingNode - C_10 * sunSinMeanAnomaly;
        // lambda_m
        final double lambda = Angle.normalizePositive(
                atan2(sin(trueLongitude - correctedLongitudeAscendingNode) * COS_I,
                        cos(trueLongitude - correctedLongitudeAscendingNode)) + correctedLongitudeAscendingNode
        );
        // beta_m
        final double beta = asin(sin(trueLongitude - correctedLongitudeAscendingNode) * SIN_I);

        // F
        final double phase = (1 - cos(trueLongitude - sun.eclipticPos().lon())) / 2d;

        // rho
        final double distance = (1 - E_SQUARED) / (1 + E * cos(correctedAnomaly + eC));
        // theta
        final double angularSize = THETA_0 / distance;

        return new Moon(conversion.apply(EclipticCoordinates.of(lambda, beta)), (float) angularSize,
                0f, (float) phase);
    }

}
