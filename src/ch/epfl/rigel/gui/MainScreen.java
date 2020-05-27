package ch.epfl.rigel.gui;

import ch.epfl.rigel.util.Fonts;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Represents the main menu.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
final class MainScreen implements Screen {

    private static final class BackgroundAnimation extends AnimationTimer {
        final Canvas canvas = new Canvas();

        {
            canvas.setCache(true);
            canvas.setCacheHint(CacheHint.SPEED);
        }

        @Override
        public void handle(long now) {
            final GraphicsContext gfx = canvas.getGraphicsContext2D();
            gfx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gfx.setFill(Color.BLACK);
            gfx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.getGraphicsContext2D().setFill(Color.RED);
            canvas.getGraphicsContext2D().fillOval(0, 0, 200, 200);
        }
    }

    private static final Font MAIN_FONT = Fonts.loadUnsafe("/PatrickHandSC-Regular.ttf");

    private final StackPane root = new StackPane();

    MainScreen() {
        final BorderPane pane = new BorderPane();
        pane.setStyle("-fx-background-color: transparent;");

        // top pane: title
        final Text text = new Text("RIGEL");
        text.setFont(MAIN_FONT);
        text.setFill(Color.WHITESMOKE);
        text.getStyleClass().add("main-title");
        // little fade animation
        final FadeTransition transition = new FadeTransition(Duration.seconds(2d), text);
        transition.setFromValue(1.0d);
        transition.setToValue(0.1);
        transition.setCycleCount(Timeline.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();
        final GridPane top = grid(Pos.TOP_CENTER, text);
        top.getStyleClass().add("title-box");
        pane.setTop(top);

        // middle pane: buttons
        final Button start = button("COMMENCER");
        final Button about = button("A PROPOS");
        // TODO: change font of buttons!!
        final VBox buttons = new VBox(start, about);
        buttons.setSpacing(25d);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(-150d, 0, 0, 0));
        final GridPane center = grid(Pos.CENTER, buttons);
        pane.setCenter(center);
        pane.setBackground(Background.EMPTY);

        final BorderPane p = new BorderPane();
        BackgroundAnimation animation = new BackgroundAnimation();
        animation.canvas.widthProperty().bind(p.widthProperty());
        animation.canvas.heightProperty().bind(p.heightProperty());
        p.setCenter(animation.canvas);
        root.getChildren().addAll(p, pane);
        animation.start();
    }

    /**
     * @param text the text of the button
     * @return a new button with the default font.
     */
    private Button button(String text) {
        final Button ret = new Button(text);
        ret.setFont(MAIN_FONT);
        return ret;
    }

    /**
     * @param pos the alignement to use for the pane
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
    public Pane getPane() {
        return root;
    }

}
