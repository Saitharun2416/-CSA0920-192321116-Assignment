package task11;

/** Valid for staff and students only, between two day numbers (inclusive). */
public class MonthlyPass implements Pass {
    private final int fromDay, toDay;
    public MonthlyPass(int fromDay, int toDay) {
        if (toDay < fromDay) throw new IllegalArgumentException("pass end before start");
        this.fromDay = fromDay; this.toDay = toDay;
    }
    @Override public boolean covers(int entryMinute, int exitMinute) {
        return entryMinute / 1440 >= fromDay && exitMinute / 1440 <= toDay;
    }
    @Override public boolean allowedFor(UserType user) { return user != UserType.VISITOR; }
}
