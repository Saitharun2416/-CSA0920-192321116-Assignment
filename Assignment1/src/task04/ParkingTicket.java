package task04;

/** TASK 4 - Class with default, parameterised, overloaded, chained and copy constructors. */
public class ParkingTicket {
    private static int nextNo = 1;          // unique ticket numbers (see Task 5)

    private final int ticketNo;
    private final String regNo;
    private final int floor, slot;
    private final int entryMinute;

    /** Default constructor: an 'empty' placeholder ticket (used e.g. for display/testing). */
    public ParkingTicket() { this("UNKNOWN", -1, -1, 0); }

    /** Overloaded: vehicle only (slot not assigned yet). */
    public ParkingTicket(String regNo) { this(regNo, -1, -1, 0); }

    /** Overloaded: vehicle + slot, entry time defaults to 0. */
    public ParkingTicket(String regNo, int floor, int slot) { this(regNo, floor, slot, 0); }

    /** Master constructor - all others chain to it with this(...), so validation is written once. */
    public ParkingTicket(String regNo, int floor, int slot, int entryMinute) {
        if (regNo == null || regNo.isBlank()) throw new IllegalArgumentException("regNo required");
        if (entryMinute < 0) throw new IllegalArgumentException("entry time negative");
        this.ticketNo = nextNo++;
        this.regNo = regNo; this.floor = floor; this.slot = slot; this.entryMinute = entryMinute;
    }

    /** Copy constructor: a DUPLICATE ticket (lost-ticket re-issue) keeps the SAME ticket number. */
    public ParkingTicket(ParkingTicket other) {
        this.ticketNo = other.ticketNo;
        this.regNo = other.regNo; this.floor = other.floor; this.slot = other.slot;
        this.entryMinute = other.entryMinute;
    }

    public int getTicketNo() { return ticketNo; }
    public String getRegNo() { return regNo; }
    @Override public String toString() {
        return "Ticket#" + ticketNo + "[" + regNo + " @" + floor + "," + slot + " t=" + entryMinute + "]";
    }
}
