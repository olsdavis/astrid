package ch.epfl.rigel.gui;

import javafx.scene.layout.Pane;

/**
 * Represents a screen that can be accessed by the user.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public interface Screen {

    /**
     * @return the pane of the current screen.
     */
    Pane getPane();

}
