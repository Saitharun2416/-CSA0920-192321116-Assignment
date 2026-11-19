package task03;

/** TASK 3 - Fee slabs with if-else / switch, break/continue/return. */
public class Task03Fee {
    /** first hour Rs base, each extra hour Rs extra, daily maximum Rs cap. Zero minutes -> 0. */
    static double fee(String type, int minutes) {
        if (minutes <= 0) return 0;                       // return early: nothing to compute
        double first, extra, cap;
        switch (type) {
            case "TWO_WHEELER": first = 10; extra = 5;  cap = 50;  break;
            case "CAR":         first = 30; extra = 20; cap = 150; break;
            case "EV":          first = 25; extra = 15; cap = 120; break;
            default: throw new IllegalArgumentException("Unknown type " + type);
        }
        int hours = (minutes + 59) / 60;                  // every started hour
        double total = first;
        for (int h = 2; h <= hours; h++) {
            total += extra;
            if (total >= cap) { total = cap; break; }     // break: cap reached, stop looping
        }
        return Math.min(total, cap);
    }

    public static void main(String[] args) {
        int[] mins = {0, 30, 60, 61, 130, 600};
        for (String t : new String[]{"TWO_WHEELER", "CAR", "EV"})
            for (int m : mins) System.out.println(t + " " + m + " min -> Rs " + fee(t, m));
        try { fee("TRUCK", 10); } catch (IllegalArgumentException e) { System.out.println("Rejected: " + e.getMessage()); }
        System.out.println("When no slot is free: allocate() returns null; the caller shows 'FULL' and issues no ticket.");
    }
}
