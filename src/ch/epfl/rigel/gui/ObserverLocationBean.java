package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Holds the parameters of the observer's location: his GeographicCoordinates
 * (his longitude, and latitude, in degrees).
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/04/2020
 */
public class ObserverLocationBean {

    // both values are stored in degrees
    private final SimpleDoubleProperty latitude = new SimpleDoubleProperty();
    private final SimpleDoubleProperty longitude = new SimpleDoubleProperty();

    /**
     * @return the property of the observer's latitude.
     */
    public SimpleDoubleProperty latitudeProperty() {
        return latitude;
    }

    /**
     * @return the property of the observer's longitude.
     */
    public SimpleDoubleProperty longitudeProperty() {
        return longitude;
    }

    /**
     * @return the latitude of the current bean (in degrees).
     */
    public double getLatitude() {
        return latitude.get();
    }

    /**
     * @return the longitude of the current bean (in degrees).
     */
    public double getLongitude() {
        return longitude.get();
    }

    /**
     * Updates the latitude of the current bean.
     *
     * @param latitude the new latitude in degrees
     */
    public void setLatitude(double latitude) {
        this.latitude.set(latitude);
    }

    /**
     * Updates the longitude of the current bean.
     *
     * @param longitude the new longitude in degrees
     */
    public void setLongitude(double longitude) {
        this.longitude.set(longitude);
    }

    /**
     * @return the coordinates of the observer.
     */
    public GeographicCoordinates getCoordinates() {
        //TODO: we used a binding here, can we? (Avoids excessive instantiations)
        // how can we name it?
        return GeographicCoordinates.ofDeg(longitude.get(), latitude.get());
    }

    /**
     * Updates the value of the observer's coordinates to {@code c}.
     *
     * @param c the new coordinates of the observer
     */
    public void setCoordinates(GeographicCoordinates c) {
        setLongitude(c.lonDeg());
        setLatitude(c.latDeg());
    }

}
