package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.UnaryOperator;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 06/05/2020
 */
public class Main extends Application {

    private HBox controlBar;
    private HBox whereController;
    private HBox whenController = new HBox();
    private HBox timeController = new HBox();

    private final ObserverLocationBean position = new ObserverLocationBean();
    private final DateTimeBean date = new DateTimeBean();
    private final ViewingParametersBean parameters = new ViewingParametersBean();
    private SkyCanvasManager manager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rigel");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);

        //Initialize beans
        position.setCoordinates(GeographicCoordinates.ofDeg(6.57, 46.52));
        date.setZonedDateTime(ZonedDateTime.parse("2020-02-17T20:15:00+01:00"));
        date.setDate(LocalDate.of(2020, 6, 8));
        parameters.setCenter(HorizontalCoordinates.ofDeg(180.000000000001, 15));
        parameters.setFieldOfViewDeg(100);

        try (InputStream hs = getClass().getResourceAsStream("/hygdata_v3.csv")) {
            manager = new SkyCanvasManager(new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .build(), date, position, parameters);
        } catch (Exception e) {
            return;
        }

        BorderPane root = new BorderPane();
        controlBar();
        manager.canvas().widthProperty().bind(root.widthProperty());
        manager.canvas().heightProperty().bind(root.heightProperty());
        root.setTop(controlBar);
        Pane p = new Pane(manager.canvas());
        root.setCenter(p);
        root.setBottom(bottomPane());

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        manager.canvas().requestFocus();
    }

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
                () -> String.format("Champ de vue : %.1f°", parameters.getFieldOfView()),
                parameters.getFieldOfViewProperty())
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

    private void controlBar() {
        whereController = setupPositionBox();
        whenController = setupTimeBox();
        controlBar = new HBox(whereController, new Separator(Orientation.VERTICAL), whenController, new Separator(Orientation.VERTICAL), timeController);
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
    }

    private HBox setupPositionBox() {
        final Label lon = new Label("Longitude (°) :");
        final Label lat = new Label("Latitude (°) :");
        final TextField lonVal = new TextField();
        final TextField latVal = new TextField();
        lonVal.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right");
        latVal.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right");

        //text formatters for longitude and latitude
        final NumberStringConverter stringConverter = new NumberStringConverter("#0.00");

        //longitude
        UnaryOperator<TextFormatter.Change> lonFilter = (change -> {
            try {
                final String newText =
                        change.getControlNewText();
                final double newLonDeg =
                        stringConverter.fromString(newText).doubleValue();
                return GeographicCoordinates.isValidLonDeg(newLonDeg)
                        ? change
                        : null;
            } catch (Exception e) {
                return null;
            }
        });
        final TextFormatter<Number> lonTextFormatter =
                new TextFormatter<>(stringConverter, 6.57d, lonFilter);

        //latitude
        UnaryOperator<TextFormatter.Change> latFilter = (change -> {
            try {
                final String newText =
                        change.getControlNewText();
                final double newLatDeg =
                        stringConverter.fromString(newText).doubleValue();
                return GeographicCoordinates.isValidLatDeg(newLatDeg)
                        ? change
                        : null;
            } catch (Exception e) {
                return null;
            }
        });

        final TextFormatter<Number> latTextFormatter =
                new TextFormatter<>(stringConverter, 46.52d, latFilter);

        lonVal.setTextFormatter(lonTextFormatter);
        latVal.setTextFormatter(latTextFormatter);
        position.longitudeProperty().bindBidirectional(lonTextFormatter.valueProperty());
        position.latitudeProperty().bindBidirectional(latTextFormatter.valueProperty());

        final HBox posControl = new HBox(lon, lonVal, lat, latVal);
        posControl.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left");

        return posControl;
    }


    private HBox setupTimeBox() {
        //Date setup
        Label dateLabel = new Label("Date :");
        DatePicker dateText = new DatePicker();
        //date.dateProperty().bind(dateText.valueProperty());
        dateText.setStyle("-fx-pref-width: 120;");

        //Hour setup
        Label hourLabel = new Label("Heure :");
        TextField hourText = new TextField();
        hourText.setStyle("-fx-pref-width: 75; -fx-alignment: baseline-right;");

        //text formatters for date and hour
        DateTimeFormatter hmsFormatter =
                DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter =
                new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        TextFormatter<LocalTime> timeFormatter =
                new TextFormatter<>(stringConverter);

        //date.timeProperty().bind(timeFormatter.valueProperty());

        ArrayList<String> zonesAvailable = new ArrayList<>(ZoneId.getAvailableZoneIds());
        Collections.sort(zonesAvailable);

        ObservableList<String> zoneList = FXCollections.observableList(zonesAvailable);
        ComboBox<String> zones = new ComboBox<>(zoneList);
        zones.setStyle("-fx-pref-width: 180;");
        //date.zoneProperty().bind(Bindings.createObjectBinding(() -> ZoneId.of(zones.getValue())));

        //TODO disable date and time selections when simulation is running.


        final HBox timeBox= new HBox(dateLabel, dateText, hourLabel, hourText, zones);
        timeBox.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left");

        return timeBox;
    }

}
