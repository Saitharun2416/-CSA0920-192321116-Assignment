package task_a4;

import common.DbConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

/** Saves the whole Hostel -> Room -> Resident object graph with ONE writeObject() call. */
public class HostelSave {
    public static final Path FILE = Path.of("hostel.ser");

    public static void main(String[] args) throws Exception {
        try (Connection con = DbConfig.getConnection()) {           // a live connection held by residents
            Hostel h = new Hostel("Boys Hostel A");
            Room r1 = new Room(101);
            r1.add(new Resident("Arun", "192311001", "secret1", con));
            r1.add(new Resident("Divya", "192311002", "secret2", con));
            Room r2 = new Room(102);
            r2.add(new Resident("Karthik", "192311003", "secret3", con));
            h.add(r1);
            h.add(r2);

            try (ObjectOutputStream out = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(FILE)))) {
                out.writeObject(h);          // serializes rooms and residents recursively
            }
            System.out.println("Saved hostel to " + FILE.toAbsolutePath() + " (" + Files.size(FILE) + " bytes)");
        }
    }
}
