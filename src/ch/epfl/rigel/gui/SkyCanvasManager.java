package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.Objects;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/04/2020
 */
public class SkyCanvasManager {

    /**
     * Represents the bounds of the rotation of the projection's center, when
     * moving it with the left and right keys.
     */
    private static final RightOpenInterval ROT_LIM = RightOpenInterval.of(0, 360);
    /**
     * Represents the bounds of the field of view value, when changing it with the
     * up and down keys or the mouse's scroll.
     */
    private static final ClosedInterval FOV_LIM = ClosedInterval.of(30, 150);

    private final Canvas canvas = new Canvas();
    private final SkyCanvasPainter painter = new SkyCanvasPainter(canvas);

    // the following values are stored into floats
    private final SimpleObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>(new Point2D(0, 0));

    private final ObservableObjectValue<CelestialObject> objectUnderMouse;
    private final ObservableObjectValue<StereographicProjection> projection;
    private final ObservableObjectValue<ObservedSky> observedSky;
    private final ObservableObjectValue<Transform> transform;
    private final ObservableObjectValue<HorizontalCoordinates> mouseHorizontalPosition;
    private final ObservableDoubleValue mouseAzimuth;
    private final ObservableDoubleValue mouseAltitude;

    /**
     * @param catalogue         the star and asterisms catalogue to use
     * @param dateTime          the date time of the observation
     * @param observerLocation  observer's location
     * @param viewingParameters the viewing parameters of the user
     */
    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dateTime,
                            ObserverLocationBean observerLocation, ViewingParametersBean viewingParameters) {
        // first assert all prerequisites, in order to avoid heavy syntax
        // and used in irregular ways
        Objects.requireNonNull(catalogue);
        Objects.requireNonNull(dateTime);
        Objects.requireNonNull(observerLocation);
        Objects.requireNonNull(viewingParameters);

        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewingParameters.getCenter()),
                viewingParameters.getCenterProperty()
        );

        transform = Bindings.createObjectBinding(
                () -> {
                    final double dilatation =
                            canvas.getWidth() / (2d * Math.tan(Angle.ofDeg(viewingParameters.getFieldOfView()) / 4d));
                    return Transform.affine(
                            dilatation,
                            0,
                            0,
                            -dilatation,
                            canvas.getWidth() / 2d,
                            canvas.getHeight() / 2d
                    );
                },
                canvas.widthProperty(),
                canvas.heightProperty(),
                projection,
                viewingParameters.getFieldOfViewProperty()
        );

        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> {
                    final Point2D inverse;
                    try {
                        inverse = transform.get().inverseTransform(mousePosition.get());
                    } catch (NonInvertibleTransformException e) {
                        // when the program starts up, whe can tolerate the fact that the coordinates
                        // are not invertible
                        return null;
                    }
                    return projection.get().inverseApply(CartesianCoordinates.of(inverse.getX(), inverse.getY()));
                },
                mousePosition,
                transform,
                projection
        );

        mouseAzimuth = Bindings.createDoubleBinding(
                () -> mouseHorizontalPosition.get().azDeg(),
                mouseHorizontalPosition
        );

        mouseAltitude = Bindings.createDoubleBinding(
                () -> mouseHorizontalPosition.get().altDeg(),
                mouseHorizontalPosition
        );

        observedSky = Bindings.createObjectBinding(
                () -> new ObservedSky(dateTime.getZonedDateTime(),
                        observerLocation.getCoordinates(), projection.get(), catalogue),
                observerLocation.getCoordinatesBinding(),
                projection,
                dateTime.dateProperty(),
                dateTime.timeProperty(),
                dateTime.zoneProperty()
        );

        objectUnderMouse = Bindings.createObjectBinding(
                () -> {
                    final Point2D mouse;
                    try {
                        mouse = transform.get().inverseTransform(mousePosition.get());
                    } catch (NonInvertibleTransformException e) {
                        // when the program starts up, whe can tolerate the fact that the coordinates
                        // are not invertible
                        return null;
                    }
                    return observedSky
                            .get()
                            .objectClosestTo(CartesianCoordinates.of(mouse.getX(), mouse.getY()), 1d)
                            .orElse(null);
                },
                mousePosition,
                observedSky,
                transform
        );
        // JFX events
        canvas.setOnMouseMoved(event -> {
            mousePosition.set(new Point2D(event.getX(), event.getY()));
        });
        canvas.setOnMouseClicked(event -> {
            if (event.isPrimaryButtonDown()) {
                canvas.requestFocus();
                event.consume();
            }
        });
        canvas.setOnScroll(event -> {
            final double apply;
            // choose the biggest change
            if (Math.abs(event.getDeltaY()) >= Math.abs(event.getDeltaX())) {
                apply = event.getDeltaY();
            } else {
                apply = event.getDeltaX();
            }
            viewingParameters.setFieldOfViewDeg(FOV_LIM.clip(viewingParameters.getFieldOfView() + apply));
        });
        canvas.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    final HorizontalCoordinates current = viewingParameters.getCenter();
                    viewingParameters.setCenter(HorizontalCoordinates.ofDeg(ROT_LIM.reduce(current.azDeg() - 10d),
                            current.altDeg()));
                    break;
                case RIGHT:
                    final HorizontalCoordinates c = viewingParameters.getCenter();
                    viewingParameters.setCenter(HorizontalCoordinates.ofDeg(ROT_LIM.reduce(c.azDeg() + 10d),
                            c.altDeg()));
                    break;
            }
        });
        // draw listeners
        final ChangeListener<Object> listener = (observable, oldValue, newValue) -> {
            final ObservedSky s = observedSky.get();
            final StereographicProjection p = projection.get();
            final Transform t = transform.get();
            painter.clear();
            painter.drawStars(s, p, t);
            painter.drawPlanets(s, p, t);
            painter.drawSun(s, p, t);
            painter.drawMoon(s, p, t);
            painter.drawHorizon(s, p, t);
        };
        observedSky.addListener(listener);
        transform.addListener(listener);
        projection.addListener(listener);
    }

    /**
     * @return the property that holds the object under the mouse.
     */
    public ObservableObjectValue<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    /**
     * @return the canvas used for drawing.
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * @return the azimuth of the mouse, in HorizontalCoordinates.
     */
    public double mouseAzimuth() {
        return mouseAzimuth.get();
    }

    /**
     * @return the property that holds the azimuth of the mouse, in HorizontalCoordinates.
     */
    public ObservableDoubleValue mouseAzimuthProperty() {
        return mouseAzimuth;
    }

    /**
     * @return the altitude of the mouse, in HorizontalCoordinates.
     */
    public double mouseAltitude() {
        return mouseAltitude.get();
    }

    /**
     * @return the property that holds the altitude of the mouse, in HorizontalCoordinates.
     */
    public ObservableDoubleValue mouseAltitudeProperty() {
        return mouseAltitude;
    }

}
