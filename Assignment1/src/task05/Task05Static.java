package task05;

import java.lang.ref.WeakReference;

/** TASK 5 - static members, static block, object life cycle, instance-counter pitfall. */
public class Task05Static {

    static class Ticket {
        private static int counter;                 // shared by ALL tickets and facilities
        private static int totalOccupancy;
        static final int MAX_FLOORS;
        static { MAX_FLOORS = 5; counter = 1000; System.out.println("[static block] Ticket class loaded"); }
        final int no; final String reg;
        Ticket(String reg) { this.no = ++counter; this.reg = reg; totalOccupancy++; }
        static int getTotalOccupancy() { return totalOccupancy; }      // static method
        void leave() { totalOccupancy--; }
        public String toString() { return "T" + no + ":" + reg; }
    }

    /** WRONG design: the counter is an instance variable of each facility. */
    static class FacilityInstanceCounter {
        private int counter = 1000;
        String issue(String reg) { return "T" + (++counter) + ":" + reg; }
    }

    /** Facility that uses the shared static counter. */
    static class Facility {
        final String name; Facility(String n) { name = n; }
        Ticket issue(String reg) { return new Ticket(reg); }
    }

    /** Alternative 2: a per-facility prefix keeps numbers unique even with instance counters. */
    static class FacilityPrefixed {
        private final String prefix; private int counter = 0;
        FacilityPrefixed(String prefix) { this.prefix = prefix; }
        String issue(String reg) { return prefix + "-" + (++counter) + ":" + reg; }
    }

    static Ticket passAndReturn(Ticket t) { return t; }   // references are passed by value; same object returned

    public static void main(String[] args) {
        System.out.println("Max floors (static final) = " + Ticket.MAX_FLOORS);
        FacilityInstanceCounter north = new FacilityInstanceCounter(), south = new FacilityInstanceCounter();
        System.out.println("INSTANCE counter -> north " + north.issue("A") + ", south " + south.issue("B") + "  <-- duplicate T1001!");

        Facility n = new Facility("north"), s = new Facility("south");
        Ticket t1 = n.issue("A"), t2 = s.issue("B"), t3 = n.issue("C");
        System.out.println("STATIC counter   -> " + t1 + ", " + t2 + ", " + t3 + "  (unique across facilities)");
        System.out.println("Occupancy = " + Ticket.getTotalOccupancy());

        FacilityPrefixed pn = new FacilityPrefixed("N"), ps = new FacilityPrefixed("S");
        System.out.println("PREFIX per facility -> " + pn.issue("A") + ", " + ps.issue("B"));

        System.out.println("passAndReturn gives same object: " + (passAndReturn(t1) == t1));

        // life cycle: after the vehicle leaves the ticket is unreachable -> eligible for GC
        Ticket temp = new Ticket("TEMP");
        WeakReference<Ticket> weak = new WeakReference<>(temp);
        temp.leave();
        temp = null;                                  // no strong reference left
        System.gc();
        try { Thread.sleep(100); } catch (InterruptedException ignored) { }
        System.out.println("Ticket collected after exit? " + (weak.get() == null) + " (GC timing is not guaranteed)");

        System.out.println("Space: static counter = O(1) total. Instance counter = O(k) for k facilities. Tickets are O(n) in both.");
    }
}
