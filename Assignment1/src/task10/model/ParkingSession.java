package task10.model;

/** Model layer: private fields, validating setters. */
public class ParkingSession {
    private String regNo;           // private: only this class changes it
    private int floor;
    private int slot;
    private int durationMinutes;
    int internalAuditId;            // default (package-private): visible inside task10.model only
    protected double surcharge;     // protected: visible to subclasses and this package

    public ParkingSession(String regNo, int floor, int slot) {
        setRegNo(regNo); setFloor(floor); setSlot(slot);
    }

    public String getRegNo() { return regNo; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public int getDurationMinutes() { return durationMinutes; }

    public void setRegNo(String regNo) {
        if (regNo == null || regNo.isBlank()) throw new IllegalArgumentException("Registration number required");
        this.regNo = regNo.trim().toUpperCase();
    }
    public void setFloor(int floor) {
        if (floor < 0) throw new IllegalArgumentException("Invalid floor " + floor);
        this.floor = floor;
    }
    public void setSlot(int slot) {
        if (slot < 0) throw new IllegalArgumentException("Invalid slot " + slot);
        this.slot = slot;
    }
    public void setDurationMinutes(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Duration cannot be negative: " + minutes);
        this.durationMinutes = minutes;
    }
    @Override public String toString() { return regNo + "@" + floor + "," + slot + " (" + durationMinutes + " min)"; }
}
