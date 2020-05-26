package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import static java.util.Objects.requireNonNull;

/**
 * Represents the sun.
 *
 * @see CelestialObject
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 08/03/2020
 */
public final class Sun extends CelestialObject {

    private final float meanAnomaly;
    private final EclipticCoordinates eclipticPos;

    /**
     * @param eclipticPos   the position of the Sun
     * @param equatorialPos the position of the Sun
     * @param angularSize   the angular size of the Sun
     * @param meanAnomaly   the mean anomaly of the Sun
     *
     * @throws NullPointerException if {@code eclipticPos} is {@code null}
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super("Soleil", equatorialPos, angularSize, -26.7f);
        this.eclipticPos = requireNonNull(eclipticPos, "the ecliptic position cannot be null");
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * @return the mean anomaly of the Sun.
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }

    /**
     * @return the position of the Sun represented by {@link EclipticCoordinates}.
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

}
