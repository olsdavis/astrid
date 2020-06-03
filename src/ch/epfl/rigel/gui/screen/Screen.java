package ch.epfl.rigel.gui.screen;

import javafx.scene.layout.Pane;

/**
 * Represents a screen that can be accessed by the user.
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
