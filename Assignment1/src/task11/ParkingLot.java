package task11;

import java.util.HashMap;
import java.util.Map;

/** The facility. Jagged array of tickets (null = free) + HashMap for O(1) exit lookup. */
public class ParkingLot {
    private final ParkingTicket[][] slots;
    private final Map<String, ParkingTicket> active = new HashMap<>();
    private double collection;

    public ParkingLot(int[] slotsPerFloor) {
        if (slotsPerFloor == null || slotsPerFloor.length == 0) throw new IllegalArgumentException("need at least one floor");
        slots = new ParkingTicket[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            if (slotsPerFloor[f] <= 0) throw new IllegalArgumentException("floor " + f + " needs > 0 slots");
            slots[f] = new ParkingTicket[slotsPerFloor[f]];
        }
    }

    public ParkingTicket enter(Vehicle v, UserType user, Pass pass, int entryMinute) {
        if (active.containsKey(v.getRegNo())) throw new ParkingException("Duplicate registration: " + v.getRegNo());
        if (!pass.allowedFor(user)) throw new ParkingException(pass.label() + " not allowed for " + user);
        for (int f = 0; f < slots.length; f++)
            for (int s = 0; s < slots[f].length; s++)
                if (slots[f][s] == null) {
                    ParkingTicket t = new ParkingTicket(v, user, pass, f, s, entryMinute);
                    slots[f][s] = t;
                    active.put(v.getRegNo(), t);
                    collection += pass.price();
                    return t;
                }
        throw new ParkingException("FULL");
    }

    /** Returns the parking fee (charging is billed separately by the caller). */
    public double exit(String regNo, int exitMinute) {
        ParkingTicket t = active.get(regNo == null ? null : regNo.trim().toUpperCase());
        if (t == null) throw new ParkingException("No active ticket for " + regNo);
        if (exitMinute < t.getEntryMinute()) throw new ParkingException("Exit before entry");
        int minutes = exitMinute - t.getEntryMinute();
        double fee = t.getPass().covers(t.getEntryMinute(), exitMinute) ? 0
                : t.getVehicle().calculateFee(t.getEntryMinute(), minutes);   // dynamic dispatch
        slots[t.getFloor()][t.getSlot()] = null;
        active.remove(t.getVehicle().getRegNo());
        collection += fee;
        return fee;
    }

    public int capacity() { int c = 0; for (ParkingTicket[] f : slots) c += f.length; return c; }
    public int occupied() { return active.size(); }
    public double getCollection() { return collection; }
}
