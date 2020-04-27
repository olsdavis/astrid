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
     * Updates the value held by the date property.
     *
     * @param date the new value of the date
     */
    public void setDate(LocalDate date) {
        dateProperty.setValue(date);
    }

    /**
     * @return the property holding the {@code time}.
     */
    public ObjectProperty<LocalTime> timeProperty() {
        return timeProperty;
    }

    /**
     * @return the value of the time.
     */
    public LocalTime getTime() {
        return timeProperty.get();
    }

    /**
     * Updates the value held by the time property.
     *
     * @param time the new value of the time
     */
    public void setTime(LocalTime time) {
        timeProperty.setValue(time);
    }

    /**
     * @return the property of the {@code zone}.
     */
    public ObjectProperty<ZoneId> zoneProperty() {
        return zoneProperty;
    }

    /**
     * @return the value of the zone.
     */
    public ZoneId getZone() {
        return zoneProperty.get();
    }

    /**
     * Updates the value held by the property of the zone.
     *
     * @param zone the new value of the zone
     */
    public void setZone(ZoneId zone) {
        zoneProperty.setValue(zone);
    }

    /**
     * @return a new {@code ZonedDateTime} built from the date, time and zone held by
     * this instance's properties.
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }

    /**
     * Changes the values of the instance's zone, date and time attributes to those
     * of the provided {@link ZonedDateTime}.
     *
     * @param zonedDateTime the new ZonedDateTime value
     */
    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        setDate(zonedDateTime.toLocalDate());
        setTime(zonedDateTime.toLocalTime());
        setZone(zonedDateTime.getZone());
    }

}
