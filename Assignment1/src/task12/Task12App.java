package task12;

import task11.*;

/** TASK 12 - run this: new categories, one-day pass, 5 floors with different slot counts, regression check. */
public class Task12App {
    public static void main(String[] args) {
        // Only change needed to support new types: REGISTER them (VehicleFactory is not edited).
        VehicleFactory.register("EBIKE", EBike::new);
        VehicleFactory.register("VAN", DeliveryVan::new);

        System.out.println("=== Regression: all Task 11 tests re-run against the unchanged classes ===");
        TestKit old = Task11App.runAll();
        System.out.println("Task 11 suite -> passed " + old.passed() + ", failed " + old.failed() + "\n");

        System.out.println("=== New requirements ===");
        TestKit t = new TestKit(); t.header();
        ParkingLot lot = new ParkingLot(new int[]{50, 40, 30, 60, 20});     // 5 floors, different sizes (jagged)
        t.check("capacity of 5 uneven floors", 200, lot::capacity);

        lot.enter(VehicleFactory.create("VAN", "VAN1"), UserType.VISITOR, new NoPass(), 510);          // 08:30
        lot.enter(VehicleFactory.create("EBIKE", "EB1"), UserType.VISITOR, new NoPass(), 600);
        lot.enter(VehicleFactory.create("VAN", "VAN2"), UserType.VISITOR, new OneDayPass(0), 600);     // 10:00
        t.check("van 08:30 + 130 min (30+60+60)", 150.0, () -> lot.exit("VAN1", 510 + 130));
        t.check("e-bike flat fee", 20.0, () -> lot.exit("EB1", 600 + 300));
        t.check("van with one-day pass is free", 0.0, () -> lot.exit("VAN2", 720));
        t.check("HAND TOTAL 150+20+0+50(pass sale)", 220.0, lot::getCollection);

        t.check("van fully off-peak 20:00 for 120 min", 60.0, () -> new DeliveryVan("V").calculateFee(1200, 120));
        t.check("van fully peak 10:00 for 60 min", 60.0, () -> new DeliveryVan("V").calculateFee(600, 60));
        t.check("van boundary: 16:30 for 90 min (peak 60 + off 30)", 90.0, () -> new DeliveryVan("V").calculateFee(990, 90));
        t.check("e-bike zero minutes", 0.0, () -> new EBike("E").calculateFee(0));
        t.check("one-day pass spanning midnight not free", false, () -> new OneDayPass(0).covers(1400, 1500));
        t.expectError("one-day pass for staff rejected", "not allowed", () -> lot.enter(new Car("S1"), UserType.STAFF, new OneDayPass(0), 0));
        t.expectError("monthly pass for visitor rejected", "not allowed", () -> lot.enter(new Car("S2"), UserType.VISITOR, new MonthlyPass(0, 9), 0));
        t.expectError("unknown type still rejected", "Unknown vehicle type", () -> VehicleFactory.create("TRUCK", "T1"));

        Vehicle[] mixed = { new TwoWheeler("A"), new Car("B"), new EBike("C"), new DeliveryVan("D") };
        double sum = 0;
        for (Vehicle v : mixed) sum += v.calculateFee(600, 130);        // one loop, runtime polymorphism, no instanceof
        final double s = sum;
        t.check("polymorphic loop 10:00,130min (30+90+20+180)", 320.0, () -> s);

        System.out.println("\nPassed " + t.passed() + ", failed " + t.failed());
        System.out.println("\nDESIGN EVIDENCE: existing classes modified = 0 | new classes = 3 (EBike, DeliveryVan, OneDayPass)"
                + " | registrations = 2 | adding another vehicle type later = 1 new class + 1 register() call.");
    }
}
