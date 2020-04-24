package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 22/04/2020
 */
public final class DateTimeBean {
    private ObjectProperty<LocalDate> dateProperty;
    private ObjectProperty<LocalTime> timeProperty;
    private ObjectProperty<ZoneId> zoneProperty;


    public DateTimeBean() {
        dateProperty = new SimpleObjectProperty<>(null);
        timeProperty = new SimpleObjectProperty<>(null);
        zoneProperty = new SimpleObjectProperty<>(null);
    }

    /**
     * @return the property of {@code date}
     */
    ObjectProperty<LocalDate> dateProperty() {
        return dateProperty;
    }

    /**
     * @return the date
     */
    LocalDate getDate() {
        return dateProperty.get();
    }

    /**
     * @param date the other date
     */
    void setDate(LocalDate date) {
        dateProperty.setValue(date);
    }

    /**
     * @return the property of the {@code time}
     */
    ObjectProperty<LocalTime> timeProperty() {
        return timeProperty;
    }

    /**
     * @return the time
     */
    LocalTime getTime() {
        return timeProperty.get();
    }

    /**
     * @param time the other time
     */
    void setTime(LocalTime time) {
        timeProperty.setValue(time);
    }

    /**
     * @return the property of the {@code zone}
     */
    ObjectProperty<ZoneId> zoneProperty() {
        return zoneProperty;
    }

    /**
     * @return the zone
     */
    ZoneId getZone() {
        return zoneProperty.get();
    }

    /**
     * @param zone the other zone
     */
    void setZone(ZoneId zone) {
        zoneProperty.setValue(zone);
    }

    /**
     * @return a new {@code ZonedDateTime} with parameters from {@code this}
     */
    ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }

    /**
     * Changes the values of the instance's attributes to those of the parameter.
     *
     * @param zonedDateTime the other {@code ZonedDateTime}.
     */
    void setZonedDateTime(ZonedDateTime zonedDateTime) {
        setDate(zonedDateTime.toLocalDate());
        setTime(zonedDateTime.toLocalTime());
        setZone(zonedDateTime.getZone());
    }
}
