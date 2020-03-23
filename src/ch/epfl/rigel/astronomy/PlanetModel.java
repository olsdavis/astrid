package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import java.util.List;

import static java.lang.Math.*;

/**
 * Holds all available models of the Solar System's planets.
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
    URANUS("Uranus", 84.039492, 271.063148, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19),
    /**
     * Planet Neptune.
     */
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);

    /**
     * The list of all models (ordered).
     */
    public static final List<PlanetModel> ALL = List.of(PlanetModel.values());

    private final String name;
    private final double tropicalYear;
    private final double longitude2010;
    private final double longitudeP;
    private final double eccentricity;
    private final double semiMajorAxis;
    private final double inclination;
    private final double longitudeONode;
    private final double angularSize;
    private final double magnitude;

    // pre-calculated values
    private final double longitudeDiff;
    private final double eccentricitySquare;

    /**
     * @param name           the name of the planet
     * @param tropicalYear   the revolution period
     * @param longitude2010  the longitude at {@link Epoch#J2010} (in degrees)
     * @param longitudeP     the longitude at the perigee (in degrees)
     * @param eccentricity   the eccentricity
     * @param semiMajorAxis  the length of the semi-major axis
     * @param inclination    the inclination (in degrees)
     * @param longitudeONode the longitude of the orbital node (in degrees)
     * @param angularSize    the angular size (in seconds)
     * @param magnitude      the apparent magnitude
     */
    PlanetModel(String name, double tropicalYear, double longitude2010,
                double longitudeP, double eccentricity, double semiMajorAxis,
                double inclination, double longitudeONode, double angularSize, double magnitude) {
        this.name = name;
        this.tropicalYear = tropicalYear;
        this.longitude2010 = Angle.ofDeg(longitude2010);
        this.longitudeP = Angle.ofDeg(longitudeP);
        this.eccentricity = eccentricity;
        this.semiMajorAxis = semiMajorAxis;
        this.inclination = Angle.ofDeg(inclination);
        this.longitudeONode = Angle.ofDeg(longitudeONode);
        this.angularSize = Angle.ofDMS(0, 0, angularSize);
        this.magnitude = magnitude;

        // pre-calculated values
        this.longitudeDiff = longitude2010 - longitudeP;
        this.eccentricitySquare = eccentricity * eccentricity;
    }

    //TODO: store pre-calculated values
    //TODO: add normalizePositive on longitudes + atan2?
    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion conversion) {
        // position calculations
        final double meanAnomaly = (Angle.TAU / 365.242191d) * (daysSinceJ2010 / tropicalYear)
                + longitudeDiff;
        final double trueAnomaly = meanAnomaly + 2 * eccentricity * sin(meanAnomaly);
        final double radius = (semiMajorAxis * (1 - eccentricitySquare))
                / (1 + eccentricity * cos(trueAnomaly));
        final double longitude = trueAnomaly + longitudeP;
        final double psi = asin(sin(longitude - longitudeONode) * sin(inclination));
        final double cosPsi = cos(psi);
        // projected radius
        final double pRad = radius * cosPsi;
        // projected longitude
        final double pLon = atan2(sin(longitude - longitudeONode) * cos(inclination),
                cos(longitude - longitudeONode)) + longitudeONode;

        // earth calculations
        final double earthRadius;
        final double earthLongitude;
        // shorten scope of the variables used for Earth calculations
        {
            if (this == EARTH) {
                earthRadius = radius;
                earthLongitude = longitude;
            } else {
                final double meanEarth = (Angle.TAU / 365.242191d) * (daysSinceJ2010 / EARTH.tropicalYear)
                        + EARTH.eccentricity - EARTH.longitudeP;
                final double trueEarth = meanEarth + 2 * EARTH.eccentricity * sin(meanEarth);
                earthRadius = (EARTH.semiMajorAxis * (1 - EARTH.eccentricity * EARTH.eccentricity))
                        / (1 + EARTH.eccentricity * cos(trueEarth));
                earthLongitude = trueEarth + EARTH.longitudeP;
            }
        }

        final double lambda;
        final double x = earthRadius * sin(pLon - earthLongitude);
        switch (this) {
            // inferior planets
            case VENUS:
            case MERCURY:
                lambda = PI + earthLongitude + atan2(pRad * sin(earthLongitude - pLon),
                        earthRadius - pRad * cos(earthLongitude - pLon));
                break;
            // superior planets
            default:
                lambda = pLon + atan2(x, pRad - earthRadius * cos(pLon - earthLongitude));
                break;
        }
        final double beta = atan2(pRad * tan(psi) * sin(lambda - pLon), x);

        // angular size calculations
        final double distance = sqrt(earthRadius * earthRadius + radius * radius
                - 2 * earthRadius * radius * cos(longitude - earthLongitude) * cosPsi);
        final double as = angularSize / distance;

        // magnitude calculations
        final double f = 1 + cos(lambda - longitude) / 2d;
        final double m = magnitude + 5 * log10(radius * distance / sqrt(f));
        return new Planet(name, conversion.apply(EclipticCoordinates.of(lambda, beta)), (float) as, (float) m);
    }

}
