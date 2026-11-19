package a3.q1;

/**
 * Q1 alternative: no exceptions - validate first, return error codes (-1, -2, ...).
 * Also measures the cost of creating/throwing an exception vs returning a code.
 */
public class AtmNoExceptions {

    static final int OK = 0, BAD_NUMBER = -1, NEGATIVE = -2, INSUFFICIENT = -3;

    static boolean isInt(String s) {
        if (s == null || s.isEmpty()) return false;
        int i = (s.charAt(0) == '-') ? 1 : 0;
        if (i == s.length()) return false;
        for (; i < s.length(); i++) if (!Character.isDigit(s.charAt(i))) return false;
        return true;
    }

    static int withdrawCode(String amountText, int balance) {
        if (!isInt(amountText)) return BAD_NUMBER;        // check 1
        int amount = Integer.parseInt(amountText);
        if (amount <= 0) return NEGATIVE;                 // check 2
        if (amount > balance) return INSUFFICIENT;        // check 3
        return OK;
    }

    static int withdrawThrow(String amountText, int balance) throws Exception {
        int amount = Integer.parseInt(amountText);        // may throw NFE
        if (amount <= 0) throw new IllegalArgumentException("neg");
        if (amount > balance) throw new Exception("insufficient");
        return OK;
    }

    public static void main(String[] args) throws Exception {
        final int N = 200_000;
        long sink = 0;
        // warm-up
        for (int i = 0; i < 50_000; i++) { sink += withdrawCode("abc", 100); }

        long t0 = System.nanoTime();
        for (int i = 0; i < N; i++) sink += withdrawCode("abc", 100);
        long codeNs = System.nanoTime() - t0;

        t0 = System.nanoTime();
        for (int i = 0; i < N; i++) {
            try { sink += withdrawThrow("abc", 100); } catch (Exception e) { sink++; }
        }
        long excNs = System.nanoTime() - t0;

        System.out.println("Failing input, " + N + " calls (sink=" + sink + ")");
        System.out.printf("Error code : %8.1f ns/call%n", codeNs / (double) N);
        System.out.printf("Exception  : %8.1f ns/call%n", excNs / (double) N);
        System.out.println("Both are O(1) in time, but an exception also allocates a stack trace");
        System.out.println("(O(d) in call-stack depth d, in time and memory); a code allocates nothing.");
    }
}
