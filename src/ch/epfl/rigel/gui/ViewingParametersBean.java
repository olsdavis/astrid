package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Holds the parameters of user's view: his field of view,
 * and the center of his projection.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/04/2020
 */
public class ViewingParametersBean {

    // the held value is in degrees
    private final SimpleDoubleProperty fieldOfView = new SimpleDoubleProperty();
    private final SimpleObjectProperty<HorizontalCoordinates> center = new SimpleObjectProperty<>();

    /**
     * Updates the center of the projection.
     *
     * @param coordinates the new center of the projection.
     */
    public void setCenter(HorizontalCoordinates coordinates) {
        center.set(coordinates);
    }

    /**
     * Updates the azimuth of the bean (in degrees).
     *
     * @param az the new azimuth (in degrees)
     */
    public void setAzimuth(double az) {
        setCenter(HorizontalCoordinates.of(Angle.ofDeg(az), center.get().alt()));
    }

    /**
     * Updates the altitude of the bean (in degrees).
     *
     * @param alt the new altitude (in degrees)
     */
    public void setAltitude(double alt) {
        setCenter(HorizontalCoordinates.of(center.get().az(), Angle.ofDeg(alt)));
    }

    /**
     * @return the center of the projection.
     */
    public HorizontalCoordinates getCenter() {
        return center.get();
    }

    /**
     * @return the property holding the HorizontalCoordinates of the center, in
     * a read-only property.
     */
    public ReadOnlyObjectProperty<HorizontalCoordinates> centerProperty() {
        return center;
    }

    /**
     * @return the value of the field of view, in degrees.
     */
    public double getFieldOfView() {
        return fieldOfView.get();
    }

    /**
     * @return the property holding the field of view property of the projection,
     * in a read-only property.
     */
    public ReadOnlyDoubleProperty fieldOfViewProperty() {
        return fieldOfView;
    }

    /**
     * Updates the value of the field of view.
     *
     * @param fov the field of view, in degrees
     */
    public void setFieldOfViewDeg(double fov) {
        fieldOfView.set(fov);
    }

}
