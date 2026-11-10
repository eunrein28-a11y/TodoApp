/**
 * Run this file to check your work. No libraries needed.
 * Right-click TodoTest in IntelliJ and choose Run 'TodoTest.main()'.
 *
 * It checks the five methods you complete, one group at a time:
 *   Difficulty.compareTo, Completed.compareTo, DueTime.compareTo,
 *   Sorts.isSorted, Sorts.bubbleSort
 *
 * isSorted checks never call bubbleSort, and bubbleSort checks never call isSorted.
 * isSorted and bubbleSort use less(), which uses your compareTo methods,
 * so finish the three compareTo methods first.
 */
public class TodoTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        runGroup("Difficulty.compareTo", 1);
        runGroup("Completed.compareTo", 2);
        runGroup("DueTime.compareTo", 3);
        runGroup("Sorts.isSorted", 4);
        runGroup("Sorts.bubbleSort", 5);

        System.out.println();
        System.out.println("Passed: " + passed + "   Failed: " + failed);
        if (failed == 0) {
            System.out.println("All checks passed. Nice work!");
        } else {
            System.out.println("Fix the FAIL lines above, then run again.");
        }
    }

    /** Runs one group. If your code crashes, the group reports it and the rest still run. */
    private static void runGroup(String title, int group) {
        System.out.println();
        System.out.println("=== " + title + " ===");
        try {
            if (group == 1) testDifficulty();
            if (group == 2) testCompleted();
            if (group == 3) testDueTime();
            if (group == 4) testIsSorted();
            if (group == 5) testBubbleSort();
        } catch (Exception e) {
            failed++;
            System.out.println("FAIL  crashed with " + e);
        }
    }

    /** Prints PASS or FAIL for one check. */
    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }

    // ---------- helpers ----------

    private static Task task(String name, String difficulty, boolean done, int month, int day, int hour) {
        return new Task(name, new Difficulty(difficulty), new Completed(done), new DueTime(month, day, hour, 0));
    }

    private static Task byDifficulty(String name, String difficulty) {
        return task(name, difficulty, false, 1, 1, 0);
    }

    private static Task byCompleted(String name, boolean done) {
        return task(name, "easy", done, 1, 1, 0);
    }

    private static Task byDueTime(String name, int month, int day, int hour) {
        return task(name, "easy", false, month, day, hour);
    }

    /** True if the array holds exactly these tasks, in this order. */
    private static boolean sameOrder(Task[] actual, Task... expected) {
        if (actual.length != expected.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if (actual[i] != expected[i]) return false;
        }
        return true;
    }

    // =====================================================
    // Difficulty.compareTo   (easy < medium < hard)
    // =====================================================

    private static void testDifficulty() {
        Difficulty easy = new Difficulty("easy");
        Difficulty medium = new Difficulty("medium");
        Difficulty hard = new Difficulty("hard");

        check("easy is less than medium", easy.compareTo(medium) < 0);
        check("medium is less than hard", medium.compareTo(hard) < 0);
        check("easy is less than hard", easy.compareTo(hard) < 0);
        check("hard is greater than easy", hard.compareTo(easy) > 0);
        check("medium is greater than easy", medium.compareTo(easy) > 0);
        check("hard is greater than medium", hard.compareTo(medium) > 0);
        check("easy equals easy (returns 0)", easy.compareTo(new Difficulty("easy")) == 0);
        check("medium equals medium (returns 0)", medium.compareTo(new Difficulty("medium")) == 0);
        check("hard equals hard (returns 0)", hard.compareTo(new Difficulty("hard")) == 0);
    }

    // =====================================================
    // Completed.compareTo   (not done < done)
    // =====================================================

    private static void testCompleted() {
        Completed notDone = new Completed(false);
        Completed done = new Completed(true);

        check("not done comes before done", notDone.compareTo(done) < 0);
        check("done comes after not done", done.compareTo(notDone) > 0);
        check("not done equals not done (returns 0)", notDone.compareTo(new Completed(false)) == 0);
        check("done equals done (returns 0)", done.compareTo(new Completed(true)) == 0);
    }

    // =====================================================
    // DueTime.compareTo   (month, then day, then hour, then minute)
    // =====================================================

    private static void testDueTime() {
        check("earlier month comes first",
                new DueTime(3, 28, 23, 59).compareTo(new DueTime(4, 1, 0, 0)) < 0);
        check("later month comes after",
                new DueTime(4, 1, 0, 0).compareTo(new DueTime(3, 28, 23, 59)) > 0);
        check("day decides when months match",
                new DueTime(5, 10, 23, 59).compareTo(new DueTime(5, 11, 0, 0)) < 0);
        check("later day comes after",
                new DueTime(5, 11, 0, 0).compareTo(new DueTime(5, 10, 23, 59)) > 0);
        check("hour decides when month and day match",
                new DueTime(5, 10, 8, 59).compareTo(new DueTime(5, 10, 9, 0)) < 0);
        check("later hour comes after",
                new DueTime(5, 10, 9, 0).compareTo(new DueTime(5, 10, 8, 59)) > 0);
        check("minute decides when everything else matches",
                new DueTime(5, 10, 9, 15).compareTo(new DueTime(5, 10, 9, 30)) < 0);
        check("later minute comes after",
                new DueTime(5, 10, 9, 30).compareTo(new DueTime(5, 10, 9, 15)) > 0);
        check("same time returns 0",
                new DueTime(11, 12, 18, 30).compareTo(new DueTime(11, 12, 18, 30)) == 0);
    }

    // =====================================================
    // Sorts.isSorted   (does NOT use bubbleSort)
    // =====================================================

    private static void testIsSorted() {
        check("empty array is sorted", Sorts.isSorted(new Task[0], "difficulty"));

        check("one task is sorted",
                Sorts.isSorted(new Task[]{byDifficulty("a", "hard")}, "difficulty"));

        Task[] sorted = {byDifficulty("a", "easy"), byDifficulty("b", "medium"), byDifficulty("c", "hard")};
        check("true when sorted by difficulty", Sorts.isSorted(sorted, "difficulty"));

        Task[] unsorted = {byDifficulty("a", "hard"), byDifficulty("b", "easy"), byDifficulty("c", "medium")};
        check("false when not sorted by difficulty", !Sorts.isSorted(unsorted, "difficulty"));

        Task[] equalNeighbors = {byDifficulty("a", "easy"), byDifficulty("b", "easy"), byDifficulty("c", "hard")};
        check("equal neighbors are allowed", Sorts.isSorted(equalNeighbors, "difficulty"));

        Task[] badAtEnd = {byDifficulty("a", "easy"), byDifficulty("b", "medium"),
                byDifficulty("c", "hard"), byDifficulty("d", "medium")};
        check("catches a bad pair at the end", !Sorts.isSorted(badAtEnd, "difficulty"));

        Task[] badAtStart = {byDifficulty("a", "medium"), byDifficulty("b", "easy"), byDifficulty("c", "hard")};
        check("catches a bad pair at the start", !Sorts.isSorted(badAtStart, "difficulty"));

        check("true when sorted by completed",
                Sorts.isSorted(new Task[]{byCompleted("a", false), byCompleted("b", true)}, "completed"));
        check("false when not sorted by completed",
                !Sorts.isSorted(new Task[]{byCompleted("a", true), byCompleted("b", false)}, "completed"));

        Task[] timeSorted = {byDueTime("a", 1, 5, 9), byDueTime("b", 1, 5, 10), byDueTime("c", 2, 1, 0)};
        Task[] timeUnsorted = {byDueTime("a", 2, 1, 0), byDueTime("b", 1, 5, 10), byDueTime("c", 1, 5, 9)};
        check("true when sorted by dueTime", Sorts.isSorted(timeSorted, "dueTime"));
        check("false when not sorted by dueTime", !Sorts.isSorted(timeUnsorted, "dueTime"));

        // Sorted by difficulty, but NOT by completed.
        Task[] mixed = {task("a", "easy", true, 1, 1, 0), task("b", "hard", false, 1, 1, 0)};
        check("uses the field you ask for (difficulty)", Sorts.isSorted(mixed, "difficulty"));
        check("uses the field you ask for (completed)", !Sorts.isSorted(mixed, "completed"));
    }

    // =====================================================
    // Sorts.bubbleSort   (does NOT use isSorted)
    // Each result is compared to a hand-written expected order.
    // =====================================================

    private static void testBubbleSort() {
        Task[] empty = new Task[0];
        Sorts.bubbleSort(empty, "difficulty");
        check("empty array does not crash", empty.length == 0);

        Task only = byDifficulty("only", "hard");
        Task[] one = {only};
        Sorts.bubbleSort(one, "difficulty");
        check("one task stays put", sameOrder(one, only));

        Task easy = byDifficulty("easy", "easy");
        Task medium = byDifficulty("medium", "medium");
        Task hard = byDifficulty("hard", "hard");
        Task[] a = {hard, easy, medium};
        Sorts.bubbleSort(a, "difficulty");
        check("sorts by difficulty", sameOrder(a, easy, medium, hard));

        Task t1 = byDifficulty("t1", "easy");
        Task t2 = byDifficulty("t2", "medium");
        Task t3 = byDifficulty("t3", "hard");
        Task[] reversed = {t3, t3, t2, t1, t1};
        Sorts.bubbleSort(reversed, "difficulty");
        check("sorts reverse-order input", sameOrder(reversed, t1, t1, t2, t3, t3));

        Task e1 = byDifficulty("e1", "easy");
        Task e2 = byDifficulty("e2", "easy");
        Task h = byDifficulty("h", "hard");
        Task[] largest = {h, e1, e2};
        Sorts.bubbleSort(largest, "difficulty");
        check("largest task ends up last", largest[2] == h);

        Task s1 = byDifficulty("s1", "easy");
        Task s2 = byDifficulty("s2", "medium");
        Task s3 = byDifficulty("s3", "hard");
        Task[] already = {s1, s2, s3};
        Sorts.bubbleSort(already, "difficulty");
        check("already sorted array stays the same", sameOrder(already, s1, s2, s3));

        Task first = byDifficulty("first", "medium");
        Task second = byDifficulty("second", "medium");
        Task low = byDifficulty("low", "easy");
        Task[] ties = {first, second, low};
        Sorts.bubbleSort(ties, "difficulty");
        check("equal tasks keep their original order", sameOrder(ties, low, first, second));

        Task n1 = byCompleted("n1", false);
        Task n2 = byCompleted("n2", false);
        Task d = byCompleted("d", true);
        Task[] byDone = {d, n1, n2};
        Sorts.bubbleSort(byDone, "completed");
        check("sorts by completed", sameOrder(byDone, n1, n2, d));

        Task jan = byDueTime("jan", 1, 20, 8);
        Task feb = byDueTime("feb", 2, 1, 8);
        Task febLater = byDueTime("febLater", 2, 1, 15);
        Task mar = byDueTime("mar", 3, 5, 0);
        Task[] byTime = {mar, febLater, jan, feb};
        Sorts.bubbleSort(byTime, "dueTime");
        check("sorts by dueTime", sameOrder(byTime, jan, feb, febLater, mar));

        Task x = task("x", "hard", false, 1, 1, 0);
        Task y = task("y", "easy", true, 1, 1, 0);
        Task[] fieldA = {x, y};
        Sorts.bubbleSort(fieldA, "difficulty");
        check("uses the field you ask for (difficulty: easy first)", sameOrder(fieldA, y, x));
        Task[] fieldB = {x, y};
        Sorts.bubbleSort(fieldB, "completed");
        check("uses the field you ask for (completed: not done first)", sameOrder(fieldB, x, y));
    }
}
