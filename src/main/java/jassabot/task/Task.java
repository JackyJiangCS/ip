package jassabot.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Work represented by this task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon used to represent this task's completion status.
     *
     * @return {@code X} if this task is completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns this task's description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Converts this task into the common portion of its storage-file representation.
     *
     * @param taskType single-letter code identifying the task type
     * @return task type, completion status, and description separated by {@code |}
     */
    protected String toDataString(String taskType) {
        return taskType + " | " + (isDone ? "1" : "0") + " | " + encodeDataField(description);
    }

    /**
     * Escapes delimiter and backslash characters before a text field is stored.
     *
     * @param value text field to encode
     * @return encoded field that cannot be mistaken for a storage delimiter
     */
    protected static String encodeDataField(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("|", "\\|");
    }

    /**
     * Converts this task into one line suitable for saving to the storage file.
     *
     * @return storage-file representation of this task
     */
    public String toDataString() {
        return toDataString("T");
    }

    /**
     * Returns the completion status and description used when displaying this task.
     *
     * @return Display representation of this task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
