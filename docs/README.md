# JassaBot User Guide

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

Use `deadline DESCRIPTION /by DATE` to add a task that must be completed by a specific date and time.
Dates can use `yyyy-MM-dd` or `d/M/yyyy`. Add a 24-hour time in `HHmm` format when needed.

Example: `deadline return book /by 2/12/2019 1800`

JassaBot stores the value as a date and time and displays it in a friendlier format:

```text
[D][ ] return book (by: Dec 2 2019, 6:00 PM)
```

## Adding events

Use `event DESCRIPTION /from START /to END`. Both dates accept the same formats as deadlines.

Example: `event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600`

```text
[E][ ] project meeting (from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)
```

## Getting help

Type `help` in the GUI or console to display the command reference below. It works offline,
leaves your tasks unchanged, and lets you continue entering commands immediately.
The console adds its usual divider lines around this response.

```text
JassaBot commands:
todo DESCRIPTION - Add a task.
deadline DESCRIPTION /by DATE [TIME] - Add a deadline.
event DESCRIPTION /from DATE [TIME] /to DATE [TIME] - Add an event.
list - Show all tasks and their numbers.
find KEYWORD - Find descriptions containing KEYWORD, ignoring case.
mark NUMBER - Mark a task as done.
unmark NUMBER - Mark a task as not done.
delete NUMBER - Delete a task.
help - Show this help.
bye - Exit JassaBot.

Replace uppercase placeholders with your values. [TIME] is optional.
Commands are lowercase. Dates: yyyy-MM-dd or d/M/yyyy. Time: 24-hour HHmm.
Use numbers from list for mark, unmark, and delete; find renumbers its results.

Examples:
deadline return book /by 2019-12-02 1800
event meeting /from 2019-12-02 1400 /to 2019-12-02 1600
```

Use lowercase `help` with no arguments. Surrounding spaces and tabs are accepted.
`HELP`, `help todo`, `?`, `/help`, and `--help` are rejected with:

```text
OOPS!!! I don't recognise that command. Type help to see available commands.
```

Help uses the normal GUI reply style. Scroll up if its beginning is above the visible conversation.
