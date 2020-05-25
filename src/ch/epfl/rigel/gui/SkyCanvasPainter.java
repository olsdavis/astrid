package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;

import java.util.List;
import java.util.Objects;

/**
 * This class handles the drawing to {@link javafx.scene.canvas.GraphicsContext}
 * from the sky information held by {@link ObservedSky}.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 11/04/2020
 */
public class SkyCanvasPainter {

    private static final double SUN_ANGLE = Angle.ofDeg(0.5d);

    /*
    The four following fields hold the names of the cardinal points.
    They are used for drawHorizon.
     */

    private static final String NORTH = "N";
    private static final String SOUTH = "S";
    private static final String WEST = "O"; // Ouest
    private static final String EAST = "E";

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

    /**
     * @param canvas the canvas to draw to
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = Objects.requireNonNull(canvas);
    }

    /**
     * Clears the canvas and paints the black background.
     */
    public void clear() {
        final GraphicsContext gfx = canvas.getGraphicsContext2D();
        gfx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gfx.setFill(Color.BLACK);
        gfx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Draws the stars and the asterisms to the canvas.
     *
     * @param sky        the observed sky to draw
     * @param projection the projection used to calculate the coordinates
     * @param transform  the transform to apply to all coordinates
     */
    public void drawStars(ObservedSky sky, StereographicProjection projection, Transform transform) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);
        Objects.requireNonNull(transform);

        final GraphicsContext gfx = canvas.getGraphicsContext2D();
        final double[] starPositions = sky.starPositions();
        // apply the transform
        transform.transform2DPoints(starPositions, 0, starPositions, 0, starPositions.length / 2);

        // draw asterisms first, then stars
        // set the stroke for all lines
        gfx.setStroke(Color.BLUE);
        gfx.setLineWidth(1d);
        for (Asterism asterism : sky.asterisms()) {
            final List<Integer> indices = asterism.indices(sky);
            gfx.beginPath();
            for (int i = 0; i < indices.size() - 1; i++) {
                final int current = indices.get(i);
                final int next = indices.get(i + 1);
                final Point2D pointA = new Point2D(starPositions[2 * current], starPositions[2 * current + 1]);
                final Point2D pointB = new Point2D(starPositions[2 * next], starPositions[2 * next + 1]);
                if (canvas.contains(pointA) || canvas.contains(pointB)) {
                    gfx.moveTo(pointA.getX(), pointA.getY());
                    gfx.lineTo(pointB.getX(), pointB.getY());
                    gfx.stroke();
                }
            }
            gfx.closePath();
        }

        // draw stars
        for (int i = 0; i < sky.stars().size(); i++) {
            final Star star = sky.stars().get(i);
            gfx.setFill(BlackBodyColor.fromTemperature(star.colorTemperature()));
            drawUsual(star, starPositions[2 * i], starPositions[2 * i + 1], transform, projection);
        }
    }

    /**
     * Draws the planets to the canvas.
     *
     * @param sky        the observed sky to draw
     * @param projection the projection used to calculate the coordinates
     * @param transform  the transform to apply to all coordinates
     */
    public void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform transform) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);
        Objects.requireNonNull(transform);

        final GraphicsContext gfx = canvas.getGraphicsContext2D();
        final double[] planetPositions = sky.planetPositions();
        // apply the transform
        transform.transform2DPoints(planetPositions, 0, planetPositions, 0, planetPositions.length / 2);

        gfx.setFill(Color.LIGHTGRAY);
        for (int i = 0; i < sky.planets().size(); i++) {
            drawUsual(sky.planets().get(i), planetPositions[2 * i], planetPositions[2 * i + 1], transform, projection);
        }
    }

    /**
     * Draws the provided object as a circle of the provided information.
     * The fill color must be set beforehand.
     *
     * @param object     the CelestialObject representation
     * @param x          the x-coordinate on the plane
     * @param y          the y-coordinate on the plane
     * @param transform  the transform to apply
     * @param projection the projection to apply
     */
    private void drawUsual(CelestialObject object, double x, double y, Transform transform, StereographicProjection projection) {
        final Point2D point = new Point2D(x, y);
        final double diameter = Math.abs(
                transform.deltaTransform(objectRadius(object.magnitude(), projection), 0).getX()
        );
        canvas.getGraphicsContext2D().fillOval(point.getX() - diameter / 2d, point.getY() - diameter / 2d, diameter, diameter);
    }

    /**
     * Draws the Moon to the canvas.
     *
     * @param sky        the observed sky to draw
     * @param projection the projection used to calculate the coordinates
     * @param transform  the transform to apply to all coordinates
     */
    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform transform) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);
        Objects.requireNonNull(transform);

        final GraphicsContext gfx = canvas.getGraphicsContext2D();
        gfx.setFill(Color.WHITE);
        final Point2D point = transform.transform(sky.moonPosition().x(), sky.moonPosition().y());
        final double radius = Math.abs(
                transform.deltaTransform(projection.applyToAngle(sky.moon().angularSize()), 0).getX()
        ) / 2d;
        gfx.fillOval(point.getX() - radius, point.getY() - radius, 2 * radius, 2 * radius);
    }

    /**
     * Draws the Sun to the canvas.
     *
     * @param sky        the observed sky to draw
     * @param projection the projection used to calculate the coordinates
     * @param transform  the transform to apply to all coordinates
     */
    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform transform) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);
        Objects.requireNonNull(transform);

        final GraphicsContext gfx = canvas.getGraphicsContext2D();
        final Point2D point = transform.transform(sky.sunPosition().x(), sky.sunPosition().y());
        if (canvas.contains(point)) {
            final double diameter = Math.abs(
                    transform.deltaTransform(projection.applyToAngle(sky.sun().angularSize()), 0).getX()
            );
            gfx.setFill(Color.YELLOW.deriveColor(0, 1d, 1d, 0.25d));
            gfx.fillOval(point.getX() - ((diameter / 2d) * 2.2d), point.getY() - ((diameter / 2d) * 2.2d), diameter * 2.2d, diameter * 2.2d);
            gfx.setFill(Color.YELLOW);
            gfx.fillOval(point.getX() - (diameter + 2d) / 2d, point.getY() - (diameter + 2d) / 2d, diameter + 2d, diameter + 2d);
            gfx.setFill(Color.WHITE);
            gfx.fillOval(point.getX() - diameter / 2d, point.getY() - diameter / 2d, diameter, diameter);
        }
    }

    /**
     * Draws the horizon line and the cardinal points.
     *
     * @param sky        the observed sky to draw
     * @param projection the projection used to calculate the coordinates
     * @param transform  the transform to apply to all coordinates
     */
    public void drawHorizon(ObservedSky sky, StereographicProjection projection, Transform transform) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);
        Objects.requireNonNull(transform);

        final GraphicsContext gfx = canvas.getGraphicsContext2D();
        // draw the horizon line
        gfx.setStroke(Color.RED);
        gfx.setLineWidth(2d);
        final CartesianCoordinates center = projection.circleCenterForParallel(HorizontalCoordinates.ofDeg(0, 0));
        final double radius = Math.abs(
                transform.deltaTransform(projection.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0, 0)), 0).getX()
        );
        final Point2D point = transform.transform(center.x(), center.y());
        gfx.strokeOval(point.getX() - radius, point.getY() - radius, radius * 2d, radius * 2d);

        // draw the cardinal points
        gfx.setFill(Color.RED);
        gfx.setTextAlign(TextAlignment.CENTER);
        gfx.setTextBaseline(VPos.TOP);
        for (int i = 0; i < 360; i += 45) {
            final HorizontalCoordinates coordinates = HorizontalCoordinates.ofDeg(i, -0.5d);
            final CartesianCoordinates raw = projection.apply(coordinates);
            final Point2D projected = transform.transform(raw.x(), raw.y());
            if (canvas.contains(projected)) {
                gfx.fillText(
                        coordinates.azOctantName(NORTH, EAST, SOUTH, WEST),
                        projected.getX(), projected.getY()
                );
            }
        }
    }

}
