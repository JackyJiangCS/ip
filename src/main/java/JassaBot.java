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

        String[] tasks = new String[100];
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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
                System.out.println(divider);
            } else {
                tasks[taskCount] = command;
                System.out.println("added: " + command);
                System.out.println(divider);
                taskCount++;
            }
        }
    }
}
