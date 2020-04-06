package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * Represents the position of a celestial object in a geocentric model.
 *
 * @param <O> the type of objects modeled by the current model.
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 10/03/2020
 */
public interface CelestialObjectModel<O> {

    /**
     * Creates a new instance of the object (of type {@code O}) with coordinates associated to a certain moment
     * in time (described by {@code daysSinceJ2010}) and a certain position on Earth (given in the {@code conversion}).
     *
     * @param daysSinceJ2010 number of days from the {@link Epoch} {@code J2010}. A negative number of days means that
     *                       the modeled moment is in the past.
     * @param conversion     the conversion associated to a certain position on the Earth.
     * @return a new instance of the model object with coordinates and data based on the provided parameters.
     */
    O at(double daysSinceJ2010, EclipticToEquatorialConversion conversion);

}
