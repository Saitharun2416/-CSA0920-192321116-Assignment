package task08;

import java.time.Duration;
import java.time.LocalTime;

/**
 * TASK 8 - compile-time polymorphism (overloading), upcasting, dispatch trace and the instanceof alternative.
 * Uses the Vehicle classes from FeeDemo.java (same package).
 */
public class Task08Extras {

    // Overloaded methods: the COMPILER chooses by the argument types (compile-time polymorphism)
    static double calculateFee(Vehicle v, int minutes) {
        System.out.println("  [overload chosen: (Vehicle,int)]");
        return v.calculateFee(minutes);
    }
    static double calculateFee(Vehicle v, LocalTime in, LocalTime out) {
        System.out.println("  [overload chosen: (Vehicle,LocalTime,LocalTime)]");
        int minutes = (int) Duration.between(in, out).toMinutes();
        return v.calculateFee(minutes);
    }

    /** Alternative: instanceof chain - must be edited for every new type; up to k checks. */
    static double feeByInstanceof(Vehicle v, int minutes) {
        int h = (minutes + 59) / 60;
        if (v instanceof ElectricCar) return h * 30;
        else if (v instanceof Car) return h * 30;
        else if (v instanceof TwoWheeler) return h * 10;
        throw new IllegalArgumentException("unknown vehicle");
    }

    public static void main(String[] args) {
        Vehicle v = new ElectricCar("TN03C3");     // upcasting: declared type Vehicle, actual type ElectricCar
        System.out.println("Declared type : Vehicle");
        System.out.println("Actual type   : " + v.getClass().getSimpleName());
        System.out.println("Executed      : Vehicle.calculateFee(int) calling Car.ratePerHour() (inherited by ElectricCar), chosen at RUNTIME");
        System.out.println("fee(int)      = " + calculateFee(v, 130));
        System.out.println("fee(in,out)   = " + calculateFee(v, LocalTime.of(10, 0), LocalTime.of(12, 10)));
        System.out.println("instanceof    = " + feeByInstanceof(v, 130));

        Vehicle[] fleet = { new TwoWheeler("A"), new Car("B"), new ElectricCar("C") };
        long t0 = System.nanoTime(); double s = 0;
        for (int i = 0; i < 30_000_000; i++) s += fleet[i % 3].calculateFee(130);
        long a = System.nanoTime() - t0; t0 = System.nanoTime();
        for (int i = 0; i < 30_000_000; i++) s += feeByInstanceof(fleet[i % 3], 130);
        long b = System.nanoTime() - t0;
        System.out.printf("30M calls: dispatch %d ms, instanceof chain %d ms (sum %.0f)%n", a / 1_000_000, b / 1_000_000, s);

        System.out.println("equals(): Car(TN02B2).equals(ElectricCar(TN02B2)) is true because equals compares only regNo via instanceof Vehicle.");
        System.out.println("That is correct here: a registration number identifies ONE physical vehicle, whatever subclass it was recorded as.");
    }
}
