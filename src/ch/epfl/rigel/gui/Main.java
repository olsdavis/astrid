package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 06/05/2020
 */
public class Main extends Application {

    /**
     * Holds the font used for buttons, such as the resume/pause button.
     */
    private static final Font BUTTONS_FONT;

    static {
        final InputStream stream = Main.class.getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf");
        BUTTONS_FONT = Font.loadFont(stream, 15);
        try {
            stream.close();
        } catch (IOException e) {
            // ignore the exception, should not happen
        }
    }

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
     * Initial time and date.
     */
    private static final ZonedDateTime INIT_TIME = ZonedDateTime.parse("2020-02-17T20:15:00+01:00");
    /**
     * Initial coordinates of the user.
     */
    private static final GeographicCoordinates INIT_COORDINATES = GeographicCoordinates.ofDeg(6.57, 46.52);
    /**
     * The initial projection center.
     */
    private static final HorizontalCoordinates INIT_PROJ_CENTER = HorizontalCoordinates.ofDeg(180.000000000001, 15);
    /**
     * Holds the default longitude for the observer's position.
     */
    private static final Number DEFAULT_LON = 6.57;
    /**
     * Holds the default latitude for the observer's position.
     */
    private static final Number DEFAULT_LAT = 46.52d;

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

    private final ObserverLocationBean position = new ObserverLocationBean();
    private final DateTimeBean date = new DateTimeBean();
    private final ViewingParametersBean viewingParameters = new ViewingParametersBean();
    private final TimeAnimator animator = new TimeAnimator(date);
    private SkyCanvasManager manager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rigel");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Initialize beans
        position.setCoordinates(INIT_COORDINATES);
        date.setZonedDateTime(INIT_TIME);
        viewingParameters.setCenter(INIT_PROJ_CENTER);
        viewingParameters.setFieldOfViewDeg(INIT_FOV);

        try (InputStream hs = getClass().getResourceAsStream("/hygdata_v3.csv")) {
            manager = new SkyCanvasManager(
                    new StarCatalogue.Builder()
                            .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                            .build(),
                    date,
                    position,
                    viewingParameters
            );
        } catch (Exception e) {
            // ignore, should not occur
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

        final Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        manager.canvas().requestFocus();
    }

    /**
     * @return the bottom pane of the window.
     */
    private BorderPane bottomPane() {
        // set up the celestial object under mouse
        final Text underMouse = new Text("");
        underMouse.textProperty().bind(Bindings.createStringBinding(() -> {
            final CelestialObject object = manager.objectUnderMouseProperty().get();
            if (object == null) {
                return "";
            } else {
                return object.toString();
            }
        }, manager.objectUnderMouseProperty()));
        final BorderPane bottom = new BorderPane(underMouse);
        bottom.setStyle("-fx-padding: 4; -fx-background-color: #ffffff;");
        bottom.setCenter(underMouse);
        // set up the field of view text
        final Text fov = new Text("");
        fov.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Champ de vue : %.1f°", viewingParameters.getFieldOfView()),
                viewingParameters.getFieldOfViewProperty())
        );
        bottom.setLeft(fov);
        // set up the mouse position in horizontal coordinates text
        final Text mouseHorizontal = new Text("");
        mouseHorizontal.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Azimut : %.1f°, hauteur : %.1f°",
                        manager.mouseAzimuth(), manager.mouseAltitude()),
                manager.mouseAzimuthProperty(), manager.mouseAltitudeProperty()
        ));
        bottom.setRight(mouseHorizontal);
        return bottom;
    }

    /**
     * @return the upper control bar.
     */
    private HBox controlBar() {
        final HBox controlBar = new HBox(createPositionBox(), new Separator(Orientation.VERTICAL),
                createTimeBox(), new Separator(Orientation.VERTICAL), createAnimatorBox());
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlBar;
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

        // TODO: check for the default zone
        final ComboBox<String> zonesChoice = new ComboBox<>(FXCollections.observableList(zonesAvailable));
        zonesChoice.setStyle("-fx-pref-width: 180;");

        // update zone changes without binding it, because otherwise the animator
        // will not be able to change the value
        zonesChoice.valueProperty().addListener((observable, oldValue, newValue) -> date.zoneProperty().set(ZoneId.of(newValue)));
        zonesChoice.valueProperty().set(date.getZone().getId()); // set up initial value to avoid the blank box

        // TODO disable date and time selections when simulation is running.

        final HBox timeBox = new HBox(dateLabel, dateText, hourLabel, hourText, zonesChoice);
        timeBox.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left");
        return timeBox;
    }

    /**
     * @return the animator box, which allows time simulation in different speeds.
     */
    private HBox createAnimatorBox() {
        final ChoiceBox<NamedTimeAccelerator> animatorChoice = new ChoiceBox<>();
        animatorChoice.setItems(FXCollections.observableList(Arrays.asList(NamedTimeAccelerator.values())));
        animatorChoice.valueProperty().addListener((observable, oldValue, newValue) ->
                animator.setAccelerator(newValue.getAccelerator()));
        animatorChoice.setValue(NamedTimeAccelerator.TIMES_1);
        final HBox chooseAnimator = new HBox(animatorChoice);
        chooseAnimator.setStyle("-fx-spacing: inherit;");
        final Button play = new Button(PLAY_CHARACTER);
        play.setOnMouseClicked(event -> animator.runningProperty().set(!animator.runningProperty().get()));
        play.setFont(BUTTONS_FONT);
        play.textProperty().bind(Bindings.createStringBinding(
                () -> animator.runningProperty().get() ? PAUSE_CHARACTER : PLAY_CHARACTER,
                animator.runningProperty())
        );
        return new HBox(chooseAnimator, play);
    }

}
