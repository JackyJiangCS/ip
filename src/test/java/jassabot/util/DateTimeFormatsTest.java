package jassabot.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests user-facing and persistent date-time conversions.
 */
public class DateTimeFormatsTest {

    @Test
    public void formatForDisplay_midnight_omitsTime() {
        LocalDateTime midnight = LocalDateTime.of(2019, 12, 2, 0, 0);

        assertEquals("Dec 2 2019", DateTimeFormats.formatForDisplay(midnight));
    }

    @Test
    public void formatForDisplay_nonMidnight_usesFriendlyTwelveHourTime() {
        assertAll(
                () -> assertEquals("Dec 2 2019, 12:01 AM",
                        DateTimeFormats.formatForDisplay(
                                LocalDateTime.of(2019, 12, 2, 0, 1))),
                () -> assertEquals("Dec 2 2019, 12:00 PM",
                        DateTimeFormats.formatForDisplay(
                                LocalDateTime.of(2019, 12, 2, 12, 0))),
                () -> assertEquals("Dec 2 2019, 11:59 PM",
                        DateTimeFormats.formatForDisplay(
                                LocalDateTime.of(2019, 12, 2, 23, 59)))
        );
    }

    @Test
    public void formatAndParseStorageDateTime_validValue_roundTripsExactly() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 2, 29, 18, 5);

        String storedValue = DateTimeFormats.formatForStorage(dateTime);

        assertAll(
                () -> assertEquals("2024-02-29T18:05", storedValue),
                () -> assertEquals(dateTime,
                        DateTimeFormats.parseStorageDateTime(storedValue))
        );
    }

    @Test
    public void parseStorageDateTime_malformedValue_throwsDateTimeParseException() {
        assertAll(
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeFormats.parseStorageDateTime("2024-02-29 18:05")),
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeFormats.parseStorageDateTime("2023-02-29T18:05")),
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeFormats.parseStorageDateTime("2024-02-29T24:00"))
        );
    }
}
