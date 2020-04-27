package ch.epfl.rigel.gui;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 27/04/2020
 */
public class TimeAcceleratorTest {

    @Test
    void framapad() {
        ZonedDateTime initialTime = ZonedDateTime.parse("2020-04-17T21:00:00+00:00");
        ZonedDateTime laterTime = TimeAccelerator.continuous(300).adjust(initialTime, (long) (2.34 * 1e9));
        assertEquals(ZonedDateTime.parse("2020-04-17T21:11:42+00:00"), laterTime);
    }

}
