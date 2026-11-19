package task11;

/** A pass decides whether a stay is free. Times are absolute minutes; day = minute / 1440. */
public interface Pass {
    boolean covers(int entryMinute, int exitMinute);
    boolean allowedFor(UserType user);
    default double price() { return 0; }
    default String label() { return getClass().getSimpleName(); }
}
