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
