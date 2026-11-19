package a3.q3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/** Q3 - three ways to read store files: leaky manual, correct manual (finally), try-with-resources. */
public class ReportGenerator {
    private static final Logger LOG = Logger.getLogger("report");

    /** Sum of qty*price in one file. Throws NumberFormatException on a corrupt line. */
    private static double subtotal(TrackedReader r) throws IOException {
        double total = 0; String line;
        while ((line = r.readLine()) != null) {
            String[] p = line.split(",");
            total += Double.parseDouble(p[1]) * Double.parseDouble(p[2]);   // breakpoint here for step-through
        }
        return total;
    }

    /** BUGGY manual version: close() is only reached if nothing fails -> leak. */
    static double manualLeaky(List<Path> files) {
        double sum = 0;
        for (Path f : files) {
            try {
                TrackedReader r = new TrackedReader(f);
                double s = subtotal(r);          // exception here skips the close() below
                r.close();
                sum += s;
            } catch (IOException | RuntimeException e) {
                LOG.warning("LEAKY: failed on " + f.getFileName() + " : " + e);
            }
        }
        return sum;
    }

    /** Correct manual version with finally (note the amount of boilerplate). */
    static double manualFinally(List<Path> files) {
        double sum = 0;
        for (Path f : files) {
            TrackedReader r = null;
            try {
                r = new TrackedReader(f);
                sum += subtotal(r);
            } catch (IOException | RuntimeException e) {
                LOG.warning("FINALLY: failed on " + f.getFileName() + " : " + e);
            } finally {
                if (r != null) {
                    try { r.close(); } catch (IOException ignored) { }
                }
            }
        }
        return sum;
    }

    /** try-with-resources: closed automatically, even on exceptions, in reverse order of opening. */
    static double withResources(List<Path> files) {
        double sum = 0;
        for (Path f : files) {
            try (TrackedReader r = new TrackedReader(f)) {
                sum += subtotal(r);
            } catch (IOException | RuntimeException e) {
                LOG.warning("TWR: failed on " + f.getFileName() + " : " + e);
            }
        }
        return sum;
    }

    // ---- suppressed exception demo ----
    static class Res implements AutoCloseable {
        final String n; Res(String n) { this.n = n; System.out.println("  open " + n); }
        @Override public void close() { System.out.println("  close " + n); throw new IllegalStateException("close failed: " + n); }
    }

    static void suppressedDemo() {
        System.out.println("\n== Suppressed exceptions (closing order is reverse of opening) ==");
        try (Res a = new Res("A"); Res b = new Res("B")) {
            throw new RuntimeException("body failed");
        } catch (RuntimeException e) {
            System.out.println("  primary   : " + e.getMessage());
            for (Throwable s : e.getSuppressed()) System.out.println("  suppressed: " + s.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("stores");
        Path[] f = new Path[5];
        for (int i = 0; i < 5; i++) {
            f[i] = dir.resolve("store" + (i + 1) + ".txt");
            Files.writeString(f[i], "pen,10,2.5\nbook,4,100\nbag,1,799.5\n");
        }
        // store3 and store5 are corrupt: they fail PART-WAY through reading
        Files.writeString(f[2], "pen,10,2.5\nbook,four,100\nbag,1,799.5\n");
        Files.writeString(f[4], "pen,10,2.5\nCORRUPT\n");
        List<Path> files = List.of(f);

        System.out.println("Failing input: " + files.size() + " files, 2 corrupt (store3, store5)");

        double a = manualLeaky(files);
        System.out.printf("manual (leaky)     total=%.2f  files left open=%d%n", a, TrackedReader.OPEN.get());
        TrackedReader.OPEN.set(0);

        double b = manualFinally(files);
        System.out.printf("manual (finally)   total=%.2f  files left open=%d%n", b, TrackedReader.OPEN.get());
        TrackedReader.OPEN.set(0);

        double c = withResources(files);
        System.out.printf("try-with-resources total=%.2f  files left open=%d%n", c, TrackedReader.OPEN.get());

        System.out.println("Same report from correct versions: " + (b == c));
        suppressedDemo();
    }
}
