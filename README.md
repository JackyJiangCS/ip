# JassaBot project

This is a project template for the JassaBot chatbot. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/jassabot/JassaBot.java` file, right-click it, and choose `Run JassaBot.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
   Hello! I'm JassaBot.
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.


## Running the GUI or console

Use JDK 25 for both interfaces. From the project directory on Windows:

- GUI: `.\gradlew.bat run`, or run `jassabot.Launcher.main()` in IntelliJ.
- Console: `.\gradlew.bat --console=plain runConsole`, or run `jassabot.JassaBot.main()` in IntelliJ.
- Packaged GUI: `.\gradlew.bat shadowJar`, then `java -jar build/libs/jassabot.jar`.
- Packaged console: `java -cp build/libs/jassabot.jar jassabot.JassaBot`.

On macOS/Linux, use `./gradlew` in place of `.\gradlew.bat`.
Both interfaces load and save `data/jassabot.txt` relative to the working directory.
Run one interface at a time so separate sessions do not overwrite each other's saved tasks.

In the GUI, type a command and press Enter or click Send. Commands include `todo read book`,
`deadline return book /by 2019-12-02 1800`, `event meeting /from 2019-12-02 1400 /to 2019-12-02 1600`,
`list`, `mark 1`, `unmark 1`, `delete 1`, and `find book`.
The `bye` command exits the console immediately; in the GUI it shows a farewell and closes after one second.
