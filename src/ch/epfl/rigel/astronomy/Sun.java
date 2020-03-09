package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.time.ZonedDateTime;

/**
 * Represents the sun.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 08/03/2020
 * @see CelestialObject
 */
public final class Sun extends CelestialObject {

    private final double meanAnomaly;

    /**
     * @param eclipticPos the position of the Sun represented by EclipticCoordinates
     * @param angularSize the angular size of the Sun
     * @param meanAnomaly the mean anomaly of the Sun
     */
    public Sun(EclipticCoordinates eclipticPos, float angularSize, float meanAnomaly) {
        //TODO: verify this ZonedDateTime thing, it was just for now
        super("Soleil", new EclipticToEquatorialConversion(ZonedDateTime.now()).apply(eclipticPos), angularSize, -26.7f);
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * @return the mean anomaly of the Sun.
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }

    @Override
    public EquatorialCoordinates equatorialPos() {
        return super.equatorialPos();
    }

}
