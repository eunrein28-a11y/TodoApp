/**
 * A due time: month (1-12), day (1-31), and a time of day. Earlier comes first.
 *
 * The time is stored internally in 24-hour form (hour 0-23) so comparing is simple.
 * You can create one two ways:
 *   new DueTime(11, 12, 18, 30)          // 24-hour: 6:30 PM
 *   new DueTime(11, 12, 6, 30, "PM")     // 12-hour with AM/PM: 6:30 PM
 * It always prints in AM/PM form.
 */
public class DueTime implements Comparable<DueTime> {
    private final int month, day, hour, minute; // hour is 0-23

    /** 24-hour constructor (hour 0-23). */
    public DueTime(int month, int day, int hour, int minute) {
        if (month < 1 || month > 12) throw new IllegalArgumentException("month must be 1-12");
        if (day < 1 || day > 31) throw new IllegalArgumentException("day must be 1-31");
        if (hour < 0 || hour > 23) throw new IllegalArgumentException("hour must be 0-23");
        if (minute < 0 || minute > 59) throw new IllegalArgumentException("minute must be 0-59");
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    /** 12-hour constructor: hour12 is 1-12 and amPm is "AM" or "PM". */
    public DueTime(int month, int day, int hour12, int minute, String amPm) {
        this(month, day, to24Hour(hour12, amPm), minute);
    }

    /** Converts 12-hour time to 24-hour: 12 AM -> 0, 1-11 AM -> 1-11, 12 PM -> 12, 1-11 PM -> 13-23. */
    private static int to24Hour(int hour12, String amPm) {
        if (hour12 < 1 || hour12 > 12) throw new IllegalArgumentException("hour must be 1-12");
        if (amPm == null) throw new IllegalArgumentException("choose AM or PM");
        String s = amPm.trim().toUpperCase();
        if (!s.equals("AM") && !s.equals("PM")) throw new IllegalArgumentException("choose AM or PM");
        int h = hour12 % 12;            // 12 becomes 0
        return s.equals("PM") ? h + 12 : h;
    }

    public int getMonth()  { return month; }
    public int getDay()    { return day; }
    public int getHour()   { return hour; }   // 0-23
    public int getMinute() { return minute; }

    /**
     * TODO: Compare this due time to other, earlier = smaller.
     * Check month first, then day, then hour, then minute (just like the Date example in the video).
     * The hour is stored as 0-23, so 11:59 PM comes after 8:00 AM with no extra work.
     * Return -1 / 0 / +1 (or any negative / 0 / positive).
     * Throw NullPointerException if other is null.
     */
    @Override
    public int compareTo(DueTime other) {
        if(this.getMonth() > other.getMonth()){
            return 1;
        }
        if(this.getMonth() < other.getMonth()){
            return -1;
        }
        if(this.getDay() > other.getDay()){
            return 1;
        }
        if(this.getDay() < other.getDay()){
            return -1;
        }
        if(this.getHour() > other.getHour()){
            return 1;
        }
        if(this.getHour() < other.getHour()){
            return -1;
        }
        if(this.getMinute() > other.getMinute()){
            return 1;
        }
        if(this.getMinute() < other.getMinute()){
            return -1;
        }
        return 0;
    }

    /** Prints like 11/12 6:30 PM. */
    @Override
    public String toString() {
        int h12 = hour % 12 == 0 ? 12 : hour % 12;
        String suffix = hour < 12 ? "AM" : "PM";
        return String.format("%02d/%02d %d:%02d %s", month, day, h12, minute, suffix);
    }
}
