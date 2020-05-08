package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.function.UnaryOperator;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 06/05/2020
 */
public class Main extends Application {

    private BorderPane root;
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

        position.setCoordinates(GeographicCoordinates.ofDeg(6.57, 46.52));
        date.setZonedDateTime(ZonedDateTime.parse("2020-02-17T20:15:00+01:00"));
        parameters.setCenter(HorizontalCoordinates.ofDeg(180.000000000001, 15));
        parameters.setFieldOfViewDeg(100);
        try (InputStream hs = getClass().getResourceAsStream("/hygdata_v3.csv")) {
            manager = new SkyCanvasManager(new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .build(), date, position, parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        BorderPane root = new BorderPane();
        controlBar();
        manager.canvas().widthProperty().bind(root.widthProperty());
        manager.canvas().heightProperty().bind(root.heightProperty());
        root.setTop(controlBar);
        Pane p = new Pane(manager.canvas());
        root.setCenter(p);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        manager.canvas().requestFocus();
    }

    private void controlBar() {
        whereController = setupPositionBox();
        controlBar = new HBox(whereController, whenController, timeController);
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

}
