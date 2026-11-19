package task_a4;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** TASK A4 - Alternative concept: CSV text instead of Java serialization (size and time for n residents). */
public class SerializationVsCsv {

    public static void main(String[] args) throws Exception {
        int[] sizes = {1_000, 10_000, 100_000};
        System.out.printf("%-10s %-14s %-12s %-12s | %-14s %-12s %-12s%n",
                "n", "serial bytes", "save ms", "load ms", "csv bytes", "save ms", "load ms");

        for (int n : sizes) {
            List<Resident> list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                list.add(new Resident("Student" + i, String.valueOf(192300000L + i), "pw", null));
            }
            Path ser = Files.createTempFile("res", ".ser");
            Path csv = Files.createTempFile("res", ".csv");

            long t = System.nanoTime();
            try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(ser)))) {
                out.writeObject(list);
            }
            double serSave = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(ser)))) {
                in.readObject();
            }
            double serLoad = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            try (BufferedWriter w = Files.newBufferedWriter(csv)) {
                for (Resident r : list) {
                    w.write(r.getName() + "," + r.getRegNo());
                    w.newLine();
                }
            }
            double csvSave = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            List<String[]> back = new ArrayList<>();
            try (BufferedReader r = Files.newBufferedReader(csv)) {
                String line;
                while ((line = r.readLine()) != null) {
                    back.add(line.split(","));      // a NEW column simply appears as an extra field
                }
            }
            double csvLoad = (System.nanoTime() - t) / 1e6;

            System.out.printf("%-10d %-14d %-12.1f %-12.1f | %-14d %-12.1f %-12.1f%n",
                    n, Files.size(ser), serSave, serLoad, Files.size(csv), csvSave, csvLoad);
            Files.delete(ser);
            Files.delete(csv);
        }
    }
}
