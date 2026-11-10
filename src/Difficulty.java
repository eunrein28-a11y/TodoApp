/** Difficulty of a task: EASY < MEDIUM < HARD. */
public class Difficulty implements Comparable<Difficulty> {
    public static final String EASY = "easy";
    public static final String MEDIUM = "medium";
    public static final String HARD = "hard";

    private final String level; // "easy", "medium", or "hard"

    public Difficulty(String level) {
        level = level.trim().toLowerCase();
        if (!level.equals(EASY) && !level.equals(MEDIUM) && !level.equals(HARD)) {
            throw new IllegalArgumentException("Difficulty must be easy, medium, or hard");
        }
        this.level = level;
    }

    public String getLevel() { return level; }

    /** Helper: easy -> 0, medium -> 1, hard -> 2. */
    private int rank() {
        switch (level) {
            case EASY:   return 0;
            case MEDIUM: return 1;
            default:     return 2;
        }
    }

    /**
     * TODO: Compare this difficulty to other.
     * Return a negative number if this < other, 0 if equal, positive if this > other.
     * Throw a NullPointerException if other is null.
     * (Hint: you can use rank().)
     */
    @Override
    public int compareTo(Difficulty other) {
        if(this.rank() < other.rank()){
            return -1;
        }
        if(this.rank() > other.rank()){
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() { return level; }
}
