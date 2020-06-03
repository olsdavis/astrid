package ch.epfl.rigel.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Holds the data about what the user wants to see displayed on
 * his screen or not.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 03/06/2020
 */
public class DisplayParametersBean {

    private final SimpleBooleanProperty displayAsterisms = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty displayStars = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty displayMoon = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty displaySun = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty displayPlanets = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty displayHorizon = new SimpleBooleanProperty(true);

    /**
     * Updates the value of whether the asterisms should be displayed or not.
     *
     * @param value the new value
     */
    public void setDisplayAsterisms(boolean value) {
        displayAsterisms.set(value);
    }

    /**
     * Updates the value of whether the stars should be displayed or not.
     *
     * @param value the new value
     */
    public void setDisplayStars(boolean value) {
        displayStars.set(value);
    }

    /**
     * Updates the value of whether the horizon line should be displayed or not.
     *
     * @param value the new value
     */
    public void setDisplayHorizon(boolean value) {
        displayHorizon.set(value);
    }

    /**
     * Updates the value of whether the moon should be displayed or not.
     *
     * @param value the new value
     */
    public void setDisplayMoon(boolean value) {
        this.displayMoon.set(value);
    }

    /**
     * Updates the value of whether the sun should be displayed or not.
     *
     * @param value the new value
     */
    public void setDisplaySun(boolean value) {
        this.displaySun.set(value);
    }

    /**
     * Updates the value of whether the planets should be displayed or not.
     *
     * @param value the new value
     */
    public void setDisplayPlanets(boolean value) {
        this.displayPlanets.set(value);
    }

    /**
     * @return {@code true} if and onlly if the asterisms should be displayed.
     */
    public boolean isDisplayAsterisms() {
        return displayAsterisms.get();
    }

    /**
     * @return {@code true} if and onlly if the stars should be displayed.
     */
    public boolean isDisplayStars() {
        return displayStars.get();
    }

    /**
     * @return {@code true} if and onlly if the horizon line should be displayed.
     */
    public boolean isDisplayHorizon() {
        return displayHorizon.get();
    }

    /**
     * @return {@code true} if and onlly if the sun should be displayed.
     */
    public boolean isDisplaySun() {
        return displaySun.get();
    }

    /**
     * @return {@code true} if and onlly if the moon should be displayed.
     */
    public boolean isDisplayMoon() {
        return displayMoon.get();
    }

    /**
     * @return {@code true} if and onlly if the planets should be displayed.
     */
    public boolean isDisplayPlanets() {
        return displayPlanets.get();
    }

    /**
     * @return the property holding the value that tells whether the asterisms should
     * be displayed or not.
     */
    public BooleanProperty displayAsterismsProperty() {
        return displayAsterisms;
    }

    /**
     * @return the property holding the value that tells whether the stars should
     * be displayed or not.
     */
    public BooleanProperty displayStarsProperty() {
        return displayStars;
    }

    /**
     * @return the property holding the value that tells whether the horizon line should
     * be displayed or not.
     */
    public BooleanProperty displayHorizonProperty() {
        return displayHorizon;
    }

    /**
     * @return the property holding the value that tells whether the moon should
     * be displayed or not.
     */
    public BooleanProperty displayMoonProperty() {
        return displayMoon;
    }

    /**
     * @return the property holding the value that tells whether the sun should
     * be displayed or not.
     */
    public BooleanProperty displaySunProperty() {
        return displaySun;
    }

    /**
     * @return the property holding the value that tells whether the planets should
     * be displayed or not.
     */
    public BooleanProperty displayPlanetsProperty() {
        return displayPlanets;
    }

}
