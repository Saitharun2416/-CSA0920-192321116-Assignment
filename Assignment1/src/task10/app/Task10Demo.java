package task10.app;

import task10.model.ParkingSession;
import task10.service.SessionService;

/** Application layer: run this. Shows validation, access modifiers, wrappers/autoboxing. */
public class Task10Demo {

    /** Alternative: public fields + validation in the caller (what can go wrong). */
    static class PublicFieldSession { public int durationMinutes; }

    public static void main(String[] args) {
        SessionService svc = new SessionService();
        ParkingSession s = new ParkingSession(" tn01ab1111 ", 1, 7);
        s.setDurationMinutes(130);
        svc.start(s);
        System.out.println(svc.find("TN01AB1111") + " fee = " + svc.fee(s, 30));

        try { s.setDurationMinutes(-5); } catch (IllegalArgumentException e) { System.out.println("Rejected: " + e.getMessage()); }
        try { s.setSlot(-1); }            catch (IllegalArgumentException e) { System.out.println("Rejected: " + e.getMessage()); }
        // s.regNo = "X";            // compile error: private
        // s.internalAuditId = 5;    // compile error: package-private, different package

        PublicFieldSession p = new PublicFieldSession();
        p.durationMinutes = -999;    // nothing stops this; every caller must remember to check
        System.out.println("Public-field design accepted invalid duration: " + p.durationMinutes);

        // Wrapper classes + autoboxing
        Integer a = 127, b = 127, c = 1000, d = 1000;          // autoboxing int -> Integer
        System.out.println("127 == 127  : " + (a == b) + "   (cached -128..127)");
        System.out.println("1000 == 1000: " + (c == d) + "  (different objects!)  equals: " + c.equals(d));
        Integer maybeNull = null;
        try { int x = maybeNull; } catch (NullPointerException e) { System.out.println("Auto-unboxing null -> NullPointerException"); }
        int parsed = Integer.parseInt("42");
        System.out.println("parseInt -> " + parsed);

        System.out.println("Validation cost: O(1) per update; with public fields the same check is repeated at every call site (k places).");
    }
}
