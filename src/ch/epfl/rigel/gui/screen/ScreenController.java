package ch.epfl.rigel.gui.screen;

import ch.epfl.rigel.Preconditions;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Controls the current screen used by the user.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/05/2020
 */
public final class ScreenController {

    // the name of the current screen
    private final SimpleStringProperty screenName = new SimpleStringProperty();
    private final Map<String, Screen> screens = new HashMap<>();
    private final ObservableObjectValue<Screen> currentScreen = Bindings.createObjectBinding(
            () -> screens.get(screenName.get()), screenName
    );

    {
        currentScreen.addListener(((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.onLeave();
            }
            newValue.onEnter();
        }));
    }

    /**
     * Adds the provided screen to the collection of screens; overrides
     * the previous screen of the same name, if there is one.
     *
     * @param screen the screen to add
     * @throws NullPointerException     if {@code screen} is null
     * @throws IllegalArgumentException if {@code screen.getName()} returns {@code null}
     *                                  or an empty String
     */
    public void addScreen(Screen screen) {
        if (Objects.requireNonNull(screen).getName() == null || screen.getName().equals("")) {
            throw new IllegalArgumentException("screenName cannot be null or empty");
        }

        screens.put(screen.getName(), screen);
    }

    /**
     * Changes the screen shown to the user to the provided one.
     *
     * @param newScreen the new screen to show
     * @throws IllegalArgumentException if the provided screen {@code newScreen}
     *                                  does not exist in the collection of screens
     */
    public void changeScreen(String newScreen) {
        Preconditions.checkArgument(screens.containsKey(newScreen));

        screenName.set(newScreen);
    }

    /**
     * @return the current screen that the user should see.
     */
    public ObservableObjectValue<Screen> currentScreenProperty() {
        return currentScreen;
    }

}
