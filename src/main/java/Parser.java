/**
 * Interprets user input as commands understood by JassaBot.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Determines the type of a user-entered command.
     *
     * <p>The matching rules preserve the application's existing behavior. For example,
     * {@code deadline} is recognised so that JassaBot can report its missing description,
     * while {@code mark} without a task number remains an unknown command.</p>
     *
     * @param command complete line entered by the user
     * @return the matching command type, or {@link CommandType#UNKNOWN} if none matches
     */
    public static CommandType parseCommandType(String command) {
        if (command.equals("bye")) {
            return CommandType.BYE;
        } else if (command.equals("list")) {
            return CommandType.LIST;
        } else if (command.startsWith("mark ")) {
            return CommandType.MARK;
        } else if (command.startsWith("unmark ")) {
            return CommandType.UNMARK;
        } else if (command.startsWith("delete ")) {
            return CommandType.DELETE;
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        } else if (command.equals("event") || command.startsWith("event ")) {
            return CommandType.EVENT;
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            return CommandType.TODO;
        }
        return CommandType.UNKNOWN;
    }
}
