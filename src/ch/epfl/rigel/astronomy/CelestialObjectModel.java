package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * Represents the position of a celestial object in a geocentric model.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 10/03/2020
 */
public interface CelestialObjectModel<O> {

    /**
     * Creates a new instance of the object (of type {@code O}) with coordinates given by the elapsed time,
     * and the coordinates conversion.
     * @param daysSinceJ2010 numberco of days from the {@link Epoch} {@code J2010}. Can be decimal. Can be negative
     *                       (meaning we want to modelize the object {@code O} before {@link Epoch} {@code J2010}.
     * @param conversion instance of {@link EclipticToEquatorialConversion} used for coordinates conversion.
     * @return a new instance of the object (of type {@code o} with coordinates updated based on the
     * elapsed time given by {@code daysSinceJ2010}.
     */
    O at(double daysSinceJ2010, EclipticToEquatorialConversion conversion);

}
