import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns the application's ordered collection of tasks and its list operations.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param tasks initial tasks in display order
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task at the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Restores a task at a specific position, such as after a failed save.
     *
     * @param index zero-based position at which to insert the task
     * @param task task to insert
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Returns the task at a specific position.
     *
     * @param index zero-based task position
     * @return task at the requested position
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a specific position.
     *
     * @param index zero-based task position
     * @return removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks the task at a specific position as completed.
     *
     * @param index zero-based task position
     */
    public void mark(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at a specific position as incomplete.
     *
     * @param index zero-based task position
     */
    public void unmark(int index) {
        tasks.get(index).markAsUndone();
    }

    /**
     * Returns the current number of tasks.
     *
     * @return number of tasks in the list
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Provides a read-only view for displaying or saving the current tasks.
     *
     * @return unmodifiable task view in display order
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
