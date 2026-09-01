import java.time.LocalDateTime;

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

    @Override
    public String toDataString() {
        return super.toDataString("E") + " | " + DateTimeFormats.formatForStorage(from)
                + " | " + DateTimeFormats.formatForStorage(to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + DateTimeFormats.formatForDisplay(from) + " to: "
                + DateTimeFormats.formatForDisplay(to) + ")";
    }
}
