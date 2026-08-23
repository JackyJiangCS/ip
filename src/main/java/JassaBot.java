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

        Task[] tasks = new Task[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            System.out.println(divider);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(divider);
                break;
            } else if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    Task currentTask = tasks[i];
                    System.out.println((i + 1) + ".[" + currentTask.getStatusIcon() +
                            "] " + currentTask.description);
                }
                System.out.println(divider);
            } else if (command.startsWith("mark ")) {
                String number = command.substring(5);
                int index = getTaskIndex(number, taskCount);
                if (index == -1) {
                    System.out.println("Please enter a valid task number.");
                    System.out.println(divider);
                } else {
                    tasks[index].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  [" + tasks[index].getStatusIcon() + "] "
                            + tasks[index].getDescription());
                    System.out.println(divider);
                }

            } else if (command.startsWith("unmark ")) {
                String number = command.substring(7);
                int index = getTaskIndex(number, taskCount);
                if (index == -1) {
                    System.out.println("Please enter a valid task number.");
                    System.out.println(divider);
                } else {
                    tasks[index].markAsUndone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  [" + tasks[index].getStatusIcon() + "] "
                            + tasks[index].getDescription());
                    System.out.println(divider);
                }

            } else {
                Task newTask = new Task(command);
                tasks[taskCount] = newTask;
                System.out.println("added: " + command);
                System.out.println(divider);
                taskCount++;
            }
        }
    }

    /**
     * Converts a user-entered, one-based task number into a valid array index.
     *
     * @param number the task number entered by the user
     * @param taskCount the number of tasks currently stored
     * @return the zero-based task index, or {@code -1} if the number is invalid
     */
    private static int getTaskIndex(String number, int taskCount) {
        try {
            int taskNumber = Integer.parseInt(number);
            if (taskNumber < 1 || taskNumber > taskCount) {
                return -1;
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
