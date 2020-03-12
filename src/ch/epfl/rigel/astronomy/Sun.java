package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Represents the sun.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 08/03/2020
 * @see CelestialObject
 */
public final class Sun extends CelestialObject {

    private final float meanAnomaly;
    private final EclipticCoordinates eclipticPos;

    /**
     * @param eclipticPos   the position of the Sun represented by EclipticCoordinates
     * @param equatorialPos the position of the Sun represented by EquatorialCoordinates
     * @param angularSize   the angular size of the Sun
     * @param meanAnomaly   the mean anomaly of the Sun
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super("Soleil", equatorialPos, angularSize, -26.7f);
        requireNonNull(equatorialPos, "the equatorial position cannot be null");
        this.eclipticPos = eclipticPos;
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * @return the mean anomaly of the Sun.
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }

    /**
     * @return the position of the Sun represented by EquatorialCoordinates.
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    @Override
    public EquatorialCoordinates equatorialPos() {
        return super.equatorialPos();
    }

}
