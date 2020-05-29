package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Represents planets.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 08/03/2020
 * @see CelestialObject
 */
public final class Planet extends CelestialObject {

    /**
     * @param name          the name of the planet
     * @param equatorialPos the position of the planet
     * @param angularSize   the angular size of the planet
     * @param magnitude     the magnitude of the planet
     */
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        super(name, equatorialPos, angularSize, magnitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Planet)) {
            return false;
        }

        final Planet planet = (Planet) obj;
        // this uses the assumption that there is only one instance of each planet that is used, per
        // per moment of observation
        return planet.name().equals(name());
    }

}
