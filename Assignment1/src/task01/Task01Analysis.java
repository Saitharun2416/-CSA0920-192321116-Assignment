package task01;

import java.util.HashMap;
import java.util.Map;

/**
 * TASK 1 - Problem analysis + Alternative concept (objects vs parallel arrays).
 * Run this class. It prints the analysis summary and measures search cost.
 */
public class Task01Analysis {

    /** Object design: one Vehicle object per parked vehicle. */
    static class VehicleRecord {
        final String regNo; final String type; final long entryTime;
        VehicleRecord(String regNo, String type, long entryTime) {
            this.regNo = regNo; this.type = type; this.entryTime = entryTime;
        }
    }

    static void printAnalysis() {
        System.out.println("=== CANDIDATE CLASSES (nouns) AND RESPONSIBILITIES ===");
        System.out.println("Vehicle (abstract) : holds regNo; knows its hourly rate; computes fee");
        System.out.println("TwoWheeler/Car/EV  : specialise the rate / charging capability");
        System.out.println("ParkingSlot/Floor  : hold position; know whether free");
        System.out.println("ParkingFacility    : owns floors; allocates and releases slots");
        System.out.println("ParkingTicket      : unique ticket no, vehicle, slot, entry time");
        System.out.println("Pass               : monthly pass (free parking for staff/students)");
        System.out.println("User (Student/Staff/Visitor): decides who pays");
        System.out.println("\n=== VERBS -> METHODS ===");
        System.out.println("enter -> allocate(), issueTicket(); leave -> release(), calculateFee(); charge -> chargingFee()");
        System.out.println("\n=== BUSINESS RULES ===");
        System.out.println("1. A slot cannot hold two vehicles.  2. A ticket number is never reused.");
        System.out.println("3. A vehicle cannot be parked twice at the same time.  4. Exit time >= entry time.");
        System.out.println("5. Only staff/students with a valid monthly pass park free.");
        System.out.println("\n=== ASSUMPTIONS ===");
        System.out.println("Time is in whole minutes; every started hour is charged; first free slot is allocated;");
        System.out.println("charging is billed separately per kWh; one facility = fixed slots per floor.\n");
    }

    public static void main(String[] args) {
        printAnalysis();

        int n = 200_000;
        // ---- Alternative: parallel arrays ----
        String[] regNo = new String[n];
        String[] type = new String[n];
        long[] entry = new long[n];
        // ---- Object design ----
        VehicleRecord[] objs = new VehicleRecord[n];
        Map<String, VehicleRecord> index = new HashMap<>();
        for (int i = 0; i < n; i++) {
            regNo[i] = "TN" + i; type[i] = (i % 3 == 0) ? "CAR" : "BIKE"; entry[i] = i;
            objs[i] = new VehicleRecord(regNo[i], type[i], i);
            index.put(regNo[i], objs[i]);
        }
        String target = "TN" + (n - 1);   // worst case: last element
        int reps = 200;

        long t0 = System.nanoTime(); int found = -1;
        for (int r = 0; r < reps; r++)
            for (int i = 0; i < n; i++) if (regNo[i].equals(target)) { found = i; break; }
        long parallel = (System.nanoTime() - t0) / reps;

        t0 = System.nanoTime(); VehicleRecord rec = null;
        for (int r = 0; r < reps; r++)
            for (VehicleRecord v : objs) if (v.regNo.equals(target)) { rec = v; break; }
        long objects = (System.nanoTime() - t0) / reps;

        t0 = System.nanoTime();
        for (int r = 0; r < reps; r++) rec = index.get(target);
        long hashed = (System.nanoTime() - t0) / reps;

        System.out.println("Search for last of " + n + " vehicles (ns per search, avg of " + reps + "):");
        System.out.println("  Parallel arrays  (linear, O(n)) : " + parallel + "  [index " + found + "]");
        System.out.println("  Array of objects (linear, O(n)) : " + objects);
        System.out.println("  Objects + HashMap (O(1) avg)    : " + hashed);
        System.out.println("\nAdding a new attribute (e.g. ownerName):");
        System.out.println("  Parallel arrays: add a 4th array and update EVERY place that inserts/removes/shifts.");
        System.out.println("  Objects        : add one field to the class; callers are untouched.");
        System.out.println("\nSpace per vehicle: parallel arrays = 3 slots (2 refs + 1 long) + the Strings;");
        System.out.println("  object = 1 header (~12-16 B) + 3 fields + the Strings, plus 1 reference in the array.");
        System.out.println("  Both are O(1) per vehicle; objects add a small constant (header) but gain safety.");
    }
}
