package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Solely holds the model of the sun.
 * <br>
 * Please note that, for this class, Java naming conventions
 * and standards have been omitted for the sake of readability. They were
 * not, initially; yet, this led to major confusions and mistakes. Thank you
 * for your understanding.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 19/03/2020
 */
public enum SunModel implements CelestialObjectModel<Sun> {

    SUN;

    /**
     * Tau (2 * PI) divided by a tropical year.
     */
    private static final double TAU_BY_TROPICAL_YEAR = (Angle.TAU / 365.242191d);
    /**
     * Sun's longitude at {@link Epoch#J2010}.
     */
    private static final double EPSILON = Angle.ofDeg(279.557208d);
    /**
     * Sun's longitude at perigee.
     */
    private static final double OMEGA_BAR = Angle.ofDeg(283.112438d);
    /**
     * Theta0 value used for the angular size of the sun.
     */
    private static final double THETA_0 = Angle.ofDeg(0.533128d);
    /**
     * Eccentricity of the Sun/Earth orbital.
     */
    private static final double E = 0.016705d;
    /**
     * The square of {@link #E}.
     */
    private static final double E_2 = E * E;

    @Override
    public Sun at(double D, EclipticToEquatorialConversion conversion) {
        final double M = TAU_BY_TROPICAL_YEAR * D + EPSILON - OMEGA_BAR;
        final double nu = M + 2 * E * sin(M);
        final EclipticCoordinates coordinates = EclipticCoordinates.of(Angle.normalizePositive(nu + OMEGA_BAR), 0);
        final double theta = THETA_0 * (1 + E * cos(nu)) / (1 - E_2);
        return new Sun(coordinates, conversion.apply(coordinates), (float) theta, (float) M);
    }

}
