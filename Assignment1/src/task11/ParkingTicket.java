package task11;

import java.util.concurrent.atomic.AtomicInteger;

public class ParkingTicket {
    private static final AtomicInteger COUNTER = new AtomicInteger(0);   // never reused
    private final int ticketNo = COUNTER.incrementAndGet();
    private final Vehicle vehicle; private final UserType user; private final Pass pass;
    private final int floor, slot, entryMinute;

    ParkingTicket(Vehicle v, UserType u, Pass p, int floor, int slot, int entryMinute) {
        vehicle = v; user = u; pass = p; this.floor = floor; this.slot = slot; this.entryMinute = entryMinute;
    }
    public int getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public UserType getUser() { return user; }
    public Pass getPass() { return pass; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public int getEntryMinute() { return entryMinute; }
    @Override public String toString() { return "Ticket#" + ticketNo + " " + vehicle + " slot(" + floor + "," + slot + ") in=" + entryMinute; }
}
