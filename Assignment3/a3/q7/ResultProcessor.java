package a3.q7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Q7 - Semester result processing.
 *
 * Requirements (measurable):
 *  R1 every one of N files yields exactly one outcome (success or failure)  => ok + failed == N
 *  R2 a bad/missing file never stops the run
 *  R3 summary lists each failed file with the reason
 *  R4 total time decreases when threads are added up to the I/O limit
 *
 * Design: ExecutorService(p) + Callable<Outcome> per file + Future.get() for each;
 * shared results in ConcurrentHashMap (id -> average), failures in ConcurrentLinkedQueue.
 * Exceptions are caught INSIDE call() (domain failures) and by ExecutionException (unexpected ones).
 */
public class ResultProcessor {

    static class CorruptFileException extends Exception {
        CorruptFileException(String m, Throwable c) { super(m, c); }
        CorruptFileException(String m) { super(m); }
    }

    record Outcome(Path file, boolean ok, String reason) { }

    /** simulated per-file I/O latency in nanoseconds (so thread-count effects are visible) */
    static volatile long LATENCY_NS = 200_000;

    final Map<String, Double> results = new ConcurrentHashMap<>();
    final ConcurrentLinkedQueue<String> failures = new ConcurrentLinkedQueue<>();

    /** read + validate + compute. Format: studentId,m1,m2,m3,m4,m5 with 0 <= m <= 100 */
    Callable<Outcome> task(Path f) {
        return () -> {
            try {
                LockSupport.parkNanos(LATENCY_NS);
                String line = Files.readAllLines(f).get(0);              // NoSuchFileException / IOException / IndexOutOfBounds
                String[] p = line.split(",");
                if (p.length != 6) throw new CorruptFileException("expected 6 fields but found " + p.length);
                double sum = 0;
                for (int i = 1; i < p.length; i++) {
                    double m = Double.parseDouble(p[i].trim());          // NumberFormatException
                    if (m < 0 || m > 100) throw new CorruptFileException("mark out of range: " + m);
                    sum += m;
                }
                results.put(p[0], sum / 5);
                return new Outcome(f, true, "OK");
            } catch (NoSuchFileException e) {
                failures.add(f.getFileName() + " : MISSING");
                return new Outcome(f, false, "missing");
            } catch (CorruptFileException e) {
                failures.add(f.getFileName() + " : CORRUPT (" + e.getMessage() + ")");
                return new Outcome(f, false, "corrupt");
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                failures.add(f.getFileName() + " : CORRUPT (" + e + ")");
                return new Outcome(f, false, "corrupt");
            } catch (IOException e) {
                failures.add(f.getFileName() + " : IO ERROR (" + e.getMessage() + ")");
                return new Outcome(f, false, "io");
            }
        };
    }

    /** returns {ok, failed}. */
    int[] run(List<Path> files, int threads) throws InterruptedException {
        results.clear(); failures.clear();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<Outcome>> futures = new ArrayList<>();
        for (Path f : files) futures.add(pool.submit(task(f)));
        int ok = 0, bad = 0;
        for (Future<Outcome> fu : futures) {
            try {
                if (fu.get().ok()) ok++; else bad++;
            } catch (ExecutionException e) {                              // anything unexpected thrown by call()
                bad++; failures.add("UNEXPECTED: " + e.getCause());
            }
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);
        return new int[]{ok, bad};
    }

    static List<Path> generate(Path dir, int n) throws IOException {
        List<Path> files = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Path f = dir.resolve("score" + i + ".txt");
            files.add(f);
            if (i % 70 == 69) continue;                                           // missing file
            if (i % 50 == 49) Files.writeString(f, "S" + i + ",80,xx,70,60,50\n"); // corrupt
            else Files.writeString(f, "S" + i + "," + (50 + i % 50) + ",70,80,60,90\n");
        }
        return files;
    }

    public static void main(String[] args) throws Exception {
        // ---- Hand-checked small set ----
        Path small = Files.createTempDirectory("small");
        List<Path> sf = new ArrayList<>();
        String[] content = {"A1,80,80,80,80,80", "A2,90,70,60,50,80", "A3,100,100,100,100,100",
                "A4,80,abc,80,80,80", null /* missing */, "A6,101,50,50,50,50", ""};
        for (int i = 0; i < content.length; i++) {
            Path f = small.resolve("s" + i + ".txt"); sf.add(f);
            if (content[i] != null) Files.writeString(f, content[i]);
        }
        ResultProcessor rp = new ResultProcessor();
        LATENCY_NS = 0;
        int[] r = rp.run(sf, 4);
        System.out.println("== Hand-checked set: 7 files -> expected ok=3, failed=4 ==");
        System.out.println("actual ok=" + r[0] + " failed=" + r[1] + (r[0] == 3 && r[1] == 4 ? "  PASS" : "  FAIL"));
        rp.results.forEach((k, v) -> System.out.println("  " + k + " average=" + v));
        rp.failures.forEach(s -> System.out.println("  FAILED " + s));

        // ---- 10,000 files, pool sizes 1,2,4,8 ----
        LATENCY_NS = 200_000;
        Path big = Files.createTempDirectory("big");
        List<Path> files = generate(big, 10_000);
        long expectedMissing = files.stream().filter(p -> !Files.exists(p)).count();
        System.out.println("\n== 10,000 files (" + expectedMissing + " missing, every 50th file corrupt) ==");
        for (int p : new int[]{1, 2, 4, 8}) {
            long t0 = System.nanoTime();
            int[] x = rp.run(files, p);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            System.out.printf("pool=%d  time=%5d ms  ok=%d failed=%d  (ok+failed=%d)%n", p, ms, x[0], x[1], x[0] + x[1]);
        }

        // ---- Alternatives ----
        System.out.println("\n== Alternatives ==");
        long t0 = System.nanoTime();
        long okPar = files.parallelStream().filter(f -> {
            try { return rp.task(f).call().ok(); } catch (Exception e) { return false; }
        }).count();
        System.out.printf("parallelStream (common pool ~%d threads): %d ms, ok=%d%n",
                Runtime.getRuntime().availableProcessors(), (System.nanoTime() - t0) / 1_000_000, okPar);

        t0 = System.nanoTime();
        List<Thread> ts = new ArrayList<>();
        for (Path f : files) { Thread t = new Thread(() -> { try { rp.task(f).call(); } catch (Exception ignored) { } }); ts.add(t); t.start(); }
        for (Thread t : ts) t.join();
        System.out.printf("thread-per-file (10,000 threads x ~1MB stack reserved): %d ms%n", (System.nanoTime() - t0) / 1_000_000);
    }
}
