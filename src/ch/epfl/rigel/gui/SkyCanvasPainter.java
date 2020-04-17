package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

import java.util.List;
import java.util.Objects;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 11/04/2020
 */
public class SkyCanvasPainter {

    private static final double SUN_ANGLE = Angle.ofDeg(0.5d);

    /**
     * This interval represents the values we keep for the magnitude of a
     * celestial object drawn on the canvas, since most of the magnitudes are
     * in between the bounds of this interval.
     */
    private static final ClosedInterval MAGNITUDE_CLIP = ClosedInterval.of(-2d, 5d);

    /**
     * @param magnitude the magnitude of a CelestialObject
     * @return the radius of the circle representing a CelestialObject
     * according to its provided magnitude {@code magnitude}.
     */
    private static double objectRadius(double magnitude, StereographicProjection projection) {
        final double clipped = MAGNITUDE_CLIP.clip(magnitude);
        final double scaleFactor = (99d - 17d * clipped) / 140d;
        return scaleFactor * projection.applyToAngle(SUN_ANGLE);
    }

    private final Canvas canvas;
    private final Transform correctionTransform;

    /**
     * @param canvas the canvas to draw to
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = Objects.requireNonNull(canvas);
        //TODO: calculate the real value of the scale factor, in a future step
        correctionTransform = Transform.affine(1300, 0, 0, -1300, canvas.getWidth() / 2d, canvas.getHeight() / 2d);
    }

    /**
     * Clears the canvas.
     */
    public void clear() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Draws the stars and the asterisms to the canvas.
     *
     * @param sky        the observed sky to draw
     * @param projection the projection used to calculate the coordinates
     */
    public void drawStars(ObservedSky sky, StereographicProjection projection) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);

        final double[] starPositions = sky.starPositions();
        //Change coordinates system to adapt to the canvas.
        correctionTransform.transform2DPoints(starPositions, 0, starPositions, 0, starPositions.length / 2);


        // draw asterisms first, then stars

        // set the stroke for all lines
        canvas.getGraphicsContext2D().setStroke(Color.BLUE);
        canvas.getGraphicsContext2D().setLineWidth(1d);
        for (Asterism asterism : sky.asterisms()) {
            final List<Integer> indices = sky.asterismIndices(asterism);
            canvas.getGraphicsContext2D().beginPath();
            for (int i = 0; i < indices.size() - 1; i++) {
                final int current = indices.get(i);
                final int next = indices.get(i + 1);
                final Point2D pointA = new Point2D(starPositions[2 * current], starPositions[2 * current + 1]);
                final Point2D pointB = new Point2D(starPositions[2 * next], starPositions[2 * next + 1]);
                if (canvas.contains(pointA) || canvas.contains(pointB)) {
                    if (i == 0) {
                        canvas.getGraphicsContext2D().moveTo(pointA.getX(), pointA.getY());
                    } else {
                        canvas.getGraphicsContext2D().lineTo(pointA.getX(), pointA.getY());
                    }
                    canvas.getGraphicsContext2D().lineTo(pointB.getX(), pointB.getY());
                    canvas.getGraphicsContext2D().stroke();

                }
            }
            canvas.getGraphicsContext2D().closePath();
        }

        // draw stars
        for (int i = 0; i < sky.stars().size(); i++) {
            final Star star = sky.stars().get(i);
            final Point2D point = new Point2D(starPositions[2 * i], starPositions[2 * i + 1]);
            final double diameter = correctionTransform.deltaTransform(objectRadius(star.magnitude(), projection), 0).getX();
            canvas.getGraphicsContext2D().setFill(BlackBodyColor.fromTemperature(star.colorTemperature()));
            canvas.getGraphicsContext2D().fillOval(point.getX() - diameter / 2d, point.getY() - diameter / 2d, diameter, diameter);
        }
    }

    /**
     * Draws the planets to the canvas.
     *
     * @param sky        the observed sky to draw
     * @param projection the projection used to calculate the coordinates
     */
    public void drawPlanets(ObservedSky sky, StereographicProjection projection) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);
        final double[] planetPositions = sky.planetPositions();
        //Change system coordinates to adapt to the canvas.
        correctionTransform.transform2DPoints(planetPositions, 0, planetPositions, 0, planetPositions.length / 2);

        //set fill for all planets
        //BONUS: modify the fill depending on the planet to get more adequate colors
        canvas.getGraphicsContext2D().setFill(Color.LIGHTGRAY);

        for (int i = 0; i < sky.planets().size(); i++) {
            final Planet planet = sky.planets().get(i);
            final Point2D point = new Point2D(planetPositions[2 * i], planetPositions[2 * i + 1]);
            final double diameter = correctionTransform.deltaTransform(objectRadius(planet.magnitude(), projection), 0).getX();
            canvas.getGraphicsContext2D().fillOval(point.getX() - diameter / 2d, point.getY() - diameter / 2d, diameter, diameter);
        }
    }

    /**
     * Draws the Moon to the canvas.
     *
     * @param sky        the observed sky to draw
     * @param projection the projection used to calculate the coordinates
     */
    public void drawMoon(ObservedSky sky, StereographicProjection projection) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);
        canvas.getGraphicsContext2D().setFill(Color.WHITE);
        final Point2D point = correctionTransform.transform(sky.moonPosition().x(), sky.moonPosition().y());
        final double radius = correctionTransform.deltaTransform(sky.moon().angularSize() / 2d, 0).getX();
        canvas.getGraphicsContext2D().fillOval(point.getX() - radius, point.getY() - radius, 2 * radius, 2 * radius);
    }

    /**
     * Draws the Sun to the canvas.
     *
     * @param sky        the osberved sky to draw
     * @param projection the projection used to calculate the coordinates
     */
    public void drawSun(ObservedSky sky, StereographicProjection projection) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);

        final Point2D point = correctionTransform.transform(sky.sunPosition().x(), sky.sunPosition().y());
        if (canvas.contains(point)) {
            final double diameter = correctionTransform.deltaTransform(sky.sun().angularSize(), 0).getX();
            canvas.getGraphicsContext2D().setFill(Color.YELLOW.deriveColor(0, 1d, 1d, 0.25d));
            canvas.getGraphicsContext2D().fillOval(point.getX() - ((diameter / 2d) * 2.2d), point.getY() - ((diameter / 2d) * 2.2d), diameter * 2.2d, diameter * 2.2d);
            canvas.getGraphicsContext2D().setFill(Color.YELLOW);
            canvas.getGraphicsContext2D().fillOval(point.getX() - (diameter + 2d) / 2d, point.getY() - (diameter + 2d) / 2d, diameter + 2d, diameter + 2d);
            canvas.getGraphicsContext2D().setFill(Color.WHITE);
            canvas.getGraphicsContext2D().fillOval(point.getX() - diameter / 2d, point.getY() - diameter / 2d, diameter, diameter);
        }
    }

    /**
     * Draws the horizon line and the cardinal points.
     *
     * @param sky        the osberved sky to draw
     * @param projection the projection used to calculate the coordinates
     */
    public void drawHorizon(ObservedSky sky, StereographicProjection projection) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);

        canvas.getGraphicsContext2D().setStroke(Color.RED);
        canvas.getGraphicsContext2D().setLineWidth(2d);
        final CartesianCoordinates center = projection.circleCenterForParallel(HorizontalCoordinates.ofDeg(0, 0));
        final double radius = correctionTransform.deltaTransform(projection.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0, 0)), 0).getX();
        final Point2D point = correctionTransform.transform(center.x(), center.y());
        canvas.getGraphicsContext2D().strokeOval(point.getX() - radius, point.getY() - radius, radius * 2d, radius * 2d);
        // TODO: cardinal points
    }
}
