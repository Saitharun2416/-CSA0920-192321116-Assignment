package task04;

/** TASK 4 - Demo + Alternative concept (setters / static factory) with cost measurement. */
public class Task04Demo {

    /** Alternative 1: default constructor + setters (object can exist in an INVALID state). */
    static class SetterTicket {
        int ticketNo; String regNo; int floor, slot, entryMinute;
        void setRegNo(String r) { regNo = r; }
    }

    /** Alternative 2: static factory methods (named, validated, can return cached/shared objects). */
    static class FactoryTicket {
        private static int next = 1;
        final int ticketNo; final String regNo; final int floor, slot;
        private FactoryTicket(String r, int f, int s) { ticketNo = next++; regNo = r; floor = f; slot = s; }
        static FactoryTicket forSlot(String r, int f, int s) {
            if (r == null || r.isBlank()) throw new IllegalArgumentException("regNo required");
            return new FactoryTicket(r, f, s);
        }
    }

    public static void main(String[] args) {
        ParkingTicket a = new ParkingTicket();
        ParkingTicket b = new ParkingTicket("TN01AB1111");
        ParkingTicket c = new ParkingTicket("TN02CD2222", 1, 5);
        ParkingTicket d = new ParkingTicket("TN03EF3333", 2, 7, 540);
        ParkingTicket dup = new ParkingTicket(d);          // copy constructor
        System.out.println(a + "\n" + b + "\n" + c + "\n" + d + "\n" + dup);
        System.out.println("d == dup (same object)? " + (d == dup) + "; same ticketNo? " + (d.getTicketNo() == dup.getTicketNo()));

        // Memory: references a,b,c,d,dup live on the STACK; the ParkingTicket objects live on the HEAP.
        ParkingTicket alias = d;                            // second reference, same heap object
        System.out.println("alias == d : " + (alias == d));

        try { new ParkingTicket(""); } catch (IllegalArgumentException e) { System.out.println("Constructor rejected: " + e.getMessage()); }
        SetterTicket bad = new SetterTicket();             // never forced to set regNo -> invalid object possible
        System.out.println("Setter approach allows invalid object, regNo = " + bad.regNo);

        int n = 1_000_000;
        for (int warm = 0; warm < 2; warm++) run(n);
        long t0 = System.nanoTime(); ParkingTicket[] x = new ParkingTicket[n];
        for (int i = 0; i < n; i++) x[i] = new ParkingTicket("V" + i, 0, i, i);
        long t1 = System.nanoTime(); SetterTicket[] y = new SetterTicket[n];
        for (int i = 0; i < n; i++) { SetterTicket s = new SetterTicket(); s.setRegNo("V" + i); s.slot = i; y[i] = s; }
        long t2 = System.nanoTime(); FactoryTicket[] z = new FactoryTicket[n];
        for (int i = 0; i < n; i++) z[i] = FactoryTicket.forSlot("V" + i, 0, i);
        long t3 = System.nanoTime();
        System.out.printf("Create %,d tickets: constructors %d ms, setters %d ms, factory %d ms (all O(n) time, O(n) space)%n",
                n, (t1 - t0) / 1_000_000, (t2 - t1) / 1_000_000, (t3 - t2) / 1_000_000);
    }
    static void run(int n) { for (int i = 0; i < n; i++) new ParkingTicket("W" + i, 0, i, i); }
}
