# Console UI Test Plan

## How to run the application

Use Java 25. Compile into an ignored temporary directory and start a fresh process for each test case:

```powershell
$uiTestClasses = Join-Path $env:TEMP 'jassabot-ui-test-classes'
Remove-Item -Recurse -Force $uiTestClasses -ErrorAction SilentlyContinue
javac -d $uiTestClasses src\main\java\*.java
java -cp $uiTestClasses JassaBot
```

The expected startup output for every test case is:

```text
____________________________________________________________
   _                         ____        _
  | | __ _ ___ ___  __ _    | __ )  ___ | |_
  | |/ _` / __/ __|/ _` |   |  _ \ / _ \| __|
  | | (_| \__ \__ \ (_| |   | |_) | (_) | |_
 _|_|\__,_|___/___/\__,_|   |____/ \___/ \__|
Hello! I'm JassaBot.
What can I do for you?
____________________________________________________________
```

Each case below is one fresh console session. After sending an input, compare the next output block exactly, including whitespace and divider lines.

## Test cases

### TC-01 — Exit politely

**Aim:** Verify that the application acknowledges the exit command and terminates.

**Inputs and expected output:**

1. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

### TC-02 — Add and list the three task types

**Aim:** Verify that Todo, Deadline, and Event commands retain their type and date or time details in `list`.

**Inputs and expected output:**

1. Input:

   ```text
   todo read book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
   ____________________________________________________________
   ```

2. Input:

   ```text
   deadline return book /by Sunday
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Got it. I've added this task:
     [D][ ] return book (by: Sunday)
   Now you have 2 tasks in the list.
   ____________________________________________________________
   ```

3. Input:

   ```text
   event project meeting /from Mon 2pm /to 4pm
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Got it. I've added this task:
     [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Now you have 3 tasks in the list.
   ____________________________________________________________
   ```

4. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] read book
   2.[D][ ] return book (by: Sunday)
   3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
   ____________________________________________________________
   ```

5. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

### TC-03 — Mark and unmark a task

**Aim:** Verify that task completion state changes are reflected in the confirmation and list output.

**Inputs and expected output:**

1. Input:

   ```text
   todo read book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Got it. I've added this task:
     [T][ ] read book
   Now you have 1 tasks in the list.
   ____________________________________________________________
   ```

2. Input:

   ```text
   mark 1
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Nice! I've marked this task as done:
     [T][X] read book
   ____________________________________________________________
   ```

3. Input:

   ```text
   unmark 1
   ```

   Expected output:

   ```text
   ____________________________________________________________
   OK, I've marked this task as not done yet:
     [T][ ] read book
   ____________________________________________________________
   ```

4. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] read book
   ____________________________________________________________
   ```

5. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

### TC-04 — Reject an invalid task number

**Aim:** Verify that a task number outside the list is rejected without changing the task.

**Inputs and expected output:**

1. Input:

   ```text
   mark 1
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Please enter a valid task number.
   ____________________________________________________________
   ```

2. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

### TC-05 — Reject incomplete date commands

**Aim:** Verify that missing date markers do not add an incomplete Deadline or Event task.

**Inputs and expected output:**

1. Input:

   ```text
   deadline return book
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Oops! Please use '/by' to specify the deadline.
   ____________________________________________________________
   ```

2. Input:

   ```text
   event project meeting /from Mon 2pm
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Oops! Please use '/from' and '/to' to specify the event.
   ____________________________________________________________
   ```

3. Input:

   ```text
   list
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Here are the tasks in your list:
   ____________________________________________________________
   ```

4. Input:

   ```text
   bye
   ```

   Expected output:

   ```text
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```
