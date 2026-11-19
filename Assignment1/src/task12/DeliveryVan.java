package task12;

import task11.Vehicle;

/** NEW class: double the car rate (Rs 60/h instead of 30) for hours that start between 09:00 and 17:00. */
public class DeliveryVan extends Vehicle {
    private static final double CAR_RATE = 30;
    public DeliveryVan(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return CAR_RATE; }

    /** Uses the time-aware overload that the original Vehicle already provides. */
    @Override public double calculateFee(int entryMinute, int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Duration cannot be negative");
        int hours = (minutes + 59) / 60;
        double total = 0;
        for (int h = 0; h < hours; h++) {
            int hourOfDay = ((entryMinute + h * 60) % 1440) / 60;
            total += (hourOfDay >= 9 && hourOfDay < 17) ? 2 * CAR_RATE : CAR_RATE;
        }
        return total;                       // (no daily cap for vans - stated assumption)
    }
}
