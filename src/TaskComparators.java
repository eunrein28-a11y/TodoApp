import java.util.Comparator;

public class TaskComparators {
    public static class ByDifficulty implements Comparator<Task>{
        public int compare(Task a, Task b){
            return a.getDifficulty().compareTo(b.getDifficulty());
        }
    }

    public static class ByCompleted implements Comparator<Task>{
        public int compare(Task a, Task b) {
            return a.getCompleted().compareTo(b.getCompleted());
        }
    }

    public static class ByDueTime implements Comparator<Task>{
        public int compare(Task a, Task b) {
            return a.getDueTime().compareTo(b.getDueTime());
        }
    }
}
