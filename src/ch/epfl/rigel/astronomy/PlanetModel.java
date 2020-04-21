package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import java.util.List;

import static java.lang.Math.*;

/**
 * Holds all available models of the Solar System's planets.
 * <br>
 * Please note that, for this particular class, Java naming conventions
 * and standards have been omitted for the sake of readability. They were
 * not, initially; yet, this led to major confusions and mistakes. Thank you
 * for your understanding.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 19/03/2020
 */
public enum PlanetModel implements CelestialObjectModel<Planet> {
    /**
     * Planet Mercury.
     */
    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42),
    /**
     * Planet Venus.
     */
    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947, 76.769, 16.92, -4.40),
    /**
     * Planet Earth.
     */
    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0),
    /**
     * Planet Mars.
     */
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497, 49.632, 9.36, -1.52),
    /**
     * Planet Jupiter.
     */
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40),
    /**
     * Planet Saturn.
     */
    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88),
    /**
     * Planet Uranus.
     */
    URANUS("Uranus", 84.039492, 356.135400, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19),
    /**
     * Planet Neptune.
     */
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);

    /**
     * The list of all models (ordered increasingly by their distance to the Sun).
     */
    public static final List<PlanetModel> ALL = List.of(PlanetModel.values());

    private final String name;
    private final double tropicalYear;
    private final double epsilon;
    private final double omegaBar;
    private final double e;
    private final double a;
    private final double i;
    private final double omega;
    private final double angularSize;
    private final double V0;

    // pre-calculated values
    private final double eSquare;
    private final double cosI;
    private final double sinI;

    /**
     * @param name         the name of the planet
     * @param tropicalYear the revolution period
     * @param epsilon      the longitude at {@link Epoch#J2010} (in degrees)
     * @param omegaBar     the longitude at the perigee (in degrees)
     * @param e            the eccentricity
     * @param a            the length of the semi-major axis
     * @param i            the inclination (in degrees)
     * @param omega        the longitude of the ascending node (in degrees)
     * @param angularSize  the angular size (in seconds)
     * @param V0           the apparent magnitude
     */
    PlanetModel(String name, double tropicalYear, double epsilon,
                double omegaBar, double e, double a,
                double i, double omega, double angularSize, double V0) {
        this.name = name;
        this.tropicalYear = tropicalYear;
        this.epsilon = Angle.ofDeg(epsilon);
        this.omegaBar = Angle.ofDeg(omegaBar);
        this.e = e;
        this.a = a;
        this.i = Angle.ofDeg(i);
        this.omega = Angle.ofDeg(omega);
        this.angularSize = Angle.ofArcsec(angularSize);
        this.V0 = V0;

        // pre-calculated values
        this.eSquare = e * e;
        this.cosI = cos(this.i);
        this.sinI = sin(this.i);
    }

    @Override
    public Planet at(double D, EclipticToEquatorialConversion conversion) {
        // position calculations
        final double M = (Angle.TAU / 365.242191d) * (D / tropicalYear)
                + epsilon - omegaBar;
        final double nu = M + 2 * e * sin(M);
        final double r = (a * (1 - eSquare)) / (1 + e * cos(nu));
        final double l = nu + omegaBar;
        final double psi = asin(sin(l - omega) * sinI);
        final double cosPsi = cos(psi);
        // projected radius
        final double rPrime = r * cosPsi;
        // projected longitude
        final double lPrime = atan2(sin(l - omega) * cosI,
                cos(l - omega)) + omega;

        // Earth calculations
        final double R;
        final double L;
        // shorten the scope of variables used for Earth calculations
        {
            // we assume that PlanetModel.EARTH#at is never called
            final double MEarth = (Angle.TAU / 365.242191d) * (D / EARTH.tropicalYear)
                    + EARTH.epsilon - EARTH.omegaBar;
            final double nuEarth = MEarth + 2 * EARTH.e * sin(MEarth);
            R = (EARTH.a * (1 - EARTH.eSquare))
                    / (1 + EARTH.e * cos(nuEarth));
            L = nuEarth + EARTH.omegaBar;
        }

        final double lambda;
        final double x = R * sin(lPrime - L);
        switch (this) {
            // inferior planets
            case VENUS:
            case MERCURY:
                lambda = Angle.normalizePositive(
                        PI + L + atan2(rPrime * sin(L - lPrime), R - rPrime * cos(L - lPrime))
                );
                break;
            // superior planets
            default:
                lambda = Angle.normalizePositive(
                        lPrime + atan2(x, rPrime - R * cos(lPrime - L))
                );
                break;
        }
        final double beta = atan(rPrime * tan(psi) * sin(lambda - lPrime) / x);

        // angular size calculations
        final double rho = sqrt(R * R + r * r - 2 * R * r * cos(l - L) * cosPsi);
        final double as = angularSize / rho;

        // magnitude calculations
        final double F = (1 + cos(lambda - l)) / 2d;
        final double m = V0 + 5 * log10(r * rho / sqrt(F));
        return new Planet(name, conversion.apply(EclipticCoordinates.of(lambda, beta)), (float) as, (float) m);
    }

}
