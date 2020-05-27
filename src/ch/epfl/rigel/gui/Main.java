package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.gui.screen.MainScreen;
import ch.epfl.rigel.gui.screen.ScreenController;
import ch.epfl.rigel.gui.screen.ScreenNames;
import ch.epfl.rigel.gui.screen.StarViewScreen;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;
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

    public static void main(String[] args) {
        launch(args);
    }

    private StarCatalogue catalogue;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rigel");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // initialize catalogue
        try (final InputStream hs = getClass().getResourceAsStream("/hygdata_v3.csv");
             final InputStream as = getClass().getResourceAsStream("/asterisms.txt")) {
            catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(as, AsterismLoader.INSTANCE)
                    .build();
        } catch (Exception e) {
            Logger.getLogger("Rigel").log(Level.SEVERE,
                    "Could not load the hyg database and/or the asterisms file, stack trace:", e);
            System.exit(1); // exit with error
        }

        final ScreenController controller = new ScreenController();
        controller.addScreen(new MainScreen(controller));
        controller.addScreen(new StarViewScreen(catalogue));

        controller.changeScreen(ScreenNames.MAIN_SCREEN);

        final BorderPane lastPane = new BorderPane();
        final HBox top = new HBox();
        top.setAlignment(Pos.CENTER_RIGHT);
        // todo: setup top buttons
        lastPane.setTop(top);
        lastPane.centerProperty().bind(Bindings.createObjectBinding(
                () -> controller.getCurrentScreen().get().getPane(), controller.getCurrentScreen()
        ));
        final Scene scene = new Scene(lastPane);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
