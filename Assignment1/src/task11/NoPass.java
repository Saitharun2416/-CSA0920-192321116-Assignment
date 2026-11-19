package task11;
public class NoPass implements Pass {
    @Override public boolean covers(int entryMinute, int exitMinute) { return false; }
    @Override public boolean allowedFor(UserType user) { return true; }
}
