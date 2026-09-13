package jassabot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jassabot.parser.CommandType;

/**
 * Tests the command entry point shared by the GUI and console, including failed saves.
 */
public class JassaBotTest {
    private final Path directory = Path.of("build", "test-data", "bot-" + UUID.randomUUID());
    private final Path dataFile = directory.resolve("tasks.txt");
    private JassaBot bot;

    @BeforeEach
    public void setUp() throws IOException {
        Files.createDirectories(directory);
        bot = new JassaBot(dataFile);
    }

    @AfterEach
    public void tearDown() throws IOException {
        List<Path> paths;
        try (Stream<Path> stream = Files.walk(directory)) {
            paths = stream.sorted(Comparator.reverseOrder()).toList();
        }
        for (Path path : paths) {
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void getResponse_taskLifecycle_preservesStateAndSavesChanges() {
        assertEquals("Got it. I've added this task:\n  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", bot.getResponse("  todo read book  "));
        assertEquals(CommandType.TODO, bot.getCommandType());
        assertEquals("Got it. I've added this task:\n  [D][ ] return book (by: Dec 2 2019, 6:00 PM)\n"
                + "Now you have 2 tasks in the list.",
                bot.getResponse("deadline return book /by 2019-12-02 1800"));
        assertEquals("Got it. I've added this task:\n"
                + "  [E][ ] meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)\n"
                + "Now you have 3 tasks in the list.",
                bot.getResponse("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600"));
        assertTrue(bot.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(bot.getResponse("unmark 1").contains("[T][ ] read book"));
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)", bot.getResponse("find BOOK"));
        assertTrue(bot.getResponse("delete 2").contains("Now you have 2 tasks in the list."));
        String listing = bot.getResponse("list");
        assertTrue(listing.contains("2.[E][ ] meeting"));
        assertEquals(listing, new JassaBot(dataFile).getResponse("list"));
    }

    @Test
    public void getResponse_invalidCommands_returnsErrorsWithoutChangingTasks() {
        bot.getResponse("todo keep me");
        String listing = bot.getResponse("list");
        for (String command : List.of(" ", "todo", "deadline bad /by 2019-02-29", "event",
                "find", "unknown", "mark 0", "unmark 99999999999999999", "delete")) {
            String response = bot.getResponse(command);
            assertFalse(response.isBlank());
            assertEquals(CommandType.UNKNOWN, bot.getCommandType(), command);
            assertEquals(listing, bot.getResponse("list"), command);
        }
        assertEquals("OOPS!!! Please enter a command.", bot.getResponse(" "));
    }

    @Test
    public void getResponse_markerPrefixes_skipsPartialMatchesAndFindsCompleteMarkers() {
        assertTrue(bot.getResponse("deadline compare /bypass /by 2019-12-02")
                .contains("[D][ ] compare /bypass (by: Dec 2 2019)"));
        assertTrue(bot.getResponse("event travel /fromage /together /from 2019-12-02 /to 2019-12-03")
                .contains("[E][ ] travel /fromage /together"));
        assertEquals("OOPS!!! A deadline needs '/by' followed by its due time.",
                bot.getResponse("deadline compare /bypass"));
    }

    @Test
    public void getWelcome_malformedStorage_reportsWarningsAndLoadsValidTasks() throws IOException {
        Files.writeString(dataFile, "T | 1 | saved task\nX | 0 | invalid\n");
        bot = new JassaBot(dataFile);
        assertEquals("Hello! I'm JassaBot.\nWhat can I do for you?\n"
                + "WARNING: Skipped data line 2: unknown task type 'X'.", bot.getWelcome());
        assertEquals("Here are the tasks in your list:\n1.[T][X] saved task", bot.getResponse("list"));
    }

    @Test
    public void getResponse_failedSaves_rollBackEveryMutation() throws IOException {
        bot.getResponse("todo keep first");
        bot.getResponse("todo keep second");
        bot.getResponse("mark 2");
        String listing = bot.getResponse("list");
        Files.delete(dataFile);
        Files.createDirectory(dataFile);
        Files.writeString(dataFile.resolve("blocker"), "Force a failed replacement on all platforms.");
        for (String command : List.of("todo rejected", "deadline rejected /by 2019-12-02",
                "event rejected /from 2019-12-02 /to 2019-12-03", "mark 1", "mark 2",
                "unmark 1", "unmark 2", "delete 1")) {
            assertEquals("OOPS!!! I couldn't save your tasks, so no changes were made.",
                    bot.getResponse(command), command);
            assertEquals(CommandType.UNKNOWN, bot.getCommandType());
            assertEquals(listing, bot.getResponse("list"), command);
        }
    }

    @Test
    public void getResponse_bye_requestsExitAndPreventsFurtherChanges() throws IOException {
        bot.getResponse("todo saved before exit");
        String saved = Files.readString(dataFile);
        assertFalse(bot.isExitRequested());
        assertEquals("Bye. Hope to see you again soon!", bot.getResponse("  bye  "));
        assertEquals(CommandType.BYE, bot.getCommandType());
        assertTrue(bot.isExitRequested());
        assertEquals("Bye. Hope to see you again soon!", bot.getResponse("todo too late"));
        assertEquals(saved, Files.readString(dataFile));
    }
}
