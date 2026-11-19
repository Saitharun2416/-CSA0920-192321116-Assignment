package task_a1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * TASK A1 - Alternative concept: RandomAccessFile with FIXED-LENGTH records.
 * Record i starts at byte i * RECORD_SIZE, so a record is found with ONE seek: O(1).
 * Price: padding wastes space.
 */
public class FixedRecordFile implements AutoCloseable {

    static final int ID = 8, REG = 12, NAME = 30;
    static final int RECORD_SIZE = ID + REG + NAME + 4 + 8;   // 62 bytes

    private final RandomAccessFile raf;

    public FixedRecordFile(Path file) throws IOException {
        this.raf = new RandomAccessFile(file.toFile(), "rw");
    }

    private static void putPadded(ByteBuffer b, String s, int len) {
        byte[] field = new byte[len];
        Arrays.fill(field, (byte) ' ');
        byte[] src = s.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(src, 0, field, 0, Math.min(src.length, len));
        b.put(field);
    }

    private static String getPadded(ByteBuffer b, int len) {
        byte[] field = new byte[len];
        b.get(field);
        return new String(field, StandardCharsets.UTF_8).trim();
    }

    public void append(String id, String reg, String name, int route, double amount) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(RECORD_SIZE);
        putPadded(b, id, ID);
        putPadded(b, reg, REG);
        putPadded(b, name, NAME);
        b.putInt(route);
        b.putDouble(amount);
        raf.seek(raf.length());
        raf.write(b.array());
    }

    public long count() throws IOException {
        return raf.length() / RECORD_SIZE;
    }

    /** O(1): one seek + one read. */
    public String[] readAt(long index) throws IOException {
        if (index < 0 || index >= count()) {
            return null;
        }
        raf.seek(index * RECORD_SIZE);
        byte[] rec = new byte[RECORD_SIZE];
        raf.readFully(rec);
        ByteBuffer b = ByteBuffer.wrap(rec);
        return new String[]{getPadded(b, ID), getPadded(b, REG), getPadded(b, NAME),
                String.valueOf(b.getInt()), String.valueOf(b.getDouble())};
    }

    /** O(n): scan every record (what a delimited text file must do). */
    public String[] findByScan(String id) throws IOException {
        for (long i = 0; i < count(); i++) {
            String[] r = readAt(i);
            if (r[0].equals(id)) {
                return r;
            }
        }
        return null;
    }

    /** O(1): works because ids are sequential (P1001 -> record 0, P1002 -> record 1 ...). */
    public String[] findBySeek(String id) throws IOException {
        long index = Long.parseLong(id.substring(1)) - 100000;
        String[] r = readAt(index);
        return (r != null && r[0].equals(id)) ? r : null;
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }

    public static void main(String[] args) throws IOException {
        Path f = Files.createTempFile("passes", ".dat");
        int n = 100_000;
        long textBytes = 0;
        try (FixedRecordFile ff = new FixedRecordFile(f)) {
            for (int i = 0; i < n; i++) {
                String[] p = common.PassGenerator.pass(i);
                ff.append(p[0], p[1], p[2], Integer.parseInt(p[3]), Double.parseDouble(p[4]));
                textBytes += String.join(",", p).length() + 1;
            }
            String target = "P" + (100000 + n - 1);       // worst case: last record

            long t = System.nanoTime();
            String[] a = ff.findByScan(target);
            double scanMs = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            String[] b = ff.findBySeek(target);
            double seekMs = (System.nanoTime() - t) / 1e6;

            System.out.println("Scan  O(n): " + Arrays.toString(a) + "  " + scanMs + " ms");
            System.out.println("Seek  O(1): " + Arrays.toString(b) + "  " + seekMs + " ms");
            System.out.println("Fixed-length file : " + Files.size(f) + " bytes (" + RECORD_SIZE + " bytes/record)");
            System.out.println("Delimited text    : " + textBytes + " bytes");
            System.out.println("Space wasted by padding = " + (Files.size(f) - textBytes) + " bytes");
        }
        Files.delete(f);
    }
}
