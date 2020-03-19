package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Solely holds the model of the sun.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 19/03/2020
 */
public enum SunModel implements CelestialObjectModel<Sun> {

    SUN;

    /**
     * Sun's longitude at {@link Epoch#J2010}.
     */
    private static final double LONGITUDE_J2010 = Angle.ofDeg(279.557208d);
    /**
     * Sun's longitude at perigee.
     */
    private static final double LONGITUDE_PERIGEE = Angle.ofDeg(283.112438d);
    /**
     * The difference between the longitude of the sun at perigee and at {@link Epoch#J2010}.
     */
    private static final double LONGITUDE_DIFF = LONGITUDE_J2010 - LONGITUDE_PERIGEE;
    /**
     * Eccentricity of the Sun/Earth orbital.
     */
    private static final double ECCENTRICITY = 0.016705;
    /**
     * The square of {@link #ECCENTRICITY}.
     */
    private static final double ECCENTRICITY_2 = ECCENTRICITY * ECCENTRICITY;

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion conversion) {
        final double meanAnomaly = (Angle.TAU / 365.242191d) * daysSinceJ2010 + LONGITUDE_DIFF;
        final double trueAnomaly = meanAnomaly + 2 * ECCENTRICITY * sin(meanAnomaly);
        final EclipticCoordinates coordinates = EclipticCoordinates.of(trueAnomaly + LONGITUDE_PERIGEE, 0);
        final double angularSize = 0.533128d * ((1 + ECCENTRICITY * cos(trueAnomaly) / 1 - ECCENTRICITY_2));
        return new Sun(coordinates, conversion.apply(coordinates), (float) angularSize, (float) meanAnomaly);
    }

}
