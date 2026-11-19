package task11;

/** TASK 11 - run this: normal, boundary and invalid tests + hand-calculated day total. */
public class Task11App {

    public static TestKit runAll() {
        TestKit t = new TestKit();
        t.header();

        // ---- normal operation ----
        ParkingLot lot = new ParkingLot(new int[]{2, 2, 1});
        t.check("capacity of {2,2,1}", 5, lot::capacity);
        ParkingTicket t1 = lot.enter(VehicleFactory.create("TwoWheeler", "TN01A1"), UserType.VISITOR, new NoPass(), 600);
        ParkingTicket t2 = lot.enter(VehicleFactory.create("Car", "TN02B2"), UserType.VISITOR, new NoPass(), 600);
        ParkingTicket t3 = lot.enter(VehicleFactory.create("EV", "TN03C3"), UserType.STUDENT, new MonthlyPass(0, 30), 600);
        t.check("slot of first vehicle", "0,0", () -> t1.getFloor() + "," + t1.getSlot());
        t.check("slot of third vehicle (floor 1)", "1,0", () -> t3.getFloor() + "," + t3.getSlot());
        t.check("ticket numbers increase", true, () -> t2.getTicketNo() == t1.getTicketNo() + 1);
        t.check("two-wheeler 130 min (3h x 10)", 30.0, () -> lot.exit("TN01A1", 730));
        t.check("car 130 min (3h x 30)", 90.0, () -> lot.exit("TN02B2", 730));
        t.check("student EV with monthly pass", 0.0, () -> lot.exit("TN03C3", 730));
        t.check("EV charging 10 kWh", 120.0, () -> new ElectricCar("X").chargingFee(10));
        t.check("HAND TOTAL (30+90+0) = collection", 120.0, lot::getCollection);

        // ---- boundary cases ----
        t.check("zero minutes -> Rs 0", 0.0, () -> new Car("B0").calculateFee(0));
        t.check("exactly 60 min -> 1 hour", 30.0, () -> new Car("B1").calculateFee(60));
        t.check("61 min -> 2 hours", 60.0, () -> new Car("B2").calculateFee(61));
        t.check("480 min (8h) -> cap reached", 240.0, () -> new Car("B3").calculateFee(480));
        t.check("481 min -> still capped", 240.0, () -> new Car("B4").calculateFee(481));
        t.check("1440 min (one full day)", 240.0, () -> new Car("B5").calculateFee(1440));
        t.check("1441 min (day + 1 hour)", 270.0, () -> new Car("B6").calculateFee(1441));
        ParkingLot small = new ParkingLot(new int[]{1, 1});
        small.enter(new Car("L1"), UserType.VISITOR, new NoPass(), 0);
        small.enter(new Car("L2"), UserType.VISITOR, new NoPass(), 0);   // last free slot
        t.check("lot full after last slot", 2, small::occupied);
        t.expectError("enter when full", "FULL", () -> small.enter(new Car("L3"), UserType.VISITOR, new NoPass(), 0));
        small.exit("L1", 10);
        t.check("slot reusable after exit", "0,0", () -> { ParkingTicket x = small.enter(new Car("L3"), UserType.VISITOR, new NoPass(), 20); return x.getFloor() + "," + x.getSlot(); });

        // ---- invalid cases ----
        t.expectError("unknown vehicle type", "Unknown vehicle type", () -> VehicleFactory.create("Helicopter", "H1"));
        t.expectError("duplicate registration", "Duplicate", () -> small.enter(new Car("L2"), UserType.VISITOR, new NoPass(), 5));
        t.expectError("exit unknown vehicle", "No active ticket", () -> small.exit("NOPE", 50));
        t.expectError("exit before entry", "Exit before entry", () -> small.exit("L3", 5));
        t.expectError("visitor with monthly pass", "not allowed", () -> new ParkingLot(new int[]{1}).enter(new Car("V1"), UserType.VISITOR, new MonthlyPass(0, 1), 0));
        t.expectError("invalid lot size", "needs > 0", () -> new ParkingLot(new int[]{3, 0}));
        t.expectError("blank registration", "required", () -> new Car(" "));
        t.check("equals: same regNo different subclass", true, () -> new Car("Q1").equals(new ElectricCar("q1")));
        return t;
    }

    public static void main(String[] args) {
        TestKit t = runAll();
        System.out.println("\nPassed " + t.passed() + ", failed " + t.failed());
    }
}
