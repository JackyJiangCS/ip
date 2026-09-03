package jassabot.task;

import java.time.LocalDateTime;

import jassabot.util.DateTimeFormats;

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

    /**
     * Converts this deadline into one line suitable for saving to the storage file.
     *
     * @return Storage-file representation of this deadline.
     */
    @Override
    public String toDataString() {
        return super.toDataString("D") + " | " + DateTimeFormats.formatForStorage(by);
    }

    /**
     * Returns the type, completion status, description, and due date used to display this deadline.
     *
     * @return Display representation of this deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + DateTimeFormats.formatForDisplay(by) + ")";
    }
}
