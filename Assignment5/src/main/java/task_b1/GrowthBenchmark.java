package task_b1;

import common.PassGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TASK B1.4 - time to find a pass as the file grows from 1,000 to 100,000 records,
 * plus the HashMap alternative (O(q*n) versus O(n+q)).
 */
public class GrowthBenchmark {

    public static void main(String[] args) throws IOException {
        int[] sizes = {1_000, 5_000, 10_000, 50_000, 100_000};
        int q = 200;                                   // number of searches for the HashMap comparison

        System.out.println("PART 1: one search, scanning the file (O(n))");
        System.out.printf("%-10s %-16s %-16s %-16s%n", "records", "first record ms", "last record ms", "missing id ms");

        for (int n : sizes) {
            Path f = Files.createTempFile("passes", ".csv");
            PassGenerator.writePassFile(f, n);
            PassStore store = new PassStore(f);
            store.find("P100000");                        // warm-up

            double first = avg(store, "P100000", 5);
            double last = avg(store, "P" + (100000 + n - 1), 5);
            double miss = avg(store, "P000000", 5);
            System.out.printf("%-10d %-16.3f %-16.3f %-16.3f%n", n, first, last, miss);
            Files.delete(f);
        }

        System.out.println("\nPART 2: " + q + " searches - scan every time versus HashMap built once");
        System.out.printf("%-10s %-18s %-14s %-14s %-14s%n", "records", "scan x q (ms)", "load map (ms)", "q lookups (ms)", "map total (ms)");
        for (int n : sizes) {
            Path f = Files.createTempFile("passes", ".csv");
            PassGenerator.writePassFile(f, n);
            PassStore store = new PassStore(f);

            long t = System.nanoTime();
            for (int i = 0; i < q; i++) {
                store.find("P" + (100000 + (i * 7919) % n));
            }
            double scan = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            CachedPassStore cache = new CachedPassStore(f);
            double load = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            for (int i = 0; i < q; i++) {
                cache.find("P" + (100000 + (i * 7919) % n));
            }
            double lookups = (System.nanoTime() - t) / 1e6;

            System.out.printf("%-10d %-18.2f %-14.2f %-14.3f %-14.2f%n", n, scan, load, lookups, load + lookups);
            Files.delete(f);
        }
    }

    static double avg(PassStore s, String id, int reps) throws IOException {
        long total = 0;
        for (int i = 0; i < reps; i++) {
            long t = System.nanoTime();
            s.find(id);
            total += System.nanoTime() - t;
        }
        return total / (double) reps / 1e6;
    }
}
