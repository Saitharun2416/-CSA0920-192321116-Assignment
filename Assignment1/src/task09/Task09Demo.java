package task09;

/** TASK 9 - abstract class vs interface, multiple interfaces, default and static interface methods. */
public class Task09Demo {

    interface Chargeable {
        double RATE_PER_KWH = 12.0;                       // constant
        double chargingFee(int kWh);
        default double chargingFeeWithTax(int kWh) { return chargingFee(kWh) * 1.18; }   // default method
        static boolean validKWh(int kWh) { return kWh > 0 && kWh <= 100; }                // static method
    }
    interface Payable {
        double amountDue();
        default String receipt() { return "Receipt: Rs " + amountDue(); }
    }

    /** abstract class: shared STATE (regNo), constructor and a template method + one abstract hook. */
    static abstract class Vehicle {
        private final String regNo;
        protected Vehicle(String r) { regNo = r; }
        String getRegNo() { return regNo; }
        protected abstract double ratePerHour();
        double calculateFee(int minutes) { return ((minutes + 59) / 60) * ratePerHour(); }
    }

    /** Multiple behaviours via interfaces: one superclass, many interfaces. */
    static class ElectricCar extends Vehicle implements Chargeable, Payable {
        private double due;
        ElectricCar(String r) { super(r); }
        @Override protected double ratePerHour() { return 30; }
        @Override public double chargingFee(int kWh) { return kWh * RATE_PER_KWH; }
        void bill(int minutes, int kWh) { due = calculateFee(minutes) + chargingFee(kWh); }
        @Override public double amountDue() { return due; }
    }

    public static void main(String[] args) {
        // Vehicle v = new Vehicle("X");   // does not compile: abstract class cannot be instantiated
        ElectricCar ev = new ElectricCar("TN03C3");
        ev.bill(130, 10);
        System.out.println(ev.receipt());
        System.out.println("Charging with tax : " + ev.chargingFeeWithTax(10));
        System.out.println("Valid 150 kWh?    : " + Chargeable.validKWh(150));
        System.out.println();
        System.out.println("Why abstract class for Vehicle: needs a field (regNo), a constructor, shared code, IS-A relation.");
        System.out.println("Why interface for Chargeable/Payable: a capability unrelated to the family tree; a class can implement many.");
        System.out.println();
        System.out.println("Alternative (Vehicle = interface, Chargeable = abstract class):");
        System.out.println(" - interface cannot hold instance state or a constructor -> regNo must be re-declared in every implementer");
        System.out.println(" - ElectricCar could not 'extend Car' AND 'extend Chargeable' (single class inheritance)");
        System.out.println(" - call cost: invokeinterface vs invokevirtual are both O(1) in practice; object space same (no per-object cost for the interface itself)");
    }
}
