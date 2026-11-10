/** Sorting utilities. Students finish isSorted and bubbleSort. */
public class Sorts {

    /**
     * Provided: is task v less than task w, judged by the given field?
     * field is "difficulty", "completed", or "dueTime".
     * It uses the compareTo methods you finished in Difficulty, Completed, and DueTime.
     */
    public static boolean less(Task v, Task w, String field) {
        switch (field) {
            case "difficulty":
                return v.getDifficulty().compareTo(w.getDifficulty()) < 0;
            case "completed":
                return v.getCompleted().compareTo(w.getCompleted()) < 0;
            case "dueTime":
                return v.getDueTime().compareTo(w.getDueTime()) < 0;
            default:
                throw new IllegalArgumentException("Unknown field: " + field);
        }
    }

    /** Provided: swap a[i] and a[j]. */
    public static void exchange(Task[] a, int i, int j) {
        Task swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    /**
     * TODO 1: Is a in nondecreasing (ascending) order by the given field?
     * Loop through the array. If any task is less than the one before it,
     * return false. Otherwise return true.
     * Use: less(a[i], a[i - 1], field)
     */
    public static boolean isSorted(Task[] a, String field) {
        for(int i = 1; i < a.length; i++){
            if(less(a[i],a[i-1],field)){
                return false;
            }
        }
        return true;
    }

    /**
     * TODO 2: Bubble sort a into ascending order by the given field.
     *   - Repeat passes. In each pass, walk j from 0 up to the last unsorted pair.
     *   - If less(a[j + 1], a[j], field) is true, call exchange(a, j, j + 1).
     *   - After each pass the largest remaining task has bubbled to the end,
     *     so each pass can stop one spot earlier.
     *   - Challenge: stop early if a whole pass makes no swaps.
     */
    public static void bubbleSort(Task[] a, String field) {
        for(int i = 0; i < a.length; i++){
            int count = 0;
            for(int j = 0; j < a.length - 1 - i; j++){
                if(less(a[j + 1], a[j], field)){
                    exchange(a,j,j+1);
                    count++;
                }
            }
            if(count == 0){
                return;
            }
        }
    }
}
