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
import javafx.scene.input.MouseButton;
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
     * Represents the bounds of the azimuth of the projection's center, when
     * moving it with the left and right keys.
     */
    private static final RightOpenInterval AZ_LIM = RightOpenInterval.of(0, 360);
    /**
     * Represents the bounds of the altitude of the projection's center, when
     * moving it with the up and down keys.
     */
    private static final ClosedInterval ALT_LIM = ClosedInterval.of(5, 90);
    /**
     * Represents the bounds of the field of view value, when changing it with the
     * up and down keys or the mouse's scroll.
     */
    private static final ClosedInterval FOV_LIM = ClosedInterval.of(30, 150);

    /**
     * The step when moving the projection center's coordinates using the up and
     * down keys.
     */
    private static final double UP_DOWN_STEP = 5d;
    /**
     * The step when moving the projection center's coordinates using the left and
     * right keys.
     */
    private static final double LEFT_RIGHT_STEP = 10d;

    /**
     * The maximal distance for the object under mouse search.
     */
    private static final double MAX_DISTANCE = 10d;

    private final Canvas canvas = new Canvas();
    private final SkyCanvasPainter painter = new SkyCanvasPainter(canvas);

    // the following values are stored into floats
    private final SimpleObjectProperty<Point2D> mousePosition = new SimpleObjectProperty<>(new Point2D(0, 0));

    private final ViewingParametersBean viewingParameters;

    private final ObservableObjectValue<CelestialObject> objectUnderMouse;
    private final ObservableObjectValue<StereographicProjection> projection;
    private final ObservableObjectValue<ObservedSky> observedSky;
    private final ObservableObjectValue<Transform> transform;
    private final ObservableObjectValue<HorizontalCoordinates> mouseHorizontalPosition;
    private final ObservableDoubleValue mouseAzimuth;
    private final ObservableDoubleValue mouseAltitude;
    private final ObservableDoubleValue maxDistance; // used for objectClosesTo

    /**
     * Initializes the bindings that allow updating the user's view, sets up
     * the canvas drawer, and sets up its updates as well.
     *
     * @param catalogue         the star and asterisms catalogue to use
     * @param dateTime          the date time of the observation
     * @param observerLocation  observer's location
     * @param viewingParameters the viewing parameters of the user
     *
     * @throws NullPointerException if one of the parameters is {@code null}
     */
    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dateTime,
                            ObserverLocationBean observerLocation, ViewingParametersBean viewingParameters) {
        // first assert all prerequisites, in order to avoid heavy syntax
        Objects.requireNonNull(catalogue);
        Objects.requireNonNull(dateTime);
        Objects.requireNonNull(observerLocation);
        this.viewingParameters = Objects.requireNonNull(viewingParameters);

        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewingParameters.getCenter()),
                viewingParameters.centerProperty()
        );

        transform = Bindings.createObjectBinding(
                () -> {
                    final double dilatation =
                            canvas.getWidth() / projection.get().applyToAngle(Angle.ofDeg(viewingParameters.getFieldOfView()));
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
                viewingParameters.fieldOfViewProperty()
        );

        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> {
                    final Point2D inverse;
                    try {
                        inverse = transform.get().inverseTransform(mousePosition.get());
                    } catch (NonInvertibleTransformException e) {
                        // when the program starts up, we can tolerate the fact that the coordinates
                        // are not invertible
                        return HorizontalCoordinates.ofDeg(0, 0);
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
                observerLocation.longitudeProperty(),
                observerLocation.latitudeProperty(),
                projection,
                dateTime.dateProperty(),
                dateTime.timeProperty(),
                dateTime.zoneProperty()
        );

        maxDistance = Bindings.createDoubleBinding(
                () -> Math.abs(transform.get().inverseDeltaTransform(MAX_DISTANCE, 0).getX()),
                transform
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
                            .objectClosestTo(CartesianCoordinates.of(mouse.getX(), mouse.getY()), maxDistance.get())
                            .orElse(null);
                },
                mousePosition,
                observedSky,
                transform
        );
        // JFX events
        canvas.setOnMouseMoved(event -> mousePosition.set(new Point2D(event.getX(), event.getY())));
        canvas.setOnMouseClicked(event -> {
            // event#isPrimaryButton indicates something else than
            // the button that is responsible for the event (which is given by event#getButton)
            // It didn't work on Windows 10.
            if (event.getButton() == MouseButton.PRIMARY) {
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
            // we could have put this declaration (current = ...) in each switch case, hence
            // calling it only if a listened key is triggered. Yet, it is a minor
            // early optimization, and it creates big code repetitions.
            // Moreover, it is possible that the HotSpot JVM applies this optimization
            // (cf. loop invariant hoisting).
            // https://www.oracle.com/technetwork/java/whitepaper-135217.html#server
            final HorizontalCoordinates current = viewingParameters.getCenter();
            switch (event.getCode()) {
                case LEFT:
                    viewingParameters.setAzimuth(AZ_LIM.reduce(current.azDeg() - LEFT_RIGHT_STEP));
                    break;
                case RIGHT:
                    viewingParameters.setAzimuth(AZ_LIM.reduce(current.azDeg() + LEFT_RIGHT_STEP));
                    break;
                case DOWN:
                    viewingParameters.setAltitude(ALT_LIM.clip(current.altDeg() - UP_DOWN_STEP));
                    break;
                case UP:
                    viewingParameters.setAltitude(ALT_LIM.clip(current.altDeg() + UP_DOWN_STEP));
            }
            event.consume();
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
        // we do not add a listener to projection, because its value change
        // updates transform anyway
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
     * @return the property that holds the azimuth of the mouse, in HorizontalCoordinates.
     */
    public ObservableDoubleValue mouseAzimuthProperty() {
        return mouseAzimuth;
    }

    /**
     * @return the property that holds the altitude of the mouse, in HorizontalCoordinates.
     */
    public ObservableDoubleValue mouseAltitudeProperty() {
        return mouseAltitude;
    }

    /**
     * Sets the projection center to the provided {@link CelestialObject}
     * if it is in the current bounds of the view; if it is not, it approaches
     * the object as good as it can.
     *
     * TODO: hallo/flare
     *
     * @param o the {@link CelestialObject} to focus on
     */
    public void focus(CelestialObject o) {
        final HorizontalCoordinates coordinates = observedSky.get().locate(o);
            viewingParameters.setCenter(HorizontalCoordinates.ofDeg(
                    AZ_LIM.reduce(coordinates.azDeg()),
                    ALT_LIM.clip(coordinates.altDeg())
                )
            );
    }

    /**
     * @return the instance of {@link ObservedSky} that is in use.
     */
    public ObservedSky sky() {
        return observedSky.get();
    }

}
