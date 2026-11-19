package task_a1;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TASK A1.1(b) - binary layout written with DataOutputStream.
 *
 * Record layout (variable length, no delimiter needed, so a name containing a comma is safe):
 *   writeUTF(passId) | writeUTF(regNo) | writeUTF(name) | writeInt(route)
 *   | writeUTF(stop) | writeUTF(validity) | writeDouble(amount)
 */
public class BinaryPassFile {

    public static void write(Path file, String id, String reg, String name, int route,
                             String stop, String validity, double amount) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                Files.newOutputStream(file, java.nio.file.StandardOpenOption.CREATE,
                        java.nio.file.StandardOpenOption.APPEND)))) {
            out.writeUTF(id);
            out.writeUTF(reg);
            out.writeUTF(name);
            out.writeInt(route);
            out.writeUTF(stop);
            out.writeUTF(validity);
            out.writeDouble(amount);
        }
    }

    public static void readAll(Path file) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            while (true) {
                try {
                    String id = in.readUTF();
                    String reg = in.readUTF();
                    String name = in.readUTF();
                    int route = in.readInt();
                    String stop = in.readUTF();
                    String validity = in.readUTF();
                    double amount = in.readDouble();
                    System.out.println(id + " | " + reg + " | " + name + " | route " + route
                            + " | " + stop + " | " + validity + " | " + amount);
                } catch (EOFException endOfFile) {
                    break;               // normal end of data
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Path f = Files.createTempFile("passes", ".bin");
        write(f, "P1001", "192311001", "Arun", 12, "Sriperumbudur", "2027-03-31", 4500);
        write(f, "P1002", "192311002", "Meena, K", 7, "Guindy", "2027-03-31", 3800);  // comma in name is safe
        readAll(f);
        System.out.println("Binary file size = " + Files.size(f) + " bytes");
        Files.delete(f);
    }
}
