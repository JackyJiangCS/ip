package jassabot.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task with the given description.
     *
     * @param description Work represented by this todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Converts this todo into one line suitable for saving to the storage file.
     *
     * @return Storage-file representation of this todo.
     */
    @Override
    public String toDataString() {
        return super.toDataString("T");
    }

    /**
     * Returns the type, completion status, and description used when displaying this todo.
     *
     * @return Display representation of this todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
