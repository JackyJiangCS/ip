import java.time.LocalDateTime;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {

    protected final LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description work that must be completed
     * @param by date and time by which the task is due
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toDataString() {
        return super.toDataString("D") + " | " + DateTimeFormats.formatForStorage(by);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + DateTimeFormats.formatForDisplay(by) + ")";
    }
}
