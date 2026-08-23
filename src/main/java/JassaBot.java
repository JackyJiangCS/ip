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
                    System.out.println((i + 1) + "." + tasks[i]);
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
                    System.out.println("  " + tasks[index]);
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
                    System.out.println("  " + tasks[index]);
                    System.out.println(divider);
                }

            } else if (command.startsWith("deadline ")) {
                int byIndex = command.indexOf(" /by ");

                if(byIndex == -1) {
                    System.out.println("Oops! Please use '/by' to specify the deadline.");
                    System.out.println(divider);
                } else {
                    //get description and time from command text
                    String description = command.substring(9, byIndex).trim();
                    String byTime = command.substring(byIndex + 5).trim();

                    Deadline newDeadline = new Deadline(description, byTime);
                    tasks[taskCount] = newDeadline;
                    taskCount++;

                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + newDeadline);
                    System.out.println("Now you have " + taskCount + " tasks in the list.");
                    System.out.println(divider);
                }

            } else if (command.startsWith("event ")) {
                int fromIndex = command.indexOf(" /from ");
                int toIndex = command.indexOf(" /to ");

                if (fromIndex == -1 || toIndex == -1 || toIndex <= fromIndex) {
                    System.out.println("Oops! Please use '/from' and '/to' to specify the event.");
                    System.out.println(divider);
                } else {
                    String description = command.substring(6, fromIndex).trim();
                    String from = command.substring(fromIndex + 7, toIndex).trim();
                    String to = command.substring(toIndex + 5).trim();
                    Event newEvent = new Event(description, from, to);
                    tasks[taskCount] = newEvent;
                    taskCount++;

                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + newEvent);
                    System.out.println("Now you have " + taskCount + " tasks in the list.");
                    System.out.println(divider);
                }

            } else {
                String description = command.startsWith("todo ") ? command.substring(5).trim() : command;
                Todo newTodo = new Todo(description);
                tasks[taskCount] = newTodo;
                taskCount++;

                System.out.println("Got it. I've added this task:");
                System.out.println("  " + newTodo);
                System.out.println("Now you have " + taskCount + " tasks in the list.");
                System.out.println(divider);
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
