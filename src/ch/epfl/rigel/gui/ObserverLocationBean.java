package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ObservableObjectValue;

/**
 * Holds the parameters of the observer's location: his GeographicCoordinates
 * (his longitude, and latitude, in degrees).
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/04/2020
 */
public class ObserverLocationBean {

    // both values are in degrees
    private final SimpleDoubleProperty latitude = new SimpleDoubleProperty();
    private final SimpleDoubleProperty longitude = new SimpleDoubleProperty();
    private final ObservableObjectValue<GeographicCoordinates> coordinatesBinding;

    /**
     * Default constructor. Initializes the binding.
     */
    public ObserverLocationBean() {
        coordinatesBinding = Bindings.createObjectBinding(
                () -> GeographicCoordinates.ofDeg(longitude.get(), latitude.get()),
                longitude,
                latitude
        );
    }

    /**
     * @return the latitude of the observer.
     */
    public double getLatitude() {
        return latitude.get();
    }

    /**
     * @return the longitude of the observer.
     */
    public double getLongitude() {
        return longitude.get();
    }

    /**
     * @return the coordinates of the observer.
     */
    public GeographicCoordinates getCoordinates() {
        return coordinatesBinding.get();
    }

    /**
     * @return the binding that creates the GeographicCoordinates.
     */
    public ObservableObjectValue<GeographicCoordinates> getCoordinatesBinding() {
        return coordinatesBinding;
    }

    public void setCoordinates(GeographicCoordinates c) {
        latitude.set(c.latDeg());
        longitude.set(c.lonDeg());
    }

}
