package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import static ch.epfl.rigel.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents celestial objects.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 08/03/2020
 */
public abstract class CelestialObject {

    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;

    /**
     * @param name          the name
     * @param equatorialPos the position represented by EquatorialCoordinates
     * @param angularSize   the angular size
     * @param magnitude     the magnitude
     * @throws IllegalArgumentException if {@code angularSize} is negative
     */
    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        checkArgument(angularSize >= 0);
        this.name = requireNonNull(name, "the name of the CelestialObject cannot be null");
        this.equatorialPos = requireNonNull(equatorialPos, "the position of the CelestialObject cannot be null");
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    }

    /**
     * @return the name.
     */
    public String name() {
        return name;
    }

    /**
     * @return the angular size.
     */
    public double angularSize() {
        return angularSize;
    }

    /**
     * @return the magnitude.
     */
    public double magnitude() {
        return magnitude;
    }

    /**
     * @return the position represented by EquatorialCoordinates.
     */
    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    /**
     * @return information about the CelestialObject.
     */
    public String info() {
        return name();
    }

    @Override
    public String toString() {
        return info();
    }

}
