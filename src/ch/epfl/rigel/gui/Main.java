package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class, starting up the JavaFX application.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 06/05/2020
 */
public class Main extends Application {

    /**
     * Holds the font used for buttons, such as the resume/pause button.
     */
    private static final Font BUTTONS_FONT;
    /**
     * Holds the character used in the BUTTONS_FONT font for the play button of the animator.
     */
    private static final String PLAY_CHARACTER = "\uf04b";
    /**
     * Holds the character used in the BUTTONS_FONT font for the pause button of the animator.
     */
    private static final String PAUSE_CHARACTER = "\uf04c";
    /**
     * Holds the character used in the BUTTONS_FONT font for the reset button of the animator.
     */
    private static final String RESET_CHARACTER = "\uf0e2";
    /**
     * Initial field of view.
     */
    private static final double INIT_FOV = 100d;
    /**
     * Initial coordinates of the user.
     */
    private static final GeographicCoordinates INIT_COORDINATES = GeographicCoordinates.ofDeg(6.57d, 46.52d);
    /**
     * The initial projection center.
     */
    private static final HorizontalCoordinates INIT_PROJ_CENTER = HorizontalCoordinates.ofDeg(180.000000000001d, 15d);
    /**
     * Holds the default longitude for the observer's position.
     */
    private static final Number DEFAULT_LON = 6.57d;
    /**
     * Holds the default latitude for the observer's position.
     */
    private static final Number DEFAULT_LAT = 46.52d;

    static {
        try (final InputStream stream = Main.class.getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf")) {
            BUTTONS_FONT = Font.loadFont(stream, 15);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private final ObserverLocationBean position = new ObserverLocationBean();
    private final DateTimeBean date = new DateTimeBean();
    private final ViewingParametersBean viewingParameters = new ViewingParametersBean();
    private final TimeAnimator animator = new TimeAnimator(date);
    private SkyCanvasManager manager;

    /**
     * @param checker   a function that returns {@code true} if the passed argument (a double)
     *                  is a valid coordinate.
     * @param converter a converter that converts Strings to numbers
     * @return a filter that only allows valid coordinates.
     */
    private static UnaryOperator<TextFormatter.Change> coordinatesFilter(Function<Double, Boolean> checker,
                                                                         NumberStringConverter converter) {
        return change -> {
            try {
                final String newText =
                        change.getControlNewText();
                final double newLatDeg =
                        converter.fromString(newText).doubleValue();
                return checker.apply(newLatDeg)
                        ? change
                        : null;
            } catch (Exception e) {
                return null;
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rigel");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // initialize beans
        position.setCoordinates(INIT_COORDINATES);
        date.setZonedDateTime(ZonedDateTime.now());
        viewingParameters.setCenter(INIT_PROJ_CENTER);
        viewingParameters.setFieldOfViewDeg(INIT_FOV);

        // initialize catalogue
        try (final InputStream hs = getClass().getResourceAsStream("/hygdata_v3.csv");
             final InputStream as = getClass().getResourceAsStream("/asterisms.txt")) {
            manager = new SkyCanvasManager(
                    new StarCatalogue.Builder()
                            .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                            .loadFrom(as, AsterismLoader.INSTANCE)
                            .build(),
                    date,
                    position,
                    viewingParameters
            );
        } catch (Exception e) {
            Logger.getLogger("Rigel").log(Level.SEVERE,
                    "Could not load the hyg database and/or the asterisms file, stack trace:", e);
            System.exit(1); // exit with error
        }

        animator.runningProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                animator.start();
            } else {
                animator.stop();
            }
        });

        final BorderPane root = new BorderPane();
        manager.canvas().widthProperty().bind(root.widthProperty());
        manager.canvas().heightProperty().bind(root.heightProperty());
        root.setTop(controlBar());
        root.setCenter(new Pane(manager.canvas()));
        root.setBottom(bottomPane());

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        manager.canvas().requestFocus();
    }

    /**
     * @return the bottom pane of the window containing the object under the mouse,
     * the field of view and the corresponding horizontal coordinates of the mouse.
     */
    private BorderPane bottomPane() {
        // set up the celestial object under mouse
        final Text underMouse = new Text("");
        underMouse.textProperty().bind(Bindings.createStringBinding(() -> {
            final CelestialObject object = manager.objectUnderMouseProperty().get();
            return object == null ? "" : object.info();
        }, manager.objectUnderMouseProperty()));
        final BorderPane bottom = new BorderPane(underMouse);
        bottom.setStyle("-fx-padding: 4; -fx-background-color: #ffffff;");
        bottom.setCenter(underMouse);
        // set up the field of view text
        final Text fov = new Text("");
        fov.textProperty().bind(
                Bindings.format(Locale.ROOT, "Champ de vue : %.1f°", viewingParameters.fieldOfViewProperty())
        );
        bottom.setLeft(fov);
        // set up the mouse position in horizontal coordinates text
        final Text mouseHorizontal = new Text("");
        mouseHorizontal.textProperty().bind(
                Bindings.format(
                        Locale.ROOT, "Azimut : %.2f°, hauteur : %.2f°",
                        manager.mouseAzimuthProperty(),
                        manager.mouseAltitudeProperty()
                )
        );
        bottom.setRight(mouseHorizontal);
        return bottom;
    }

    /**
     * @return the upper control bar containing the time box and the animator box.
     */
    private HBox controlBar() {
        final HBox controlBar = new HBox(createPositionBox(), new Separator(Orientation.VERTICAL),
                createTimeBox(), new Separator(Orientation.VERTICAL), createAnimatorBox());
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlBar;
    }

    /**
     * @return the animator box containing the animator selection (speed of simulation),
     * the resume/pause button and the reset button.
     */
    private HBox createAnimatorBox() {
        // set up the animator choice button
        final ChoiceBox<NamedTimeAccelerator> animatorChoice = new ChoiceBox<>();
        animatorChoice.setItems(FXCollections.observableList(Arrays.asList(NamedTimeAccelerator.values())));
        animatorChoice.valueProperty().addListener((observable, oldValue, newValue) ->
                animator.setAccelerator(newValue.getAccelerator()));
        animatorChoice.setValue(NamedTimeAccelerator.TIMES_300);
        final HBox chooseAnimator = new HBox(animatorChoice);
        chooseAnimator.setStyle("-fx-spacing: inherit;");
        // set up the play button
        final Button play = new Button(PLAY_CHARACTER);
        play.setOnMouseClicked(event -> {
            if (animator.runningProperty().get()) {
                animator.stop();
            } else {
                animator.start();
            }
        });
        play.setFont(BUTTONS_FONT);
        play.textProperty().bind(
                Bindings.when(animator.runningProperty()).then(PAUSE_CHARACTER).otherwise(PLAY_CHARACTER)
        );
        // set up the reset button
        final Button reset = new Button(RESET_CHARACTER);
        reset.setOnMouseClicked(event -> date.setZonedDateTime(ZonedDateTime.now()));
        reset.disableProperty().bind(animator.runningProperty());
        chooseAnimator.disableProperty().bind(animator.runningProperty());
        reset.setFont(BUTTONS_FONT);
        // finally, assemble all the elements
        final HBox animatorBox = new HBox(chooseAnimator, reset, play);
        animatorBox.setStyle("-fx-spacing: inherit;");
        return animatorBox;
    }

    /**
     * @return the time box, allowing to change the date, time and zone.
     */
    private HBox createTimeBox() {
        // Date setup
        final Label dateLabel = new Label("Date :");
        final DatePicker dateText = new DatePicker();
        dateText.valueProperty().bindBidirectional(date.dateProperty());
        dateText.setStyle("-fx-pref-width: 120;");

        // Hour setup
        final Label hourLabel = new Label("Heure :");
        final TextField hourText = new TextField();
        hourText.setStyle("-fx-pref-width: 75; -fx-alignment: baseline-right;");

        // text formatters for date and hour
        final DateTimeFormatter hmsFormatter =
                DateTimeFormatter.ofPattern("HH:mm:ss");
        final LocalTimeStringConverter stringConverter =
                new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        final TextFormatter<LocalTime> timeFormatter =
                new TextFormatter<>(stringConverter);

        hourText.setTextFormatter(timeFormatter);
        timeFormatter.valueProperty().bindBidirectional(date.timeProperty());

        final List<String> zonesAvailable = new ArrayList<>(ZoneId.getAvailableZoneIds());
        // alphabetical (default) order
        Collections.sort(zonesAvailable);
        final ComboBox<String> zonesChoice = new ComboBox<>(FXCollections.observableList(zonesAvailable));
        zonesChoice.setStyle("-fx-pref-width: 180;");

        // update zone changes without binding it, because otherwise the animator
        // will not be able to change the value
        zonesChoice.valueProperty().addListener((observable, oldValue, newValue) -> date.zoneProperty().set(ZoneId.of(newValue)));
        zonesChoice.valueProperty().set(date.getZone().getId()); // set up initial value to avoid the blank box

        dateText.disableProperty().bind(animator.runningProperty());
        hourText.disableProperty().bind(animator.runningProperty());
        zonesChoice.disableProperty().bind(animator.runningProperty());

        final HBox timeBox = new HBox(dateLabel, dateText, hourLabel, hourText, zonesChoice);
        timeBox.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left");
        return timeBox;
    }

    /**
     * @return the position box, allowing to change user's position on Earth.
     */
    private HBox createPositionBox() {
        final Label lon = new Label("Longitude (°) :");
        final Label lat = new Label("Latitude (°) :");
        final TextField lonVal = new TextField();
        final TextField latVal = new TextField();
        lonVal.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right");
        latVal.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right");

        // text formatters for longitude and latitude
        final NumberStringConverter stringConverter = new NumberStringConverter("#0.00");

        // longitude
        final UnaryOperator<TextFormatter.Change> lonFilter = coordinatesFilter(GeographicCoordinates::isValidLatDeg,
                stringConverter);
        final TextFormatter<Number> lonTextFormatter =
                new TextFormatter<>(stringConverter, DEFAULT_LON, lonFilter);

        // latitude
        final UnaryOperator<TextFormatter.Change> latFilter = coordinatesFilter(GeographicCoordinates::isValidLatDeg,
                stringConverter);
        final TextFormatter<Number> latTextFormatter =
                new TextFormatter<>(stringConverter, DEFAULT_LAT, latFilter);

        lonVal.setTextFormatter(lonTextFormatter);
        latVal.setTextFormatter(latTextFormatter);
        position.longitudeProperty().bind(lonTextFormatter.valueProperty());
        position.latitudeProperty().bind(latTextFormatter.valueProperty());

        final HBox posControl = new HBox(lon, lonVal, lat, latVal);
        posControl.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left");

        return posControl;
    }

}
