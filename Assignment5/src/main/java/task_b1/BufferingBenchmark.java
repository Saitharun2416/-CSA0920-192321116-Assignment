package task_b1;

import common.PassGenerator;
import task_a2.LineCounters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TASK B1.3 - time to count lines for 1 MB, 10 MB and 50 MB files.
 * NOTE: the unbuffered version on 50 MB makes 52 million calls and can take a minute or more.
 */
public class BufferingBenchmark {

    public static void main(String[] args) throws IOException {
        long[] mb = {1, 10, 50};
        System.out.printf("%-8s %-14s %-14s %-14s %-14s%n",
                "size", "unbuffered ms", "buffered ms", "byte[8K] ms", "readAllLines ms");

        for (long m : mb) {
            Path f = Files.createTempFile("attendance", ".log");
            PassGenerator.writeLogFile(f, m * 1024 * 1024);

            double u = time(() -> LineCounters.unbuffered(f));
            double b = time(() -> LineCounters.buffered(f));
            double a = time(() -> LineCounters.byteArray(f, 8192));
            double r = time(() -> LineCounters.readAllLines(f));

            System.out.printf("%-8s %-14.1f %-14.1f %-14.1f %-14.1f   (speed-up buffered: %.1fx)%n",
                    m + " MB", u, b, a, r, u / b);
            Files.delete(f);
        }
        System.out.println("\nCompare with A2.1 prediction: unbuffered = one call per byte, "
                + "buffered = one OS call per 8192 bytes.");
    }

    interface Job { long run() throws IOException; }

    static double time(Job j) throws IOException {
        long t = System.nanoTime();
        j.run();
        return (System.nanoTime() - t) / 1e6;
    }
}
