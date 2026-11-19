package task12;

import task11.Vehicle;

/** NEW class (extension, nothing existing edited): flat fee Rs 20 per visit. */
public class EBike extends Vehicle {
    public static final double FLAT_FEE = 20;
    public EBike(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 0; }
    @Override public double calculateFee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Duration cannot be negative");
        return minutes == 0 ? 0 : FLAT_FEE;
    }
}
