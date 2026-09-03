package jassabot.task;

import java.time.LocalDateTime;

import jassabot.util.DateTimeFormats;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {

    private final LocalDateTime dueDateTime;

    /**
     * Creates a deadline task.
     *
     * @param description Work that must be completed.
     * @param dueDateTime Date and time by which the task is due.
     */
    public Deadline(String description, LocalDateTime dueDateTime) {
        super(description);
        this.dueDateTime = dueDateTime;
    }

    @Override
    public String toDataString() {
        return super.toDataString("D") + " | "
                + DateTimeFormats.formatForStorage(dueDateTime);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + DateTimeFormats.formatForDisplay(dueDateTime) + ")";
    }
}
