package task_a4;

import java.io.BufferedInputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

/** Reads hostel.ser back. Transient fields come back as null. */
public class HostelLoad {
    public static void main(String[] args) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(HostelSave.FILE)))) {
            Hostel h = (Hostel) in.readObject();
            System.out.println("Loaded " + h.getName());
            for (Room room : h.getRooms()) {
                System.out.println(" Room " + room.getRoomNo());
                for (Resident r : room.getResidents()) {
                    System.out.println("   " + r);
                }
            }
        } catch (InvalidClassException e) {
            System.out.println("VERSION MISMATCH -> " + e);
        } catch (NoSuchFileException e) {
            System.out.println("hostel.ser not found - run HostelSave first.");
        }
    }
}
