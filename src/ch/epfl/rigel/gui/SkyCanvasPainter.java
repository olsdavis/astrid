package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
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

    /**
     * Since the sun's size doesn't vary much according to one's position on
     * Earth, we store it as a constant which approximates it well.
     */
    private static final double SUN_SIZE = 2 * Math.tan(Angle.ofDeg(0.5d) / 4d);
    //TODO: calculate this in the method directly with projection.applyToAngle ?

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
    private static double objectRadius(double magnitude) {
        final double clipped = MAGNITUDE_CLIP.clip(magnitude);
        final double scaleFactor = (99d - 17d * clipped) / 140d;
        return scaleFactor * SUN_SIZE;
    }

    private final Canvas canvas;
    private final Transform correctionTransform;

    /**
     * @param canvas the canvas to draw to
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = Objects.requireNonNull(canvas);
        //TODO: calculate the real value of the scale factor, in a future step
        correctionTransform = Transform.affine(1300, 0, 0, -1300, 400, 300);
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
     * @param projection the projection used to calculate the cyoordinates
     */
    public void drawStars(ObservedSky sky, StereographicProjection projection) {
        Objects.requireNonNull(sky);
        Objects.requireNonNull(projection);

        // draw stars
        final double[] starPositions = sky.starPositions();
        correctionTransform.transform2DPoints(starPositions, 0, starPositions, 0, starPositions.length / 2);
        for (int i = 0; i < sky.stars().size(); i++) {
            final Star star = sky.stars().get(i);
            final Point2D point = new Point2D(starPositions[2 * i], starPositions[2 * i + 1]);
            final double diameter = correctionTransform.deltaTransform(objectRadius(star.magnitude()), 0).getX();
            canvas.getGraphicsContext2D().setFill(BlackBodyColor.fromTemperature(star.colorTemperature()));
            canvas.getGraphicsContext2D().fillOval(point.getX() - diameter / 2d, point.getY() - diameter / 2d, diameter, diameter);
        }

        //TODO:
        // draw asterisms

        // set the fill for all lines
        canvas.getGraphicsContext2D().setFill(Color.BLUE);
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
                }
            }
            canvas.getGraphicsContext2D().closePath();
        }
    }

}
