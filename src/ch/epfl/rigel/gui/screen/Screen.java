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

    private final String name;

    /**
     * @param name the name of the screen
     */
    public Screen(String name) {
        this.name = name;
    }

    /**
     * @return the name of the screen.
     */
    public final String getName() {
        return name;
    }

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
