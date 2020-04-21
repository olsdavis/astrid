package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.ZonedDateTime;

public final class DrawSky extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try (InputStream hs = resourceStream("/hygdata_v3.csv")) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(resourceStream("/asterisms.txt"), AsterismLoader.INSTANCE)
                    .build();

            ZonedDateTime when =
                    ZonedDateTime.parse("2020-02-17T20:15:00+01:00");
            GeographicCoordinates where =
                    GeographicCoordinates.ofDeg(6.57, 46.52);
            HorizontalCoordinates projCenter =
                    HorizontalCoordinates.ofDeg(180, 22.5);
            StereographicProjection projection =
                    new StereographicProjection(projCenter);
            ObservedSky sky =
                    new ObservedSky(when, where, projection, catalogue);

            Canvas canvas =
                    new Canvas(800, 600);
            SkyCanvasPainter painter =
                    new SkyCanvasPainter(canvas);

            painter.clear();
            painter.drawStars(sky, projection);
            painter.drawPlanets(sky, projection);
            painter.drawMoon(sky, projection);
            painter.drawSun(sky, projection);
            painter.drawHorizon(sky, projection);

            Scene scene = new Scene(new BorderPane(canvas));
            primaryStage.setScene(scene);
            primaryStage.show();
//            WritableImage fxImage =
//                    canvas.snapshot(null, null);
//            BufferedImage swingImage =
//                    SwingFXUtils.fromFXImage(fxImage, null);
//            ImageIO.write(swingImage, "png", new File("sky.png"));
        }
    }
}
