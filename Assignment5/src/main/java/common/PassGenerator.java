package common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Generates test bus-pass data and big files for the experiments. */
public final class PassGenerator {

    private PassGenerator() { }

    /** Pass i: {passId, regNo, name, route, amount} as Strings. */
    public static String[] pass(int i) {
        return new String[]{
                "P" + (100000 + i),
                String.valueOf(192300000L + i),
                "Student" + i,
                String.valueOf(i % 20 + 1),
                String.valueOf(3000.0 + (i % 20) * 100)
        };
    }

    public static List<String[]> generate(int n) {
        List<String[]> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(pass(i));
        }
        return list;
    }

    /** Writes n valid pass lines (comma separated) into the file using one buffered writer. */
    public static void writePassFile(Path file, int n) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file)) {
            for (int i = 0; i < n; i++) {
                w.write(String.join(",", pass(i)));
                w.newLine();
            }
        }
    }

    /** Creates an attendance-log style text file of roughly the requested size. */
    public static void writeLogFile(Path file, long targetBytes) throws IOException {
        long written = 0;
        int i = 0;
        try (BufferedWriter w = Files.newBufferedWriter(file)) {
            while (written < targetBytes) {
                String line = "2026-11-19 10:" + (i % 60 < 10 ? "0" : "") + (i % 60)
                        + ":00,19230" + (1000 + i % 9000) + ",ENTRY,GATE-" + (i % 5);
                w.write(line);
                w.newLine();
                written += line.length() + 1;
                i++;
            }
        }
    }
}
