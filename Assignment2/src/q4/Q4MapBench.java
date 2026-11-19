package q4;

import java.util.*;

/** Q4 - HashMap vs TreeMap vs LinkedHashMap (+ Arrays.binarySearch alternative) for a pharmacy inventory. */
public class Q4MapBench {
    record Medicine(String code, int stock) { }
    static long sink;

    static String code(int i) { return String.format("MED%07d", i); }

    public static void main(String[] args) {
        System.out.println("PREDICTION: put/get HashMap O(1) < LinkedHashMap O(1) < TreeMap O(log n).");
        System.out.println("  Code-order report: TreeMap natural (O(n)); HashMap/LinkedHashMap need an O(n log n) sort.");
        System.out.println("  Insertion-order report: LinkedHashMap natural (O(n)); others cannot recover it at all without extra data.\n");
        System.out.println("Acceptable response time for one lookup (our criterion): < 1 microsecond avg is excellent; < 1 ms per lookup is acceptable for a pharmacist.\n");

        warmUp();
        int[] sizes = {1_000, 100_000, 1_000_000};
        System.out.printf("%-10s %-14s %10s %14s %16s %18s %s%n", "medicines", "structure", "put all ms", "100k lookups ms", "code-order rpt ms", "insertion-order rpt ms", "order ok?");
        for (int n : sizes) {
            List<Integer> ids = new ArrayList<>(); for (int i = 0; i < n; i++) ids.add(i);
            Collections.shuffle(ids, new Random(7));                       // insertion order != code order
            List<String> insertionOrder = new ArrayList<>(); for (int id : ids) insertionOrder.add(code(id));
            String[] queries = new String[100_000]; Random r = new Random(3);
            for (int i = 0; i < queries.length; i++) queries[i] = code(r.nextInt(n));

            List<String> expectedCodeOrder = new ArrayList<>(insertionOrder); Collections.sort(expectedCodeOrder);

            Map<String, Medicine> hm = new HashMap<>(), lhm = new LinkedHashMap<>(), tm = new TreeMap<>();
            Map<?, Medicine>[] maps = new Map[]{hm, tm, lhm}; String[] names = {"HashMap", "TreeMap", "LinkedHashMap"};
            for (int k = 0; k < 3; k++) {
                @SuppressWarnings("unchecked") Map<String, Medicine> m = (Map<String, Medicine>) maps[k];
                long t0 = System.nanoTime();
                for (String c : insertionOrder) m.put(c, new Medicine(c, 10));
                double put = (System.nanoTime() - t0) / 1e6;
                for (String q : queries) sink += m.get(q).stock();          // warm-up
                t0 = System.nanoTime();
                for (String q : queries) sink += m.get(q).stock();
                double look = (System.nanoTime() - t0) / 1e6;

                // report 1: code order
                t0 = System.nanoTime();
                List<String> byCode = (m instanceof TreeMap) ? new ArrayList<>(m.keySet())
                        : sortedKeys(m);                                    // extra O(n log n) work for the "wrong" map
                double rep1 = (System.nanoTime() - t0) / 1e6;
                // report 2: insertion order
                t0 = System.nanoTime();
                List<String> byInsert = (m instanceof LinkedHashMap) ? new ArrayList<>(m.keySet()) : null;
                double rep2 = (System.nanoTime() - t0) / 1e6;
                boolean ok1 = byCode.equals(expectedCodeOrder);
                boolean ok2 = byInsert != null && byInsert.equals(insertionOrder);
                System.out.printf("%-10d %-14s %10.1f %14.1f %16.1f %18s %s%n", n, names[k], put, look, rep1,
                        byInsert == null ? "impossible*" : String.format("%.1f", rep2),
                        "code=" + ok1 + (byInsert == null ? "" : ", insertion=" + ok2));
            }
            System.out.println();
        }
        System.out.println("* a HashMap/TreeMap does not remember insertion order; you would need a separate ArrayList of keys (extra O(n) space).");

        // ---- Alternative: sorted array + Arrays.binarySearch ----
        int n = 100_000; String[] sortedCodes = new String[n];
        for (int i = 0; i < n; i++) sortedCodes[i] = code(i);
        long t0 = System.nanoTime(); Random r = new Random(5);
        for (int i = 0; i < 100_000; i++) sink += Arrays.binarySearch(sortedCodes, code(r.nextInt(n)));
        double bs = (System.nanoTime() - t0) / 1e6;
        String[] grow = Arrays.copyOf(sortedCodes, n + 1);
        t0 = System.nanoTime();
        for (int i = 0; i < 200; i++) { System.arraycopy(grow, 0, grow, 1, n); System.arraycopy(grow, 1, grow, 0, n); }   // shifting cost of an insert at front
        double ins = (System.nanoTime() - t0) / 1e6 / 200;
        System.out.printf("binarySearch on sorted array: 100k lookups %.1f ms (O(log n)); one insert at front = %.3f ms (O(n) shift); code-order report = already sorted O(n); space = n refs only.%n", bs, ins);
        System.out.println("Hashtable is avoided in new code: legacy class, every method synchronized (slow under no contention need), no null keys/values,");
        System.out.println("  iteration order unspecified; use HashMap (single thread) or ConcurrentHashMap (concurrent).");
        System.out.println("DEPLOY: HashMap for lookups + TreeMap (or sort on demand) for the nightly code-order report, or LinkedHashMap if insertion order matters most. Remaining cost = string hashing/compare + cache misses.");
        System.out.println("(sink=" + sink + ")");
    }

    /** JIT/GC warm-up so the first map measured is not penalised. */
    static void warmUp() {
        for (int rep = 0; rep < 3; rep++) {
            for (Map<String, Medicine> m : List.<Map<String, Medicine>>of(new HashMap<>(), new TreeMap<>(), new LinkedHashMap<>())) {
                for (int i = 0; i < 200_000; i++) m.put(code(i), new Medicine(code(i), 1));
                for (int i = 0; i < 200_000; i++) sink += m.get(code(i)).stock();
            }
        }
    }

    static List<String> sortedKeys(Map<String, Medicine> m) {
        List<String> k = new ArrayList<>(m.keySet());
        Collections.sort(k);
        return k;
    }
}
