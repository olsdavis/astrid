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

    private ObserverLocationBean position = new ObserverLocationBean();
    private DateTimeBean date = new DateTimeBean();
    private ViewingParametersBean parameters = new ViewingParametersBean();
    private SkyCanvasManager manager;


    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rigel");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);

        position.setCoordinates(GeographicCoordinates.ofDeg(6.57, 46.52));
        date.setZonedDateTime(ZonedDateTime.parse("2020-02-17T20:15:00+01:00"));
        parameters.setCenter(HorizontalCoordinates.ofDeg(180.000000000001, 42));
        parameters.setFieldOfViewDeg(100);
        try(InputStream hs = getClass().getResourceAsStream("/hygdata_v3.csv")){
           manager = new SkyCanvasManager(new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .build(), date,position, parameters);
        }catch (Exception e){
            return;
        }


        BorderPane root = new BorderPane();
        controlBar(manager.canvas());
        root.setTop(controlBar);
        Pane p = new Pane(manager.canvas());
        p.setMinSize(root.);
        p.setMinHeight(450);
        root.setCenter(p);
        primaryStage.setScene(new Scene(root));
        manager.canvas().requestFocus();
        primaryStage.setY(100);
        primaryStage.show();

    }

    private void controlBar(Canvas canvas) {
        whereController = setupPositionBox();
        controlBar = new HBox(whereController, whenController, timeController);
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");

    }


    private HBox setupPositionBox() {
        Label lon = new Label("Longitude (°) :");
        Label lat = new Label("Latitude (°) :");
        TextField lonVal = new TextField();
        TextField latVal = new TextField();
        lonVal.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right");
        latVal.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right");

        //text formatters for longitude and latitude
        NumberStringConverter stringConverter =
                new NumberStringConverter("#0.00");

        //longitude
        UnaryOperator<TextFormatter.Change> lonFilter = (change -> {
            try {
                String newText =
                        change.getControlNewText();
                double newLonDeg =
                        stringConverter.fromString(newText).doubleValue();
                return GeographicCoordinates.isValidLonDeg(newLonDeg)
                        ? change
                        : null;
            } catch (Exception e) {
                return null;
            }
        });
        TextFormatter<Number> lonTextFormatter =
                new TextFormatter<>(stringConverter, 0, lonFilter);



        //latitude
        UnaryOperator<TextFormatter.Change> latFilter = (change -> {
            try {
                String newText =
                        change.getControlNewText();
                double newLatDeg =
                        stringConverter.fromString(newText).doubleValue();
                return GeographicCoordinates.isValidLatDeg(newLatDeg)
                        ? change
                        : null;
            } catch (Exception e) {
                return null;
            }
        });

        TextFormatter<Number> latTextFormatter =
                new TextFormatter<>(stringConverter, 0, latFilter);

        lonVal.setTextFormatter(lonTextFormatter);
        latVal.setTextFormatter(latTextFormatter);
        position.longitudeProperty().bindBidirectional(lonTextFormatter.valueProperty());
        position.latitudeProperty().bindBidirectional(latTextFormatter.valueProperty());

        HBox posControl = new HBox(lon, lonVal, lat, latVal);
        posControl.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left");

        return posControl;
    }
}
