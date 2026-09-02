import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Starts the JassaBot chatbot application.
 */
public class JassaBot {
    private final Storage storage;
    private final Ui ui;

    /**
     * Creates a chatbot that stores tasks at the given relative file path.
     *
     * @param filePath relative path of the task data file
     */
    public JassaBot(Path filePath) {
        storage = new Storage(filePath);
        ui = new Ui();
    }

    /**
     * Runs the chatbot, storing entered tasks, listing them on request, and exiting when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new JassaBot(Path.of("data", "jassabot.txt")).run();
    }

    /**
     * Runs the command loop until the user exits or closes the input stream.
     */
    public void run() {
        Storage.LoadResult loadResult = storage.loadTasks();
        TaskList tasks = new TaskList(loadResult.getTasks());

        ui.showWelcome();
        ui.showLoadingWarnings(loadResult.getWarnings());

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showResponseStart();

            try {
                if (command.isEmpty()) {
                    throw new JassaBotException("Please enter a command.");
                }
                CommandType commandType = Parser.parseCommandType(command);
                switch (commandType) {
                case BYE -> {
                    ui.showGoodbye();
                    return;
                }
                case LIST -> showTaskList(tasks);
                case MARK -> markTask(command, tasks);
                case UNMARK -> unmarkTask(command, tasks);
                case DELETE -> deleteTask(command, tasks);
                case DEADLINE -> addDeadline(command, tasks);
                case EVENT -> addEvent(command, tasks);
                case TODO -> addTodo(command, tasks);
                case UNKNOWN -> throw new JassaBotException(
                        "I don't recognise that command. Try todo, deadline, event, list, mark, "
                                + "unmark, delete, or bye.");
                }
            } catch (JassaBotException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.showInputClosed();
    }

    /**
     * Displays every task in its current list position.
     *
     * @param tasks tasks currently stored by the application
     */
    private void showTaskList(TaskList tasks) {
        ui.showTaskList(tasks.asList());
    }

    /**
     * Marks the task selected by a user command as completed.
     *
     * @param command complete mark command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the task number is invalid or the changed list cannot be saved
     */
    private void markTask(String command, TaskList tasks) throws JassaBotException {
        String number = command.substring("mark".length()).trim();
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            ui.showInvalidTaskNumber();
            return;
        }

        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        tasks.mark(index);
        try {
            storage.saveTasks(tasks.asList());
        } catch (StorageException e) {
            if (!wasDone) {
                tasks.unmark(index);
            }
            throw createSaveException();
        }
        ui.showTaskMarked(task);
    }

    /**
     * Marks the task selected by a user command as incomplete.
     *
     * @param command complete unmark command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the task number is invalid or the changed list cannot be saved
     */
    private void unmarkTask(String command, TaskList tasks) throws JassaBotException {
        String number = command.substring("unmark".length()).trim();
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            ui.showInvalidTaskNumber();
            return;
        }

        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        tasks.unmark(index);
        try {
            storage.saveTasks(tasks.asList());
        } catch (StorageException e) {
            if (wasDone) {
                tasks.mark(index);
            }
            throw createSaveException();
        }
        ui.showTaskUnmarked(task);
    }

    /**
     * Deletes the task selected by a user command.
     *
     * @param command complete delete command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the task number is invalid or the changed list cannot be saved
     */
    private void deleteTask(String command, TaskList tasks) throws JassaBotException {
        String number = command.substring("delete".length()).trim();
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            ui.showInvalidTaskNumber();
            return;
        }

        Task removedTask = tasks.remove(index);
        try {
            storage.saveTasks(tasks.asList());
        } catch (StorageException e) {
            tasks.add(index, removedTask);
            throw createSaveException();
        }
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Validates a deadline command and adds the resulting task.
     *
     * @param command complete deadline command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the description, due-time marker, or date is invalid
     */
    private void addDeadline(String command, TaskList tasks)
            throws JassaBotException {
        int byIndex = findMarker(command, " /by");
        String description = byIndex == -1 ? command.substring(8).trim()
                : command.substring(8, byIndex).trim();

        if (description.isEmpty()) {
            throw new JassaBotException("The description of a deadline cannot be empty.");
        }
        if (byIndex == -1) {
            throw new JassaBotException("A deadline needs '/by' followed by its due time.");
        }
        String byDateTimeText = command.substring(byIndex + " /by".length()).trim();
        if (byDateTimeText.isEmpty()) {
            throw new JassaBotException("A deadline needs '/by' followed by its due time.");
        }

        LocalDateTime by = Parser.parseDateTime(byDateTimeText);
        Deadline newDeadline = new Deadline(description, by);
        tasks.add(newDeadline);
        try {
            storage.saveTasks(tasks.asList());
        } catch (StorageException e) {
            tasks.remove(tasks.size() - 1);
            throw createSaveException();
        }
        ui.showTaskAdded(newDeadline, tasks.size());
    }

    /**
     * Validates an event command and adds the resulting task.
     *
     * @param command complete event command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the description, time markers, or dates are invalid
     */
    private void addEvent(String command, TaskList tasks)
            throws JassaBotException {
        int fromIndex = findMarker(command, " /from");
        int toIndex = findMarker(command, " /to");
        String description = fromIndex == -1 ? command.substring(5).trim()
                : command.substring(5, fromIndex).trim();

        if (description.isEmpty()) {
            throw new JassaBotException("The description of an event cannot be empty.");
        }
        if (fromIndex == -1 || toIndex == -1 || toIndex <= fromIndex) {
            throw new JassaBotException("An event needs both '/from' and '/to' time markers.");
        }
        String fromDateTimeText = command.substring(
                fromIndex + " /from".length(), toIndex).trim();
        String toDateTimeText = command.substring(toIndex + " /to".length()).trim();
        if (fromDateTimeText.isEmpty() || toDateTimeText.isEmpty()) {
            throw new JassaBotException(
                    "An event needs non-empty times after both '/from' and '/to'.");
        }

        LocalDateTime from = Parser.parseDateTime(fromDateTimeText);
        LocalDateTime to = Parser.parseDateTime(toDateTimeText);
        Event newEvent = new Event(description, from, to);
        tasks.add(newEvent);
        try {
            storage.saveTasks(tasks.asList());
        } catch (StorageException e) {
            tasks.remove(tasks.size() - 1);
            throw createSaveException();
        }
        ui.showTaskAdded(newEvent, tasks.size());
    }

    /**
     * Validates a todo command and adds the resulting task.
     *
     * @param command complete todo command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the description is missing
     */
    private void addTodo(String command, TaskList tasks)
            throws JassaBotException {
        String description = command.substring(4).trim();
        if (description.isEmpty()) {
            throw new JassaBotException("The description of a todo cannot be empty.");
        }

        Todo newTodo = new Todo(description);
        tasks.add(newTodo);
        try {
            storage.saveTasks(tasks.asList());
        } catch (StorageException e) {
            tasks.remove(tasks.size() - 1);
            throw createSaveException();
        }
        ui.showTaskAdded(newTodo, tasks.size());
    }

    /**
     * Finds a command marker only when it ends at whitespace or at the end of the command.
     *
     * @param command complete user command
     * @param marker marker including its required leading space
     * @return index of the marker, or {@code -1} if it is absent
     */
    private static int findMarker(String command, String marker) {
        int searchFrom = 0;
        while (searchFrom < command.length()) {
            int markerIndex = command.indexOf(marker, searchFrom);
            if (markerIndex == -1) {
                return -1;
            }
            int afterMarker = markerIndex + marker.length();
            if (afterMarker == command.length()
                    || Character.isWhitespace(command.charAt(afterMarker))) {
                return markerIndex;
            }
            searchFrom = afterMarker;
        }
        return -1;
    }

    /**
     * Creates the shared user-facing error used after a failed transactional save.
     *
     * @return save error that explains the rollback
     */
    private static JassaBotException createSaveException() {
        return new JassaBotException(
                "I couldn't save your tasks, so no changes were made.");
    }

    /**
     * Converts a user-entered, one-based task number into a valid array index.
     *
     * @param number the task number entered by the user
     * @param numberOfTasks the number of tasks currently stored
     * @return the zero-based task index, or {@code -1} if the number is invalid
     */
    private static int getTaskIndex(String number, int numberOfTasks) {
        try {
            int taskNumber = Integer.parseInt(number.trim());
            if (taskNumber < 1 || taskNumber > numberOfTasks) {
                return -1;
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
