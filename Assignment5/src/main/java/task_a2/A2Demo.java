package task_a2;

import common.PassGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** TASK A2.1 / A2.2 - prediction of read calls and a quick demonstration on a 5 MB file. */
public class A2Demo {

    public static void main(String[] args) throws IOException {
        long fileBytes = 50L * 1024 * 1024;
        System.out.println("=== A2.1 Prediction for a 50 MB file ===");
        System.out.println("Unbuffered read() calls  : " + fileBytes + "  (one per byte, + 1 for EOF)");
        System.out.println("Buffered (8 KB) OS calls : " + (fileBytes / 8192) + "  (one per 8192 bytes)");
        System.out.println("Reduction factor         : " + 8192 + "x fewer system calls");

        Path f = Files.createTempFile("attendance", ".log");
        PassGenerator.writeLogFile(f, 5L * 1024 * 1024);
        System.out.println("\n=== Quick demonstration on a " + Files.size(f) / (1024 * 1024) + " MB file ===");
        run("unbuffered FileInputStream.read()", () -> LineCounters.unbuffered(f));
        run("BufferedInputStream.read()       ", () -> LineCounters.buffered(f));
        run("byte[8192] buffer                ", () -> LineCounters.byteArray(f, 8192));
        run("Files.readAllLines()             ", () -> LineCounters.readAllLines(f));
        Files.delete(f);
    }

    interface Job { long run() throws IOException; }

    static void run(String label, Job j) throws IOException {
        long t = System.nanoTime();
        long lines = j.run();
        System.out.printf("%s -> %d lines in %.1f ms%n", label, lines, (System.nanoTime() - t) / 1e6);
    }
}
