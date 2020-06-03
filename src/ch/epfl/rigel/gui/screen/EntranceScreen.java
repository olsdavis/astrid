package ch.epfl.rigel.gui.screen;

import ch.epfl.rigel.gui.Main;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents the first screen the user sees.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public final class EntranceScreen extends Screen {

    /**
     * We created a small animation running in background of the start screen,
     * so you can see it by setting this value to {@code true}. Yet, we found
     * it pretty unpleasant to startup the program with already some heavy calculations
     * for the display.
     */
    private static final boolean ANIMATION_ENABLED = false;

    private static final class BackgroundAnimation extends AnimationTimer {
        private static final float NANOS_TO_SECOND = 1_000_000_000f;

        /**
         * Represents a ball on the screen.
         */
        private static final class Ball {
            private static final float MIN_OPACITY = 0.2f;
            private static final float MAX_OPACITY = 0.8f;
            private static final ClosedInterval OPACITY_INTERVAL = ClosedInterval.of(MIN_OPACITY, MAX_OPACITY);
            // here, x and y float coordinates are used instead of CartesianCoordinates,
            // because, for animations, it costs much less to just mutate values to move
            // the displayed object
            private final float x;
            private float y;
            private final float speed;
            private final float size;
            private final float opacitySpeed;
            private float opacity;
            // true if the opacity of the object should grow
            // false, otherwise
            private boolean opacityGrow = true;

            /**
             * @param x            the starting x-coordinate
             * @param y            the starting y-coordinate
             * @param speed        the speed of the ball
             * @param size         the size of the ball
             * @param opacitySpeed the speed of the opacity change
             */
            private Ball(float x, float y, float speed, float size, float opacitySpeed) {
                this.x = x;
                this.y = y;
                this.speed = speed;
                this.size = size;
                this.opacitySpeed = (float) OPACITY_INTERVAL.clip(opacitySpeed);
            }

            /**
             * Updates the y-coordinate and the opacity of the ball.
             *
             * @param elapsed the time passed since the previous frame
             */
            private void update(long elapsed) {
                y += speed * elapsed / NANOS_TO_SECOND;
                final float add = opacitySpeed * elapsed / NANOS_TO_SECOND;
                if (opacityGrow) {
                    opacity += add;
                } else {
                    opacity -= add;
                }
                opacity = (float) OPACITY_INTERVAL.clip(opacity);
                if (opacity == MAX_OPACITY || opacity == MIN_OPACITY) {
                    opacityGrow = !opacityGrow;
                }
            }

            // here, we only need equals, so we allow
            // ourselves to not implement hashCode
            @Override
            public boolean equals(Object o) {
                if (!(o instanceof Ball)) {
                    return false;
                }

                final Ball ball = (Ball) o;
                return ball.x == x && ball.y == y && ball.speed == speed;
            }
        }

        private static final int MAX_BALL_COUNT = 30;

        private final Random random = ThreadLocalRandom.current();
        private final Canvas canvas = new Canvas();
        // contains the balls that are currently displayed
        private final List<Ball> balls = new ArrayList<>(MAX_BALL_COUNT);
        // the previous handle(long) time
        private long previous = -1L;

        private BackgroundAnimation() {
            // here, we had to use this initial value,
            // since the canvas' width and height are zero,
            // at this state (it is later changed when switched
            // with the controller)
            generateRandomBalls(Main.INITIAL_WIDTH);
            canvas.setCache(true);
            canvas.setCacheHint(CacheHint.SPEED);
        }

        /**
         * Generates {@code MAX_BALL_COUNT} random balls.
         *
         * @param maxX the maximal x-coordinate
         */
        private void generateRandomBalls(float maxX) {
            for (int i = 0; i < MAX_BALL_COUNT; i++) {
                balls.add(generateRandomBall(maxX));
            }
        }

        /**
         * @param maxX the maximal x coordinate for a ball.
         * @return a randomly generated ball.
         */
        private Ball generateRandomBall(float maxX) {
            // the following values contain a lot of "magic numbers", but there are
            // here for the purpose of a random display; hence, the values are just tweaks
            // we found good looking
            return new Ball(random.nextFloat() * maxX, random.nextFloat() * 300f,
                    65f * random.nextFloat() + 10f, 6f * random.nextFloat() * 10f + 50f,
                    random.nextFloat());
        }

        @Override
        public void handle(long now) {
            final GraphicsContext gfx = canvas.getGraphicsContext2D();
            gfx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gfx.setFill(Color.BLACK);
            gfx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            final List<Ball> toRemove = new ArrayList<>(5);
            if (previous != -1L) {
                for (Ball ball : balls) {
                    ball.update(now - previous);
                    if (ball.y > canvas.getHeight()) {
                        toRemove.add(ball);
                    } else {
                        gfx.setFill(Color.color(1f, 1f, 1f, ball.opacity));
                        gfx.fillOval(ball.x, ball.y, ball.size, ball.size);
                    }
                }
            }
            if (!toRemove.isEmpty()) {
                balls.removeAll(toRemove);
                for (int i = 0; i < toRemove.size(); i++) {
                    balls.add(generateRandomBall((float) canvas.getWidth()));
                }
            }
            previous = now;
        }
    }

    private final BackgroundAnimation animation;
    private final StackPane root = new StackPane();

    /**
     * Initializes the main screen, the first screen that the user sees.
     *
     * @param controller the screen controller of the program
     */
    public EntranceScreen(ScreenController controller) {
        final BorderPane pane = new BorderPane();
        pane.setStyle("-fx-background-color: transparent;");

        // top pane: title
        final Text text = new Text("RIGEL");
        text.getStyleClass().add("main-title");
        // little blink animation
        final FadeTransition transition = new FadeTransition(Duration.seconds(2d), text);
        transition.setFromValue(1.0d);
        transition.setToValue(0.1d);
        transition.setCycleCount(Timeline.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();
        final GridPane top = grid(Pos.TOP_CENTER, text);
        top.getStyleClass().add("title-box");
        pane.setTop(top);

        // middle pane: buttons
        final Button start = button("COMMENCER");
        start.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                controller.changeScreen(ScreenNames.STAR_VIEW_SCREEN);
                event.consume();
            }
        });
        final VBox buttons = new VBox(start);
        buttons.getStyleClass().add("main-buttons-group");
        final GridPane center = grid(Pos.CENTER, buttons);
        pane.setCenter(center);

        pane.setBackground(Background.EMPTY);

        animation = new BackgroundAnimation();
        if (ANIMATION_ENABLED) {
            final Pane backgroundPane = new Pane(animation.canvas);
            animation.canvas.widthProperty().bind(backgroundPane.widthProperty());
            animation.canvas.heightProperty().bind(backgroundPane.heightProperty());
            root.getChildren().add(backgroundPane);
        } else {
            pane.setStyle("-fx-background-color: black;");
        }
        root.getChildren().add(pane);
    }

    /**
     * @param text the text of the button
     * @return a new button with the correct style.
     */
    private Button button(String text) {
        final Button ret = new Button(text);
        ret.getStyleClass().add("main-button");
        return ret;
    }

    /**
     * @param pos   the alignement to use for the pane
     * @param nodes the nodes to add to the pane
     * @return a GridPane aligned on {@code pos} with children {@code nodes}.
     */
    private GridPane grid(Pos pos, Node... nodes) {
        GridPane p = new GridPane();
        p.setAlignment(pos);
        p.getChildren().addAll(nodes);
        return p;
    }

    @Override
    public String getName() {
        return ScreenNames.MAIN_SCREEN;
    }

    @Override
    public Pane getPane() {
        return root;
    }

    @Override
    public void onEnter() {
        if (ANIMATION_ENABLED) {
            animation.start();
        }
    }

    @Override
    public void onLeave() {
        if (ANIMATION_ENABLED) {
            animation.stop();
        }
    }

}
