package jassabot.task;

public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon that represents this task's completion state.
     *
     * @return {@code X} when the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void markAsDone() {
        isDone = true;
    }

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

    @Override
    public String toString(){
        return "[" + getStatusIcon() + "] " + description;
    }
}
