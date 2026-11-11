import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Swing GUI starter for the to-do list tracker.
 * The window, form, table, and file loading are built for you.
 * Students finish the TODO methods (adding a task, sorting, refreshing the table).
 *
 * Needs the finished Task, Difficulty, Completed, DueTime, and Sorts classes.
 * On startup it loads "tasks.txt" from the project's root folder (or the file path
 * given as the first program argument). The Load File button loads another file.
 *
 * Each line of the file is one task, with parts separated by | :
 *   description|difficulty|completed|month|day|time AM/PM
 * Example:
 *   Finish essay|hard|no|11|20|9:00 AM
 * Blank lines and lines starting with # are ignored.
 *
 * Run main() to open the window.
 */
public class TodoGui extends JFrame {

    private final ArrayList<Task> tasks = new ArrayList<>();

    // Form fields
    private final JTextField descriptionField = new JTextField(20);
    private final JComboBox<String> difficultyBox =
            new JComboBox<>(new String[]{"easy", "medium", "hard"});
    private final JCheckBox completedBox = new JCheckBox("Completed");
    private final JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
    private final JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
    private final JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(12, 1, 12, 1)); // 1-12
    private final JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
    private final JComboBox<String> amPmBox = new JComboBox<>(new String[]{"AM", "PM"});

    // Table
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Description", "Difficulty", "Completed", "Due"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };

    private final JLabel statusLabel = new JLabel(" ");

    public TodoGui(String startFile) {
        super("To-Do List Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        setSize(800, 450);
        setLocationRelativeTo(null);

        // Load the starter file. The table stays empty until refreshTable() is finished.
        loadFromFile(new File(startFile));
    }

    // ---------- Provided: building the window ----------

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Description:"));
        form.add(descriptionField);
        form.add(new JLabel("Difficulty:"));
        form.add(difficultyBox);
        form.add(completedBox);
        form.add(new JLabel("Month:"));
        form.add(monthSpinner);
        form.add(new JLabel("Day:"));
        form.add(daySpinner);
        form.add(new JLabel("Hour:"));
        form.add(hourSpinner);
        form.add(new JLabel("Min:"));
        form.add(minuteSpinner);
        form.add(amPmBox);

        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(e -> addTask());
        form.add(addButton);
        return form;
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton loadButton = new JButton("Load File...");
        loadButton.addActionListener(e -> chooseAndLoadFile());
        buttons.add(loadButton);

        buttons.add(new JLabel("   Sort by:"));

        JButton byDifficulty = new JButton("Difficulty");
        byDifficulty.addActionListener(e -> sortBy(Task::getDifficulty, "difficulty"));
        buttons.add(byDifficulty);

        JButton byCompleted = new JButton("Completed");
        byCompleted.addActionListener(e -> sortBy(Task::getCompleted, "completed"));
        buttons.add(byCompleted);

        JButton byDueTime = new JButton("Due Time");
        byDueTime.addActionListener(e -> sortBy(Task::getDueTime, "due time"));
        buttons.add(byDueTime);

        bottom.add(buttons, BorderLayout.CENTER);
        bottom.add(statusLabel, BorderLayout.SOUTH);
        return bottom;
    }

    // ---------- Provided: loading tasks from a file ----------

    /** Lets the user pick a task file, then loads it (replacing the current list). */
    private void chooseAndLoadFile() {
        JFileChooser chooser = new JFileChooser(new File("."));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            loadFromFile(chooser.getSelectedFile());
        }
    }

    /** Replaces the task list with the tasks in the file, then calls refreshTable(). */
    private void loadFromFile(File file) {
        ArrayList<Task> loaded = new ArrayList<>();
        int skipped = 0;
        try (Scanner in = new Scanner(file)) {
            while (in.hasNextLine()) {
                String line = in.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    loaded.add(parseTask(line));
                } catch (RuntimeException e) {
                    skipped++;
                }
            }
        } catch (FileNotFoundException e) {
            showError("Could not find \"" + file.getPath() + "\".\n"
                    + "Put tasks.txt in the project's root folder, or use Load File.");
            return;
        }
        tasks.clear();
        tasks.addAll(loaded);
        refreshTable();
        showStatus("Loaded " + loaded.size() + " task(s) from " + file.getName()
                + (skipped > 0 ? " (" + skipped + " bad line(s) skipped)" : ""));
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

    // ---------- TODO: students finish these ----------

    /**
     * TODO 1: Read the form fields and add a new Task to the tasks list.
     *   - description: descriptionField.getText()  (show an error if it is empty)
     *   - difficulty:  new Difficulty((String) difficultyBox.getSelectedItem())
     *   - completed:   new Completed(completedBox.isSelected())
     *   - due time:    new DueTime(month, day, hour, minute, amPm)
     *                  (get each number with (int) monthSpinner.getValue(), etc.,
     *                   and the AM/PM text with (String) amPmBox.getSelectedItem())
     * Then clear the description field and call refreshTable().
     * Hint: DueTime throws IllegalArgumentException for bad values. Catch it and
     *       show the message with showError(...).
     */
    private void addTask() {
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            showError("Please enter a description.");
            return;
        }
        try {
            Difficulty difficulty = new Difficulty((String) difficultyBox.getSelectedItem());
            Completed completed = new Completed(completedBox.isSelected());
            DueTime due = new DueTime(
                    (int) monthSpinner.getValue(),
                    (int) daySpinner.getValue(),
                    (int) hourSpinner.getValue(),
                    (int) minuteSpinner.getValue(),
                    (String) amPmBox.getSelectedItem());
            tasks.add(new Task(description, difficulty, completed, due));
            descriptionField.setText("");
            refreshTable();
            showStatus("Added \"" + description + "\"");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /**
     * TODO 2: Sort the tasks by the given property, then show the result.
     *   - Copy the list into an array: Task[] arr = tasks.toArray(new Task[0]);
     *   - Sort it: Sorts.bubbleSortBy(arr, key);
     *   - Put the sorted tasks back into the list (clear it, then add each one).
     *   - Call refreshTable().
     *   - Set the status label to say whether Sorts.isSortedBy(arr, key) is true,
     *     for example: "Sorted by difficulty: verified".
     */
    private <K extends Comparable<K>> void sortBy(Function<Task, K> key, String propertyName) {
        Task[] a = tasks.toArray(new Task[0]);
        if(propertyName.equals("due time")){
            propertyName = "dueTime";
        }
        Sorts.bubbleSort(a, propertyName);
        tasks.clear();
        for(Task t : a){
            tasks.add(t);
        }
        refreshTable();
        statusLabel.setText("Sorted by difficulty: " + Sorts.isSorted(a,propertyName));
    }

    /**
     * TODO 3: Rebuild the table from the tasks list.
     *   - tableModel.setRowCount(0) clears the table.
     *   - For each task, call tableModel.addRow(new Object[]{ ... }) with the
     *     description, difficulty, completed, and due time (toString() is fine,
     *     the due time already prints with AM/PM).
     * Do this one first: the loaded tasks will appear as soon as it works.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Task t : tasks) {
            tableModel.addRow(new Object[]{
                    t.getDescription(), t.getDifficulty(), t.getCompleted(), t.getDueTime()});
        }
    }

    // ---------- Provided helpers ----------

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Oops", JOptionPane.WARNING_MESSAGE);
    }

    private void showStatus(String message) {
        statusLabel.setText(" " + message);
    }

    public static void main(String[] args) {
        String startFile = args.length > 0 ? args[0] : "tasks.txt";
        SwingUtilities.invokeLater(() -> new TodoGui(startFile).setVisible(true));
    }
}
