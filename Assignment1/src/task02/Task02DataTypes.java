package task02;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * TASK 2 - Data representation, casting risks, jagged vs flat vs ArrayList.
 * Run normally (uses sample values). Run with program argument  --interactive  to use Scanner.
 */
public class Task02DataTypes {

    // ---- chosen data types ----
    //  slot number   : int      (small whole number)
    //  vehicle number: String   (alphanumeric, e.g. TN01AB1111)
    //  entry time    : int/long minutes since midnight of day 0 (long if epoch based)
    //  duration      : int minutes
    //  fee           : double (or long paise / BigDecimal in real billing)
    static final double RATE_PER_HOUR = 30.0;

    static double wrongFeeIntDivision(int minutes) {
        int hours = minutes / 60;                  // 130/60 = 2  (loses 10 minutes!)
        return hours * RATE_PER_HOUR;              // 60.0 instead of 90.0
    }
    static double wrongFeeCastAfter(int minutes) {
        int hours = (int) (minutes / 60);          // cast is applied AFTER the integer division
        return hours * RATE_PER_HOUR;
    }
    static double wrongFeeTruncate(int minutes) {
        int hours = (int) (minutes / 60.0);        // floating division, but (int) truncates 2.16 -> 2
        return hours * RATE_PER_HOUR;
    }
    static double correctFeeInteger(int minutes) {
        int hours = (minutes + 59) / 60;           // integer ceiling, no floating point
        return hours * RATE_PER_HOUR;
    }
    static double correctFeeMath(int minutes) {
        int hours = (int) Math.ceil(minutes / 60.0);
        return hours * RATE_PER_HOUR;
    }

    static int[] readInput(Scanner sc) {
        System.out.print("Enter floors: "); int f = sc.nextInt();
        int[] slots = new int[f];
        for (int i = 0; i < f; i++) { System.out.print("Slots on floor " + i + ": "); slots[i] = sc.nextInt(); }
        return slots;
    }

    public static void main(String[] args) {
        int[] slotsPerFloor = {20, 50, 100};
        if (args.length > 0 && args[0].equals("--interactive")) {
            try (Scanner sc = new Scanner(System.in)) { slotsPerFloor = readInput(sc); }
        }

        System.out.println("=== Casting risk: 130 minutes at Rs 30/hour (correct = Rs 90) ===");
        System.out.println("int division     : " + wrongFeeIntDivision(130));
        System.out.println("(int)(m/60) cast : " + wrongFeeCastAfter(130));
        System.out.println("(int)(m/60.0)    : " + wrongFeeTruncate(130));
        System.out.println("(m+59)/60 (used) : " + correctFeeInteger(130));
        System.out.println("Math.ceil(m/60.0): " + correctFeeMath(130));

        long epochMillis = 1_800_000_000_000L;
        System.out.println("\nlong -> int overflow: (int) " + epochMillis + " = " + (int) epochMillis
                + "  (so entry time kept as long/minutes, never cast down)");
        System.out.println("float precision    : 0.1f + 0.2f = " + (0.1f + 0.2f) + "  -> use double/BigDecimal for money");

        // ---- Representation 1: jagged array ----
        String[][] jagged = new String[slotsPerFloor.length][];
        int total = 0;
        for (int f = 0; f < slotsPerFloor.length; f++) { jagged[f] = new String[slotsPerFloor[f]]; total += slotsPerFloor[f]; }
        jagged[1][7] = "TN01AB1111";

        // ---- Representation 2: flat 1-D array with prefix offsets (needed when floors differ) ----
        int[] start = new int[slotsPerFloor.length + 1];
        for (int f = 0; f < slotsPerFloor.length; f++) start[f + 1] = start[f] + slotsPerFloor[f];
        String[] flat = new String[total];
        flat[start[1] + 7] = "TN01AB1111";   // index = start[floor] + slot  (floor*slotsPerFloor only works if equal)

        // ---- Representation 3: ArrayList<String[]> ----
        List<String[]> list = new ArrayList<>();
        for (int s : slotsPerFloor) list.add(new String[s]);
        list.get(1)[7] = "TN01AB1111";

        System.out.println("\nSlots per floor      : " + java.util.Arrays.toString(slotsPerFloor));
        System.out.println("jagged[1][7]         : " + jagged[1][7] + "   (locate: O(1))");
        System.out.println("flat[start[1]+7]     : " + flat[start[1] + 7] + "   (locate: O(1), extra int[F+1])");
        System.out.println("list.get(1)[7]       : " + list.get(1)[7] + "   (locate: O(1), floors can be added at runtime)");
        System.out.println("Total space (all)    : O(total slots) each; flat = 1 array object, jagged = F+1 array objects");
        System.out.println("Note: floor*slotsPerFloor+slot is WRONG when floors have different sizes.");
    }
}
