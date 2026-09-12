package jassabot.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import jassabot.task.Task;

/**
 * Formats shared messages for the console or a GUI response collector.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = "   _                         ____        _\n"
            + "  | | __ _ ___ ___  __ _    | __ )  ___ | |_\n"
            + "  | |/ _` / __/ __|/ _` |   |  _ \\ / _ \\| __|\n"
            + "  | | (_| \\__ \\__ \\ (_| |   | |_) | (_) | |_\n"
            + " _|_|\\__,_|___/___/\\__,_|   |____/ \\___/ \\__|";

    private final Scanner scanner;
    private final Consumer<String> output;
    private final boolean hasConsoleDecorations;

    /**
     * Creates a UI connected to the process's standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
        hasConsoleDecorations = true;
    }

    /**
     * Creates a message formatter that sends plain response lines to a GUI collector.
     *
     * @param output Destination for each formatted line.
     */
    public Ui(Consumer<String> output) {
        scanner = new Scanner("");
        this.output = output;
        hasConsoleDecorations = false;
    }

    /**
     * Displays a complete command response with the console's original dividers.
     *
     * @param response Message returned by the chatbot.
     */
    public void showResponse(String response) {
        showResponseStart();
        output.accept(response);
        showResponseStart();
    }

    /**
     * Returns whether another command can be read without consuming it.
     *
     * @return {@code true} when another input line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return Next command line without surrounding whitespace.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays the application banner and greeting. */
    public void showWelcome() {
        showResponseStart();
        if (hasConsoleDecorations) {
            output.accept(BANNER);
        }
        output.accept("Hello! I'm JassaBot.");
        output.accept("What can I do for you?");
        showResponseStart();
    }

    /**
     * Displays non-fatal problems found while loading saved tasks.
     *
     * @param warnings User-facing storage warnings.
     */
    public void showLoadingWarnings(List<String> warnings) {
        for (String warning : warnings) {
            output.accept("WARNING: " + warning);
        }
        if (!warnings.isEmpty()) {
            showResponseStart();
        }
    }

    /** Displays the divider that begins a response to a command. */
    public void showResponseStart() {
        if (hasConsoleDecorations) {
            output.accept(DIVIDER);
        }
    }

    /**
     * Displays every task in its current list position.
     *
     * @param tasks Tasks currently stored by the application.
     */
    public void showTaskList(List<Task> tasks) {
        output.accept("Here are the tasks in your list:");
        showNumberedTasks(tasks);
        showResponseStart();
    }

    /**
     * Displays tasks whose descriptions matched a search keyword.
     *
     * @param tasks matching tasks in their original list order
     */
    public void showMatchingTasks(List<Task> tasks) {
        output.accept("Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
        showResponseStart();
    }

    /**
     * Displays tasks as a one-based numbered list.
     *
     * @param tasks tasks to display in the supplied order
     */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output.accept((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was marked as completed.
     *
     * @param task Task that was marked as completed.
     */
    public void showTaskMarked(Task task) {
        output.accept("Nice! I've marked this task as done:");
        output.accept("  " + task);
        showResponseStart();
    }

    /**
     * Displays confirmation that a task was marked as incomplete.
     *
     * @param task Task that was marked as incomplete.
     */
    public void showTaskUnmarked(Task task) {
        output.accept("OK, I've marked this task as not done yet:");
        output.accept("  " + task);
        showResponseStart();
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task Task that was deleted.
     * @param numberOfTasks Number of tasks remaining in the list.
     */
    public void showTaskDeleted(Task task, int numberOfTasks) {
        output.accept("Noted. I've removed this task:");
        output.accept("  " + task);
        output.accept("Now you have " + numberOfTasks + " tasks in the list.");
        showResponseStart();
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param numberOfTasks Number of tasks now in the list.
     */
    public void showTaskAdded(Task task, int numberOfTasks) {
        output.accept("Got it. I've added this task:");
        output.accept("  " + task);
        output.accept("Now you have " + numberOfTasks + " tasks in the list.");
        showResponseStart();
    }

    /** Displays the shared invalid-task-number response. */
    public void showInvalidTaskNumber() {
        output.accept("Please enter a valid task number.");
        showResponseStart();
    }

    /**
     * Displays a recoverable command error.
     *
     * @param message User-facing explanation of the error.
     */
    public void showError(String message) {
        output.accept("OOPS!!! " + message);
        showResponseStart();
    }

    /** Displays the normal farewell requested by the {@code bye} command. */
    public void showGoodbye() {
        output.accept("Bye. Hope to see you again soon!");
        showResponseStart();
    }

    /** Displays the farewell used when the input stream closes. */
    public void showInputClosed() {
        showResponseStart();
        output.accept("Input closed. Goodbye!");
        showResponseStart();
    }
}
