import java.util.ArrayList;
import java.util.Scanner;

/**
 * Starts the JassaBot chatbot application.
 */
public class JassaBot {
    /**
     * Runs the chatbot, storing entered tasks, listing them on request, and exiting when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String divider = "____________________________________________________________";
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

        ArrayList<Task> tasks = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            System.out.println(divider);

            try {
                CommandType commandType = Parser.parseCommandType(command);
                switch (commandType) {
                case BYE -> {
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(divider);
                    return;
                }
                case LIST -> showTaskList(tasks, divider);
                case MARK -> markTask(command, tasks, divider);
                case UNMARK -> unmarkTask(command, tasks, divider);
                case DELETE -> deleteTask(command, tasks, divider);
                case DEADLINE -> addDeadline(command, tasks, divider);
                case EVENT -> addEvent(command, tasks, divider);
                case TODO -> addTodo(command, tasks, divider);
                case UNKNOWN -> throw new JassaBotException(
                        "I don't recognise that command. Try todo, deadline, event, list, mark, "
                                + "unmark, delete, or bye.");
                }
            } catch (JassaBotException e) {
                System.out.println("OOPS!!! " + e.getMessage());
                System.out.println(divider);
            }
        }
    }

    /**
     * Displays every task in its current list position.
     *
     * @param tasks tasks currently stored by the application
     * @param divider line printed after the response
     */
    private static void showTaskList(ArrayList<Task> tasks, String divider) {
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
     * @param divider line printed after the response
     */
    private static void markTask(String command, ArrayList<Task> tasks, String divider) {
        String number = command.substring(5);
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            System.out.println("Please enter a valid task number.");
            System.out.println(divider);
            return;
        }

        tasks.get(index).markAsDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + tasks.get(index));
        System.out.println(divider);
    }

    /**
     * Marks the task selected by a user command as incomplete.
     *
     * @param command complete unmark command entered by the user
     * @param tasks tasks currently stored by the application
     * @param divider line printed after the response
     */
    private static void unmarkTask(String command, ArrayList<Task> tasks, String divider) {
        String number = command.substring(7);
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            System.out.println("Please enter a valid task number.");
            System.out.println(divider);
            return;
        }

        tasks.get(index).markAsUndone();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + tasks.get(index));
        System.out.println(divider);
    }

    /**
     * Deletes the task selected by a user command.
     *
     * @param command complete delete command entered by the user
     * @param tasks tasks currently stored by the application
     * @param divider line printed after the response
     */
    private static void deleteTask(String command, ArrayList<Task> tasks, String divider) {
        String number = command.substring(7);
        int index = getTaskIndex(number, tasks.size());
        if (index == -1) {
            System.out.println("Please enter a valid task number.");
            System.out.println(divider);
            return;
        }

        Task removedTask = tasks.remove(index);
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
     * @param divider line printed after the response
     * @throws JassaBotException if the description or due-time marker is missing
     */
    private static void addDeadline(String command, ArrayList<Task> tasks, String divider)
            throws JassaBotException {
        int byIndex = command.indexOf(" /by ");
        String description = byIndex == -1 ? command.substring(8).trim()
                : command.substring(8, byIndex).trim();

        if (description.isEmpty()) {
            throw new JassaBotException("The description of a deadline cannot be empty.");
        }
        if (byIndex == -1) {
            throw new JassaBotException("A deadline needs '/by' followed by its due time.");
        }
        String byTime = command.substring(byIndex + 5).trim();

        Deadline newDeadline = new Deadline(description, byTime);
        tasks.add(newDeadline);
        showAddedTask(newDeadline, tasks.size(), divider);
    }

    /**
     * Validates an event command and adds the resulting task.
     *
     * @param command complete event command entered by the user
     * @param tasks tasks currently stored by the application
     * @param divider line printed after the response
     * @throws JassaBotException if the description or time markers are missing
     */
    private static void addEvent(String command, ArrayList<Task> tasks, String divider)
            throws JassaBotException {
        int fromIndex = command.indexOf(" /from ");
        int toIndex = command.indexOf(" /to ");
        String description = fromIndex == -1 ? command.substring(5).trim()
                : command.substring(5, fromIndex).trim();

        if (description.isEmpty()) {
            throw new JassaBotException("The description of an event cannot be empty.");
        }
        if (fromIndex == -1 || toIndex == -1 || toIndex <= fromIndex) {
            throw new JassaBotException("An event needs both '/from' and '/to' time markers.");
        }
        String from = command.substring(fromIndex + 7, toIndex).trim();
        String to = command.substring(toIndex + 5).trim();

        Event newEvent = new Event(description, from, to);
        tasks.add(newEvent);
        showAddedTask(newEvent, tasks.size(), divider);
    }

    /**
     * Validates a todo command and adds the resulting task.
     *
     * @param command complete todo command entered by the user
     * @param tasks tasks currently stored by the application
     * @param divider line printed after the response
     * @throws JassaBotException if the description is missing
     */
    private static void addTodo(String command, ArrayList<Task> tasks, String divider)
            throws JassaBotException {
        String description = command.substring(4).trim();
        if (description.isEmpty()) {
            throw new JassaBotException("The description of a todo cannot be empty.");
        }

        Todo newTodo = new Todo(description);
        tasks.add(newTodo);
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
     * Converts a user-entered, one-based task number into a valid array index.
     *
     * @param number the task number entered by the user
     * @param numberOfTasks the number of tasks currently stored
     * @return the zero-based task index, or {@code -1} if the number is invalid
     */
    private static int getTaskIndex(String number, int numberOfTasks) {
        try {
            int taskNumber = Integer.parseInt(number);
            if (taskNumber < 1 || taskNumber > numberOfTasks) {
                return -1;
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
