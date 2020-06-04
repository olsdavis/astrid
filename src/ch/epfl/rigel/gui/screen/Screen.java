package ch.epfl.rigel.gui.screen;

import javafx.scene.layout.Pane;

/**
 * Represents a screen that can be accessed by the user.
 * <p>
 * Given that all that this abstract class holds are methods, this could
 * have been an interface; yet, by design, we preferred to restrict the
 * subclasses to only inherit from this class. (Because a screen should
 * only represent a screen, and nothing else.)
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public abstract class Screen {

    /**
     * @return the name of the screen.
     */
    public abstract String getName();

    /**
     * @return the pane of the current screen.
     */
    public abstract Pane getPane();

    /**
     * This method is called as an event whenever this screen
     * becomes the displayed screen.
     */
    public void onEnter() {
    }

    /**
     * This method is called as an event whenever this screen
     * is no more the displayed screen
     */
    public void onLeave() {
    }

}
