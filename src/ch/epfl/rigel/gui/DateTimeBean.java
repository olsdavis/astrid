package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Wraps {@link ZonedDateTime} into three time fields held by JFX properties, and allows
 * its mutability.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 22/04/2020
 */
public final class DateTimeBean {

    private final ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<LocalTime> timeProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ZoneId> zoneProperty = new SimpleObjectProperty<>(null);

    /**
     * @return the property of {@code date}
     */
    public ObjectProperty<LocalDate> dateProperty() {
        return dateProperty;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return dateProperty.get();
    }

    /**
     * @param date the other date
     */
    public void setDate(LocalDate date) {
        dateProperty.setValue(date);
    }

    /**
     * @return the property of the {@code time}
     */
    public ObjectProperty<LocalTime> timeProperty() {
        return timeProperty;
    }

    /**
     * @return the time
     */
    public LocalTime getTime() {
        return timeProperty.get();
    }

    /**
     * @param time the other time
     */
    public void setTime(LocalTime time) {
        timeProperty.setValue(time);
    }

    /**
     * @return the property of the {@code zone}
     */
    public ObjectProperty<ZoneId> zoneProperty() {
        return zoneProperty;
    }

    /**
     * @return the zone
     */
    public ZoneId getZone() {
        return zoneProperty.get();
    }

    /**
     * @param zone the other zone
     */
    public void setZone(ZoneId zone) {
        zoneProperty.setValue(zone);
    }

    /**
     * @return a new {@code ZonedDateTime} with parameters from {@code this}
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }

    /**
     * Changes the values of the instance's attributes to those of the parameter.
     *
     * @param zonedDateTime the other {@code ZonedDateTime}.
     */
    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        setDate(zonedDateTime.toLocalDate());
        setTime(zonedDateTime.toLocalTime());
        setZone(zonedDateTime.getZone());
    }

}
