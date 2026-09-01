import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Starts the JassaBot chatbot application.
 */
public class JassaBot {
    private static final String divider = "____________________________________________________________";
    private static final Storage storage = new Storage(Path.of("data", "jassabot.txt"));

    /**
     * Runs the chatbot, storing entered tasks, listing them on request, and exiting when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Storage.LoadResult loadResult = storage.loadTasks();
        ArrayList<Task> tasks = loadResult.getTasks();

        String banner = "   _                         ____        _\n"
                + "  | | __ _ ___ ___  __ _    | __ )  ___ | |_\n"
                + "  | |/ _` / __/ __|/ _` |   |  _ \\ / _ \\| __|\n"
                + "  | | (_| \\__ \\__ \\ (_| |   | |_) | (_) | |_\n"
                + " _|_|\\__,_|___/___/\\__,_|   |____/ \\___/ \\__|";

        System.out.println(divider);
        System.out.println(banner);
        System.out.println("Hello! I'm JassaBot.");
        System.out.println("What can I do for you?");
        System.out.println(divider);
        for (String warning : loadResult.getWarnings()) {
            System.out.println("WARNING: " + warning);
        }
        if (!loadResult.getWarnings().isEmpty()) {
            System.out.println(divider);
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            System.out.println(divider);

            try {
                if (command.isEmpty()) {
                    throw new JassaBotException("Please enter a command.");
                }
                CommandType commandType = Parser.parseCommandType(command);
                switch (commandType) {
                case BYE -> {
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(divider);
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
                System.out.println("OOPS!!! " + e.getMessage());
                System.out.println(divider);
            }
        }

        System.out.println(divider);
        System.out.println("Input closed. Goodbye!");
        System.out.println(divider);
    }

    /**
     * Displays every task in its current list position.
     *
     * @param tasks tasks currently stored by the application
     */
    private static void showTaskList(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(divider);
    }

    /**
     * Marks the task selected by a user command as completed.
     *
     * @param command complete mark command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the task number is invalid or the changed list cannot be saved
     */
    private static void markTask(String command, ArrayList<Task> tasks) throws JassaBotException {
        String number = command.substring("mark".length()).trim();
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            System.out.println("Please enter a valid task number.");
            System.out.println(divider);
            return;
        }

        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            storage.saveTasks(tasks);
        } catch (StorageException e) {
            if (!wasDone) {
                task.markAsUndone();
            }
            throw createSaveException();
        }
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
        System.out.println(divider);
    }

    /**
     * Marks the task selected by a user command as incomplete.
     *
     * @param command complete unmark command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the task number is invalid or the changed list cannot be saved
     */
    private static void unmarkTask(String command, ArrayList<Task> tasks) throws JassaBotException {
        String number = command.substring("unmark".length()).trim();
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            System.out.println("Please enter a valid task number.");
            System.out.println(divider);
            return;
        }

        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsUndone();
        try {
            storage.saveTasks(tasks);
        } catch (StorageException e) {
            if (wasDone) {
                task.markAsDone();
            }
            throw createSaveException();
        }
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
        System.out.println(divider);
    }

    /**
     * Deletes the task selected by a user command.
     *
     * @param command complete delete command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the task number is invalid or the changed list cannot be saved
     */
    private static void deleteTask(String command, ArrayList<Task> tasks) throws JassaBotException {
        String number = command.substring("delete".length()).trim();
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            System.out.println("Please enter a valid task number.");
            System.out.println(divider);
            return;
        }

        Task removedTask = tasks.remove(index);
        try {
            storage.saveTasks(tasks);
        } catch (StorageException e) {
            tasks.add(index, removedTask);
            throw createSaveException();
        }
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removedTask);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(divider);
    }

    /**
     * Validates a deadline command and adds the resulting task.
     *
     * @param command complete deadline command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the description or due-time marker is missing
     */
    private static void addDeadline(String command, ArrayList<Task> tasks)
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
        String byTime = command.substring(byIndex + " /by".length()).trim();
        if (byTime.isEmpty()) {
            throw new JassaBotException("A deadline needs '/by' followed by its due time.");
        }

        Deadline newDeadline = new Deadline(description, byTime);
        tasks.add(newDeadline);
        try {
            storage.saveTasks(tasks);
        } catch (StorageException e) {
            tasks.remove(tasks.size() - 1);
            throw createSaveException();
        }
        showAddedTask(newDeadline, tasks.size(), divider);
    }

    /**
     * Validates an event command and adds the resulting task.
     *
     * @param command complete event command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the description or time markers are missing
     */
    private static void addEvent(String command, ArrayList<Task> tasks)
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
        String from = command.substring(fromIndex + " /from".length(), toIndex).trim();
        String to = command.substring(toIndex + " /to".length()).trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw new JassaBotException(
                    "An event needs non-empty times after both '/from' and '/to'.");
        }

        Event newEvent = new Event(description, from, to);
        tasks.add(newEvent);
        try {
            storage.saveTasks(tasks);
        } catch (StorageException e) {
            tasks.remove(tasks.size() - 1);
            throw createSaveException();
        }
        showAddedTask(newEvent, tasks.size(), divider);
    }

    /**
     * Validates a todo command and adds the resulting task.
     *
     * @param command complete todo command entered by the user
     * @param tasks tasks currently stored by the application
     * @throws JassaBotException if the description is missing
     */
    private static void addTodo(String command, ArrayList<Task> tasks)
            throws JassaBotException {
        String description = command.substring(4).trim();
        if (description.isEmpty()) {
            throw new JassaBotException("The description of a todo cannot be empty.");
        }

        Todo newTodo = new Todo(description);
        tasks.add(newTodo);
        try {
            storage.saveTasks(tasks);
        } catch (StorageException e) {
            tasks.remove(tasks.size() - 1);
            throw createSaveException();
        }
        showAddedTask(newTodo, tasks.size(), divider);
    }

    /**
     * Displays the shared confirmation printed after adding any task type.
     *
     * @param task task that was added
     * @param numberOfTasks updated number of stored tasks
     * @param divider line printed after the response
     */
    private static void showAddedTask(Task task, int numberOfTasks, String divider) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + numberOfTasks + " tasks in the list.");
        System.out.println(divider);
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
