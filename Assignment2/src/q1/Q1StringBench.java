package q1;

import java.util.StringJoiner;

/**
 * Q1 - String (+), StringBuilder, StringBuffer, plus alternatives join / StringJoiner / format.
 * PREDICTION (written before running):
 *  - Workload A (1 ticket, 10 fields): difference negligible (all in microseconds).
 *  - Workload B (10,000 tickets x 50 fields): '+' clearly slower (~3-10x); SB ~ SBuf (uncontended lock is cheap).
 *  - Workload C (100 tickets x 3,000 fields): '+' dramatically slower, O(n^2) chars copied vs O(n).
 * Reason: String is immutable, so every '+' allocates a new String and copies all previous characters.
 */
public class Q1StringBench {
    static final int REPEATS = 5;

    static String[] fields(int n) {
        String[] f = new String[n];
        for (int i = 0; i < n; i++) f[i] = "FIELD" + i + "=value" + (i * 7919 % 1000) + ";";
        return f;
    }
    static int sink;

    static String plus(String[] f) { String s = ""; for (String x : f) s = s + x; return s; }
    static String sb(String[] f) { StringBuilder b = new StringBuilder(); for (String x : f) b.append(x); return b.toString(); }
    static String sbuf(String[] f) { StringBuffer b = new StringBuffer(); for (String x : f) b.append(x); return b.toString(); }
    static String join(String[] f) { return String.join("", f); }
    static String joiner(String[] f) { StringJoiner j = new StringJoiner(""); for (String x : f) j.add(x); return j.toString(); }
    static String format(String[] f) { String s = ""; for (String x : f) s = String.format("%s%s", s, x); return s; }

    interface Builder { String build(String[] f); }

    static double timeMs(Builder b, String[] f, int tickets) {
        for (int w = 0; w < 2; w++) for (int t = 0; t < Math.max(1, tickets / 10); t++) sink += b.build(f).length(); // warm-up
        double total = 0;
        for (int r = 0; r < REPEATS; r++) {
            long t0 = System.nanoTime();
            for (int t = 0; t < tickets; t++) sink += b.build(f).length();
            total += (System.nanoTime() - t0) / 1e6;
        }
        return total / REPEATS;
    }

    static long usedMemory() { System.gc(); return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(); }

    static void workload(String name, int tickets, int nFields, boolean includeFormat) {
        String[] f = fields(nFields);
        System.out.printf("%n--- %s: %,d tickets x %,d fields (avg of %d runs) ---%n", name, tickets, nFields, REPEATS);
        long mem0 = usedMemory(); String keep = plus(f); long mem1 = usedMemory();
        System.out.printf("  est. intermediate Strings per ticket: '+' = %,d   StringBuilder = 1 final (+ ~log2 buffer growths)%n", nFields - 1);
        System.out.printf("  ticket length = %,d chars; chars copied by '+' ~ n*L/2 (n fields, L final length) = %,d%n", keep.length(), (long) nFields * keep.length() / 2);
        System.out.printf("  %-22s %10.3f ms%n", "String +", timeMs(Q1StringBench::plus, f, tickets));
        System.out.printf("  %-22s %10.3f ms%n", "StringBuilder", timeMs(Q1StringBench::sb, f, tickets));
        System.out.printf("  %-22s %10.3f ms%n", "StringBuffer", timeMs(Q1StringBench::sbuf, f, tickets));
        System.out.printf("  %-22s %10.3f ms%n", "String.join", timeMs(Q1StringBench::join, f, tickets));
        System.out.printf("  %-22s %10.3f ms%n", "StringJoiner", timeMs(Q1StringBench::joiner, f, tickets));
        if (includeFormat) System.out.printf("  %-22s %10.3f ms%n", "String.format (loop)", timeMs(Q1StringBench::format, f, Math.max(1, tickets / 10)) * 10);
        long m0 = usedMemory(); String[] hold = new String[200];
        for (int i = 0; i < hold.length; i++) hold[i] = sb(f);
        long m1 = usedMemory();
        System.out.printf("  memory held by 200 finished tickets: ~%,d KB (heap delta, approximate)%n", Math.max(0, (m1 - m0) / 1024));
        sink += hold.length + (mem1 > mem0 ? 1 : 0);
    }

    public static void main(String[] args) {
        workload("Workload A (short ticket)", 20_000, 10, true);
        workload("Workload B (many medium tickets)", 10_000, 50, false);
        workload("Workload C (few very long tickets, own workload)", 100, 3_000, false);
        System.out.println("\nComplexity: '+' in a loop = O(n^2) chars copied, n-1 intermediate Strings; StringBuilder = O(n) amortised, 1 buffer (+ growths).");
        System.out.println("join/StringJoiner = O(n) (they size once or use a builder). format in a loop is O(n^2) AND slow (parses format each time).");
        System.out.println("Recommendation: per-ticket text -> StringBuilder (thread confined, no lock). Shared booking log -> StringBuffer or better a synchronized/concurrent queue (see Q1ThreadSafety).");
        System.out.println("(sink=" + sink + ")");
    }
}
