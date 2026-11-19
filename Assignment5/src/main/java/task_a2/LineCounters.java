package task_a2;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** TASK A2 - four ways of counting the lines of a big file. */
public final class LineCounters {

    private LineCounters() { }

    /** (1) One read() call per byte  -> about one operating-system call per byte. */
    public static long unbuffered(Path p) throws IOException {
        long lines = 0;
        try (FileInputStream in = new FileInputStream(p.toFile())) {
            int b;
            while ((b = in.read()) != -1) {
                if (b == '\n') lines++;
            }
        }
        return lines;
    }

    /** (2) BufferedInputStream: still read() per byte in Java, but the OS is called once per 8 KB. */
    public static long buffered(Path p) throws IOException {
        long lines = 0;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(p.toFile()), 8192)) {
            int b;
            while ((b = in.read()) != -1) {
                if (b == '\n') lines++;
            }
        }
        return lines;
    }

    /** (3) Own byte[] buffer: one read(byte[]) call fills the whole buffer. */
    public static long byteArray(Path p, int bufferSize) throws IOException {
        long lines = 0;
        byte[] buf = new byte[bufferSize];
        try (FileInputStream in = new FileInputStream(p.toFile())) {
            int n;
            while ((n = in.read(buf)) != -1) {
                for (int i = 0; i < n; i++) {
                    if (buf[i] == '\n') lines++;
                }
            }
        }
        return lines;
    }

    /** (4) Alternative concept: whole file in memory -> O(file size) memory. */
    public static long readAllLines(Path p) throws IOException {
        return Files.readAllLines(p).size();
    }
}
