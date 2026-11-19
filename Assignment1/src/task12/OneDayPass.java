package task12;

import task11.Pass;
import task11.UserType;

/** NEW class: visitors buy a one-day pass (Rs 50); stays inside that calendar day are free. */
public class OneDayPass implements Pass {
    private final int day;
    public OneDayPass(int day) { this.day = day; }
    @Override public boolean covers(int entryMinute, int exitMinute) {
        return entryMinute / 1440 == day && exitMinute / 1440 == day;
    }
    @Override public boolean allowedFor(UserType user) { return user == UserType.VISITOR; }
    @Override public double price() { return 50; }
}
