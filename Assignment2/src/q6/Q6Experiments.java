package q6;

import java.util.*;

/** Q6 - hand-verified correctness, why printing a PQ is misleading, growth, starvation and alternatives. */
public class Q6Experiments {
    static long sink;

    public static void main(String[] args) {
        // ---- correctness check (8 patients, answer worked out by hand) ----
        // arrival order: P1(S3) P2(S1) P3(S3) P4(S5) P5(S1) P6(S2) P7(S2) P8(S1)
        // hand-worked treatment order: S1 by arrival: P2,P5,P8 | S2: P6,P7 | S3: P1,P3 | S5: P4
        Comparator<Patient> cmp = Comparator.comparingInt((Patient p) -> p.severity).thenComparingLong(p -> p.arrival);
        PriorityQueue<Patient> pq = new PriorityQueue<>(cmp);
        int[] sev = {3, 1, 3, 5, 1, 2, 2, 1};
        for (int i = 0; i < sev.length; i++) pq.add(new Patient("P" + (i + 1), sev[i], i));
        System.out.println("Printing the PriorityQueue directly (heap array order, NOT treatment order): " + pq);
        List<String> got = new ArrayList<>(); while (!pq.isEmpty()) got.add(pq.poll().name);
        List<String> expected = List.of("P2", "P5", "P8", "P6", "P7", "P1", "P3", "P4");
        System.out.println("Polled order  : " + got);
        System.out.println("Hand-worked   : " + expected + "  -> " + (got.equals(expected) ? "MATCH" : "MISMATCH"));
        System.out.println("Why the print differs: PriorityQueue is a binary heap in an array; only the HEAD is guaranteed smallest, siblings are partially ordered.\n");

        // ---- growth: predicted add O(log n), poll O(log n) ----
        System.out.println("PREDICTION: add and poll are O(log n): time per op grows very slowly with n (about +log factor).");
        System.out.printf("%-12s %16s %16s%n", "queue size", "ns per add", "ns per poll");
        Random r = new Random(1);
        for (int n : new int[]{10_000, 100_000, 1_000_000}) {
            PriorityQueue<Patient> q = new PriorityQueue<>(cmp);
            Patient[] ps = new Patient[n]; for (int i = 0; i < n; i++) ps[i] = new Patient("p", 1 + r.nextInt(5), i);
            long t0 = System.nanoTime(); for (Patient p : ps) q.add(p); long a = System.nanoTime() - t0;
            t0 = System.nanoTime(); while (q.poll() != null) sink++; long b = System.nanoTime() - t0;
            System.out.printf("%-12d %16d %16d%n", n, a / n, b / n);
        }
        System.out.println("Deployment: 5,000 patients/day = ~0.06 per second; even n=1,000,000 costs microseconds per op. YES, easily.\n");

        // ---- starvation + fair rule (aging) ----
        System.out.println("STARVATION: with a strict (severity, arrival) order, a S5 patient is served only when no S1-S4 patient is waiting.");
        System.out.println("If one S1 patient arrives every tick and the doctor serves one per tick, the S5 patient waits forever.");
        Triage er = new Triage(); er.admit("Minor", 5);
        int served = 0; boolean minorServed = false;
        for (int tick = 0; tick < 1000; tick++) { er.admit("Crit" + tick, 1); Patient p = er.next(); if (p.name.equals("Minor")) minorServed = true; served++; }
        System.out.println("After 1000 ticks, Minor served? " + minorServed + " (served " + served + " critical patients first)");
        System.out.println("FAIR RULE (aging): effective priority = severity - waited/T  <=>  key = arrival + T*severity. With T=10 a S5 patient is overtaken by at most ~40 later arrivals.");
        final int T = 10; long clock = 0;
        PriorityQueue<long[]> fair = new PriorityQueue<>(Comparator.<long[]>comparingLong(x -> x[0] + T * x[1]).thenComparingLong(x -> x[0]));
        fair.add(new long[]{clock++, 5});                         // the S5 patient (id arrival 0)
        long waitedUntil = -1;
        for (int tick = 0; tick < 1000 && waitedUntil < 0; tick++) {
            fair.add(new long[]{clock++, 1});
            long[] p = fair.poll();
            if (p[1] == 5) waitedUntil = tick;
        }
        System.out.println("With aging the S5 patient is served at tick " + waitedUntil + ".\n");

        // ---- alternatives: sorted LinkedList vs 5 FIFO queues vs PriorityQueue ----
        System.out.printf("%-10s %-24s %-24s %-24s%n", "n", "PriorityQueue add+poll", "sorted LinkedList", "5 x ArrayDeque");
        for (int n : new int[]{2_000, 10_000, 40_000}) {
            int[] sv = new int[n]; for (int i = 0; i < n; i++) sv[i] = 1 + r.nextInt(5);
            long t0 = System.nanoTime(); PriorityQueue<Patient> q = new PriorityQueue<>(cmp);
            for (int i = 0; i < n; i++) q.add(new Patient("p", sv[i], i)); while (q.poll() != null) sink++;
            long a = System.nanoTime() - t0;
            t0 = System.nanoTime(); LinkedList<Patient> ll = new LinkedList<>();
            for (int i = 0; i < n; i++) {                               // O(n) insertion at correct position
                Patient p = new Patient("p", sv[i], i); ListIterator<Patient> it = ll.listIterator();
                while (it.hasNext()) { if (it.next().severity > p.severity) { it.previous(); break; } }
                it.add(p);
            }
            while (ll.pollFirst() != null) sink++;
            long b = System.nanoTime() - t0;
            t0 = System.nanoTime(); @SuppressWarnings("unchecked") ArrayDeque<Patient>[] lv = new ArrayDeque[6];
            for (int i = 1; i <= 5; i++) lv[i] = new ArrayDeque<>();
            for (int i = 0; i < n; i++) lv[sv[i]].addLast(new Patient("p", sv[i], i));
            for (int served2 = 0; served2 < n; served2++) for (int s = 1; s <= 5; s++) if (!lv[s].isEmpty()) { lv[s].pollFirst(); break; }
            long c = System.nanoTime() - t0;
            System.out.printf("%-10d %-24s %-24s %-24s%n", n, a / 1_000_000 + " ms", b / 1_000_000 + " ms", c / 1_000_000 + " ms");
        }
        System.out.println("Complexity: PriorityQueue admit O(log n), serve O(log n), space O(n).  Sorted LinkedList admit O(n), serve O(1), space O(n) (heavier nodes).");
        System.out.println("            5 FIFO queues admit O(1), serve O(1) (scan 5 levels = constant), space O(n) - fastest but only works because severity has few fixed levels and has no aging.");
        System.out.println("(sink=" + sink + ")");
    }
}
