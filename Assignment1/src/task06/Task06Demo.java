package task06;

/** TASK 6 - Inheritance hierarchy, super, constructor order trace, composition alternative. */
public class Task06Demo {

    // ---------- hierarchy: Vehicle -> Car -> ElectricCar (multilevel), Vehicle -> TwoWheeler (hierarchical) ----------
    static class Vehicle {
        protected final String regNo;
        Vehicle(String regNo) { this.regNo = regNo; System.out.println("  1. Vehicle(" + regNo + ") constructor"); }
        double ratePerHour() { return 0; }
        double fee(int minutes) { return ((minutes + 59) / 60) * ratePerHour(); }
        String describe() { return "Vehicle " + regNo; }
    }
    static class TwoWheeler extends Vehicle {
        TwoWheeler(String r) { super(r); System.out.println("  2. TwoWheeler constructor"); }
        @Override double ratePerHour() { return 10; }
    }
    static class Car extends Vehicle {
        protected int seats;
        Car(String r) { super(r); this.seats = 4; System.out.println("  2. Car constructor (seats=4)"); }
        @Override double ratePerHour() { return 30; }
        @Override String describe() { return "Car " + regNo + " seats=" + seats; }
    }
    static class ElectricCar extends Car {
        private final int batteryKWh;
        ElectricCar(String r, int battery) {
            super(r);                                  // super(...) MUST be first: runs Vehicle -> Car first
            this.batteryKWh = battery;
            System.out.println("  3. ElectricCar constructor (battery=" + battery + ")");
        }
        @Override String describe() { return super.describe() + " EV battery=" + batteryKWh + "kWh"; }  // super.method()
    }

    // ---------- user hierarchy ----------
    static class User { final String name; User(String n) { name = n; } boolean parksFree() { return false; } }
    static class Student extends User { Student(String n) { super(n); } @Override boolean parksFree() { return true; } }
    static class Staff extends User { Staff(String n) { super(n); } @Override boolean parksFree() { return true; } }
    static class Visitor extends User { Visitor(String n) { super(n); } }

    // ---------- Alternative: composition (Vehicle HAS-A VehicleType) ----------
    static class VehicleType {
        final String name; final double rate;
        VehicleType(String n, double r) { name = n; rate = r; }
    }
    static class VehicleC {
        final String regNo; final VehicleType type;
        VehicleC(String r, VehicleType t) { regNo = r; type = t; }
        double fee(int minutes) { return ((minutes + 59) / 60) * type.rate; }
    }

    public static void main(String[] args) {
        System.out.println("Creating ElectricCar - constructor execution order:");
        ElectricCar ev = new ElectricCar("TN03C3", 40);
        System.out.println(ev.describe());
        System.out.println("EV fee 130 min = " + ev.fee(130));
        Student st = new Student("Arun"); Visitor vi = new Visitor("Guest");
        System.out.println("Student free? " + st.parksFree() + " | Visitor free? " + vi.parksFree());

        VehicleType carType = new VehicleType("CAR", 30);
        VehicleC c = new VehicleC("TN02B2", carType);
        System.out.println("Composition car fee 130 min = " + c.fee(130));
        System.out.println("New type with inheritance: write a new subclass.  With composition: new VehicleType(\"VAN\",60) - no new class.");

        long t0 = System.nanoTime(); double sum = 0;
        for (int i = 0; i < 20_000_000; i++) sum += ev.fee(130);
        long a = System.nanoTime() - t0; t0 = System.nanoTime();
        for (int i = 0; i < 20_000_000; i++) sum += c.fee(130);
        long b = System.nanoTime() - t0;
        System.out.printf("20M fee calls: inheritance %d ms, composition %d ms (both O(1)); sum=%.0f%n", a / 1_000_000, b / 1_000_000, sum);
    }
}
