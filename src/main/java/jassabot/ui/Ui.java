package jassabot.ui;

import java.util.List;
import java.util.Scanner;

import jassabot.task.Task;

/**
 * Handles all console input and output for JassaBot.
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

    /**
     * Creates a UI connected to the process's standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
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
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm JassaBot.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
    }

    /**
     * Displays non-fatal problems found while loading saved tasks.
     *
     * @param warnings User-facing storage warnings.
     */
    public void showLoadingWarnings(List<String> warnings) {
        for (String warning : warnings) {
            System.out.println("WARNING: " + warning);
        }
        if (!warnings.isEmpty()) {
            System.out.println(DIVIDER);
        }
    }

    /** Displays the divider that begins a response to a command. */
    public void showResponseStart() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays every task in its current list position.
     *
     * @param tasks Tasks currently stored by the application.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(DIVIDER);
    }

    /** Displays confirmation that a task was marked as completed. */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
        System.out.println(DIVIDER);
    }

    /** Displays confirmation that a task was marked as incomplete. */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
        System.out.println(DIVIDER);
    }

    /** Displays confirmation that a task was deleted. */
    public void showTaskDeleted(Task task, int numberOfTasks) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + numberOfTasks + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int numberOfTasks) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + numberOfTasks + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /** Displays the shared invalid-task-number response. */
    public void showInvalidTaskNumber() {
        System.out.println("Please enter a valid task number.");
        System.out.println(DIVIDER);
    }

    /** Displays a recoverable command error. */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
        System.out.println(DIVIDER);
    }

    /** Displays the normal farewell requested by the {@code bye} command. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }

    /** Displays the farewell used when the input stream closes. */
    public void showInputClosed() {
        System.out.println(DIVIDER);
        System.out.println("Input closed. Goodbye!");
        System.out.println(DIVIDER);
    }
}
