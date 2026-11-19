package task11;

public abstract class Vehicle {
    private final String regNo;
    protected Vehicle(String regNo) {
        if (regNo == null || regNo.isBlank()) throw new IllegalArgumentException("Registration number required");
        this.regNo = regNo.trim().toUpperCase();
    }
    public String getRegNo() { return regNo; }
    protected abstract double ratePerHour();
    protected int dailyCapHours() { return 8; }

    /** Every started hour is charged; a day (1440 min) costs at most dailyCapHours * rate. Zero minutes = Rs 0. */
    public double calculateFee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Duration cannot be negative");
        int days = minutes / 1440;
        int rem = minutes % 1440;
        int hours = (rem + 59) / 60;                 // integer ceiling - no floating point
        return days * dailyCapHours() * ratePerHour() + Math.min(hours, dailyCapHours()) * ratePerHour();
    }
    /** Compile-time overload: time-aware pricing hook. Default ignores the clock time. */
    public double calculateFee(int entryMinute, int minutes) { return calculateFee(minutes); }

    @Override public boolean equals(Object o) { return o instanceof Vehicle v && regNo.equals(v.regNo); }
    @Override public int hashCode() { return regNo.hashCode(); }
    @Override public String toString() { return getClass().getSimpleName() + "[" + regNo + "]"; }
}
