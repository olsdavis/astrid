package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
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
     * @return the center of the projection.
     */
    public HorizontalCoordinates getCenter() {
        return center.get();
    }

    /**
     * @return the property holding the HorizontalCoordinates of the center, in
     * a read-only property.
     */
    public ReadOnlyObjectProperty<HorizontalCoordinates> getCenterProperty() {
        return center;
    }

    /**
     * @return the value of the field of view, in degrees.
     */
    public double getFieldOfView() {
        return fieldOfView.get();
    }

    /**
     * Updates the value of the user's field of view.
     *
     * @param value the new value of the field of view (in degrees)
     */
    public void setFieldOfView(float value) {
        fieldOfView.set(value);
    }

    /**
     * @return the property holding the field of view property of the projection,
     * in a read-only property.
     */
    public ReadOnlyDoubleProperty getFieldOfViewProperty() {
        return fieldOfView;
    }

    public void setFieldOfViewDeg(double fov) {
        fieldOfView.set(fov);
    }

}
