import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Console to-do list tracker that loads its tasks from a text file.
 * Provided complete; students do not need to edit this file.
 *
 * The file name comes from the first program argument, or defaults to "tasks.txt"
 * in the project's root folder (the folder IntelliJ runs from).
 *
 * Each line of the file is one task, with parts separated by | :
 *   description|difficulty|completed|month|day|time AM/PM
 * Example:
 *   Finish essay|hard|no|11|20|9:00 AM
 * Blank lines and lines starting with # are ignored.
 */
public class TodoApp {

    public static void main(String[] args) {
        String fileName = args.length > 0 ? args[0] : "tasks.txt";
        ArrayList<Task> tasks = loadTasks(fileName);
        System.out.println("Loaded " + tasks.size() + " task(s) from " + fileName);

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("\n1) Show tasks  2) Sort by difficulty"
                    + "  3) Sort by completed  4) Sort by due time  5) Quit");
            System.out.print("Choose: ");
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1": show(tasks); break;
                case "2": sortAndShow(tasks, new TaskComparators.ByDifficulty()); break;
                case "3": sortAndShow(tasks, new TaskComparators.ByCompleted()); break;
                case "4": sortAndShow(tasks, new TaskComparators.ByDueTime()); break;
                case "5": return;
                default: System.out.println("Please enter 1-5.");
            }
        }
    }

    /** Reads every valid task line from the file. Bad lines are skipped with a message. */
    private static ArrayList<Task> loadTasks(String fileName) {
        ArrayList<Task> tasks = new ArrayList<>();
        try (Scanner file = new Scanner(new File(fileName))) {
            int lineNumber = 0;
            while (file.hasNextLine()) {
                String line = file.nextLine().trim();
                lineNumber++;
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    tasks.add(parseTask(line));
                } catch (RuntimeException e) {
                    System.out.println("Skipping line " + lineNumber + " (" + e.getMessage() + "): " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find the file \"" + fileName + "\".");
            System.out.println("Put it in your project's root folder, or pass the path as a program argument.");
        }
        return tasks;
    }

    /** Turns one line like  Finish essay|hard|no|11|20|9:00 AM  into a Task. */
    private static Task parseTask(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 6) {
            throw new IllegalArgumentException("expected 6 parts separated by |");
        }
        String description = parts[0].trim();
        Difficulty difficulty = new Difficulty(parts[1]);
        Completed completed = new Completed(parts[2].trim().equalsIgnoreCase("yes"));
        int month = Integer.parseInt(parts[3].trim());
        int day = Integer.parseInt(parts[4].trim());

        // time part looks like "9:00 AM"
        String[] timeAndAmPm = parts[5].trim().split("\\s+");
        if (timeAndAmPm.length != 2) {
            throw new IllegalArgumentException("time must look like 9:00 AM");
        }
        String[] hm = timeAndAmPm[0].split(":");
        if (hm.length != 2) {
            throw new IllegalArgumentException("time must look like 9:00 AM");
        }
        DueTime due = new DueTime(month, day,
                Integer.parseInt(hm[0]), Integer.parseInt(hm[1]), timeAndAmPm[1]);

        return new Task(description, difficulty, completed, due);
    }

    /** field is "difficulty", "completed", or "dueTime". */
    private static void sortAndShow(ArrayList<Task> tasks, Comparator<Task> c) {
        Task[] arr = tasks.toArray(new Task[0]);
        Sorts.bubbleSort(arr, c);
        System.out.println("Sorted by " + c + ":");
        for (Task t : arr) System.out.println("  " + t);
        System.out.println("isSorted check: " + Sorts.isSorted(arr, c));
    }

    private static void show(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) System.out.println("No tasks.");
        for (Task t : tasks) System.out.println("  " + t);
    }
}
