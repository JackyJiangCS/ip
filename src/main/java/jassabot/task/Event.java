package jassabot.task;

import java.time.LocalDateTime;

import jassabot.util.DateTimeFormats;

/**
 * Represents an activity occurring between two dates and times.
 */
public class Event extends Task {

    protected final LocalDateTime from;
    protected final LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description activity that will take place
     * @param from date and time at which the event starts
     * @param to date and time at which the event ends
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Converts this event into one line suitable for saving to the storage file.
     *
     * @return Storage-file representation of this event.
     */
    @Override
    public String toDataString() {
        return super.toDataString("E") + " | " + DateTimeFormats.formatForStorage(from)
                + " | " + DateTimeFormats.formatForStorage(to);
    }

    /**
     * Returns the type, completion status, description, and time range used to display this event.
     *
     * @return Display representation of this event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeFormats.formatForDisplay(from) + " to: "
                + DateTimeFormats.formatForDisplay(to) + ")";
    }
}
