package q7;

import java.util.*;

/**
 * Q7 - Diagnose, Redesign, Validate (chosen solution: Q6 triage WITH the fairness/aging rule).
 *
 * A. DIAGNOSIS. The first fair-triage idea recomputes every waiting patient's effective priority
 *    (severity - waited/T) each time the doctor asks for the next patient, because "waited" changes with time.
 *    That is a linear scan per call -> O(n) per next(), O(n^2) for a day's queue; the PriorityQueue from Q6 is
 *    not used because its ordering would go stale as time passes (re-heapifying every call = O(n) too).
 *    Repeated work: the same patients are re-scored over and over.
 * B. OBJECTIVE. next()/admit() must be O(log n) and must give EXACTLY the same service order as the naive version.
 * C. REDESIGN. effective(p, now) = severity - (now - arrival)/T. Multiply by T and add now:
 *    T*severity + arrival  -- 'now' is the same for every patient, so it cancels out of every comparison.
 *    Therefore the ORDER never changes with time: compute key = arrival + T*severity ONCE at admission and use an ordinary
 *    heap. Property exploited: a monotone shift common to all elements preserves ordering.
 *    Trade-offs: one long per patient; the rule is a fixed linear aging (cannot express "after 30 min jump straight to S1" without extra logic).
 * D. PREDICTION. Same service order; operations per next() fall from ~n to ~log2(n); at n = 20,000 expect >100x fewer comparisons
 *    and a large time drop, with the gap growing as n grows.
 */
public class Q7Redesign {
    static long comparisons;      // operation counter shared by both implementations
    static final int T = 10;

    static class P {
        final int id, severity; final long arrival; final long key;
        P(int id, int severity, long arrival) { this.id = id; this.severity = severity; this.arrival = arrival; this.key = arrival + (long) T * severity; }
    }

    /** ORIGINAL: scan everything on each call, recomputing the effective priority with the current time. */
    static class NaiveAgingTriage {
        private final ArrayList<P> waiting = new ArrayList<>();
        void admit(P p) { waiting.add(p); }
        P next(long now) {
            if (waiting.isEmpty()) return null;
            int best = 0; long bestEff = effective(waiting.get(0), now);
            for (int i = 1; i < waiting.size(); i++) {
                long eff = effective(waiting.get(i), now); comparisons++;
                if (eff < bestEff || (eff == bestEff && waiting.get(i).arrival < waiting.get(best).arrival)) { best = i; bestEff = eff; }
            }
            P p = waiting.get(best);
            waiting.set(best, waiting.get(waiting.size() - 1)); waiting.remove(waiting.size() - 1);   // O(1) removal; order irrelevant
            return p;
        }
        /** T * (severity - waited/T), kept in exact integer arithmetic: T*severity - waited. Recomputed for EVERY patient on EVERY call. */
        static long effective(P p, long now) { return (long) T * p.severity - (now - p.arrival); }
    }

    /** REDESIGN: time-invariant key -> plain binary heap. */
    static class KeyedAgingTriage {
        private final PriorityQueue<P> heap = new PriorityQueue<>((a, b) -> {
            comparisons++;
            int c = Long.compare(a.key, b.key); return c != 0 ? c : Long.compare(a.arrival, b.arrival);
        });
        void admit(P p) { heap.add(p); }
        P next() { return heap.poll(); }
    }

    /** identical deterministic workload: 60% admit, 40% serve, clock ticks on every operation. */
    static List<Integer> runNaive(int ops, long seed, long[] timeNs) {
        Random r = new Random(seed); NaiveAgingTriage t = new NaiveAgingTriage(); List<Integer> served = new ArrayList<>();
        long clock = 0, t0 = System.nanoTime(); int id = 0;
        for (int i = 0; i < ops; i++, clock++) {
            if (r.nextInt(10) < 6) t.admit(new P(id++, 1 + r.nextInt(5), clock));
            else { P p = t.next(clock); if (p != null) served.add(p.id); }
        }
        P p; while ((p = t.next(clock++)) != null) served.add(p.id);
        timeNs[0] = System.nanoTime() - t0; return served;
    }
    static List<Integer> runKeyed(int ops, long seed, long[] timeNs) {
        Random r = new Random(seed); KeyedAgingTriage t = new KeyedAgingTriage(); List<Integer> served = new ArrayList<>();
        long clock = 0, t0 = System.nanoTime(); int id = 0;
        for (int i = 0; i < ops; i++, clock++) {
            if (r.nextInt(10) < 6) t.admit(new P(id++, 1 + r.nextInt(5), clock));
            else { P p = t.next(); if (p != null) served.add(p.id); }
        }
        P p; while ((p = t.next()) != null) served.add(p.id);
        timeNs[0] = System.nanoTime() - t0; return served;
    }

    public static void main(String[] args) {
        System.out.println("E. Original (scan + re-score every call) vs Redesign (time-invariant key + heap). Same workload, same seed.");
        System.out.printf("%-9s | %-26s | %-26s | %-10s | %s%n", "ops", "ORIGINAL comps / ms", "REDESIGN comps / ms", "speed-up", "same order?");
        int[] sizes = {2_000, 10_000, 40_000, 100_000};
        for (int ops : sizes) {
            long[] tn = new long[1], tk = new long[1];
            runKeyed(ops, 99, new long[1]);                                       // warm-up
            comparisons = 0; List<Integer> a = runNaive(ops, 42, tn); long ca = comparisons;
            comparisons = 0; List<Integer> b = runKeyed(ops, 42, tk); long cb = comparisons;
            System.out.printf("%-9d | %,12d / %8.1f   | %,12d / %8.1f   | %7.1fx   | %s%n", ops, ca, tn[0] / 1e6, cb, tk[0] / 1e6,
                    (double) tn[0] / tk[0], a.equals(b));
        }
        System.out.println();
        System.out.println("Raw data above (repeat the run for averages). Objects created: identical (one P per patient in both).");
        System.out.println("F. DEFENCE: prediction supported if 'same order?' is true everywhere and comparisons drop from O(n) to O(log n) per call.");
        System.out.println("   Costs: aging is now fixed/linear (rule must be expressible as a time-invariant key); 8 extra bytes per patient (long key).");
        System.out.println("   Original may still be preferable for tiny queues (n < ~50) or when the aging rule is arbitrary (e.g. depends on patient history), since a scan can apply ANY rule.");
    }
}
