package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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

            Transform t =
                    Transform.affine(1300, 0, 0, -1300, 400, 300);
            ZonedDateTime when =
                    ZonedDateTime.parse("2020-02-17T20:15:00+01:00");
            GeographicCoordinates where =
                    GeographicCoordinates.ofDeg(6.57, 46.52);
            HorizontalCoordinates projCenter =
                    HorizontalCoordinates.ofDeg(0, 22.5);
            StereographicProjection projection =
                    new StereographicProjection(projCenter);
            ObservedSky sky =
                    new ObservedSky(when, where, projection, catalogue);

            Canvas canvas =
                    new Canvas(800, 600);
            SkyCanvasPainter painter =
                    new SkyCanvasPainter(canvas);

            painter.clear();
            // true order: stars, planets, sun, moon, horizon
            painter.drawStars(sky, projection, t, true, true);
            painter.drawPlanets(sky, projection, t);
            painter.drawSun(sky, projection, t);
            painter.drawMoon(sky, projection, t);
            painter.drawHorizon(sky, projection, t);
            canvas.getGraphicsContext2D().moveTo(400, 0);
            canvas.getGraphicsContext2D().lineTo(400, 600);
            canvas.getGraphicsContext2D().stroke();

            WritableImage fxImage =
                    canvas.snapshot(null, null);
            BufferedImage swingImage =
                    SwingFXUtils.fromFXImage(fxImage, null);
            ImageIO.write(swingImage, "png", new File("sky.png"));
        }
        Platform.exit();
    }
}
