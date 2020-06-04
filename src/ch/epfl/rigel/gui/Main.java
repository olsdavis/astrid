package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.gui.screen.EntranceScreen;
import ch.epfl.rigel.gui.screen.ScreenController;
import ch.epfl.rigel.gui.screen.ScreenNames;
import ch.epfl.rigel.gui.screen.StarViewScreen;
import ch.epfl.rigel.storage.FavoritesList;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

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

    private static final String FAVORITES_PATH = "favorites.data";
    /**
     * The initial width of the screen.
     */
    public static final float INITIAL_WIDTH = 1000f;
    /**
     * The initial height of the screen.
     */
    public static final float INITIAL_HEIGHT = 750f;

    private static final class SaveProcedure implements Runnable {
        private final FavoritesList list;

        /**
         * @param list the list to save
         */
        SaveProcedure(FavoritesList list) {
            this.list = list;
        }

        @Override
        public void run() {
            try {
                list.save();
            } catch (Exception e) {
                Logger.getLogger("Rigel").log(Level.SEVERE, "Could not save favorites list, stack trace:", e);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rigel");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setWidth(INITIAL_WIDTH);
        primaryStage.setHeight(INITIAL_HEIGHT);

        StarCatalogue catalogue = null;
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

        FavoritesList list = null;
        try {
            list = new FavoritesList(FAVORITES_PATH);
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Le fichier des favoris n'a pu être lu." +
                    " Les modifications que vous apporterez ne seront pas sauvegardées.").show();
            try {
                list = new FavoritesList("");
            } catch (Exception exc) {
                // should not happen
                Logger.getLogger("Rigel").log(Level.SEVERE,
                        "Could not create a simple non-persistent favorites list, stack trace:", e);
                System.exit(1);
            }
        }

        final ScreenController controller = new ScreenController();
        controller.addScreen(new EntranceScreen(controller));
        controller.addScreen(new StarViewScreen(catalogue, list));

        // set to main screen
        controller.changeScreen(ScreenNames.ENTRANCE_SCREEN);

        final Scene scene = new Scene(controller.currentScreenProperty().get().getPane());
        scene.rootProperty().bind(Bindings.createObjectBinding(
                () -> controller.currentScreenProperty().get().getPane(),
                controller.currentScreenProperty())
        );
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        final SaveProcedure save = new SaveProcedure(list);
        primaryStage.setOnCloseRequest(event -> save.run());
    }

}
