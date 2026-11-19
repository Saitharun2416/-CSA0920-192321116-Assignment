package task07;

import java.util.HashSet;
import java.util.Set;

/** TASK 7 - overriding, final, toString/equals/hashCode, and the switch-on-type alternative. */
public class Task07Demo {

    static abstract class Vehicle {
        private final String regNo;                         // final variable: identity never changes
        protected static final int MIN_PER_HOUR = 60;       // final constant
        Vehicle(String regNo) { this.regNo = regNo; }
        String getRegNo() { return regNo; }
        protected abstract double ratePerHour();
        /** final method: billing rule that no subclass may change. */
        public final int billableHours(int minutes) { return (minutes + MIN_PER_HOUR - 1) / MIN_PER_HOUR; }
        public double calculateFee(int minutes) { return billableHours(minutes) * ratePerHour(); }
        @Override public String toString() { return getClass().getSimpleName() + "[" + regNo + "]"; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Vehicle)) return false;
            return regNo.equals(((Vehicle) o).regNo);
        }
        @Override public int hashCode() { return regNo.hashCode(); }   // consistent with equals
    }
    static class TwoWheeler extends Vehicle {
        TwoWheeler(String r) { super(r); }
        @Override protected double ratePerHour() { return 10; }
    }
    static class Car extends Vehicle {
        Car(String r) { super(r); }
        @Override protected double ratePerHour() { return 30; }
    }
    /** final class: cannot be extended any further. */
    static final class ElectricCar extends Car {
        ElectricCar(String r) { super(r); }
        // @Override with weaker access (private) or a different return type would NOT compile
        @Override protected double ratePerHour() { return 35; }
    }

    // ---------------- Alternative: one method with switch on a type field ----------------
    static class TypedVehicle {
        final String regNo; final String type;
        TypedVehicle(String r, String t) { regNo = r; type = t; }
        double calculateFee(int minutes) {
            int h = (minutes + 59) / 60;
            switch (type) {                                  // every new type edits THIS method
                case "TWO": return h * 10;
                case "CAR": return h * 30;
                case "EV":  return h * 35;
                default: throw new IllegalArgumentException("unknown type " + type);
            }
        }
    }

    public static void main(String[] args) {
        Vehicle[] vs = { new TwoWheeler("TN01"), new Car("TN02"), new ElectricCar("TN03") };
        for (Vehicle v : vs) System.out.println(v + " -> Rs " + v.calculateFee(130));

        Vehicle a = new Car("TN99"), b = new Car("TN99");
        System.out.println("a == b      : " + (a == b));
        System.out.println("a.equals(b) : " + a.equals(b));
        Set<Vehicle> set = new HashSet<>(); set.add(a); set.add(b);
        System.out.println("HashSet size (should be 1): " + set.size());

        System.out.println("Switch version: " + new TypedVehicle("TN03", "EV").calculateFee(130));
        System.out.println("Adding a type: overriding -> 1 NEW class, 0 changed.  switch -> 1 changed method (and risk of regression).");
        System.out.println("Time per fee: overriding O(1) (vtable); switch on String O(1) hash/switch, or O(k) with if-else chain.");
    }
}
