package task_b1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

/** TASK B1.1 - test table: normal case, missing id, damaged line, list, missing file. */
public class PassStoreTest {

    static int passed = 0, failed = 0;

    static void check(String testName, String verifies, boolean ok, String actual) {
        System.out.printf("%-4s | %-34s | %s%n       verifies: %s%n", ok ? "PASS" : "FAIL", testName, actual, verifies);
        if (ok) passed++; else failed++;
    }

    public static void main(String[] args) throws IOException {
        Path f = Files.createTempFile("passes", ".csv");
        PassStore store = new PassStore(f);
        store.add("P1001", "192311001", "Arun", 12, 4500);
        store.add("P1002", "192311002", "Meena", 7, 3800);
        Files.writeString(f, "P1003,DAMAGED LINE\n", StandardOpenOption.APPEND);   // damaged: 2 fields
        Files.writeString(f, "\n", StandardOpenOption.APPEND);                      // damaged: empty line
        store.add("P1004", "192311004", "Divya", 12, 4100);

        String[] r = store.find("P1001");
        check("T1 normal find", "add() wrote a line that find() can read back",
                r != null && r[2].equals("Arun"), Arrays.toString(r));

        r = store.find("P9999");
        check("T2 pass id does not exist", "find() returns null (not an exception) when absent",
                r == null, Arrays.toString(r));

        r = store.find("P1003");
        check("T3 damaged line itself", "a damaged line is never returned as a pass",
                r == null, Arrays.toString(r));

        r = store.find("P1004");
        check("T4 pass AFTER damaged lines", "damaged lines do not stop the search",
                r != null && r[2].equals("Divya"), Arrays.toString(r));

        List<String[]> route12 = PassFileOps.listByRoute(f, 12);
        check("T5 list passes on route 12", "listByRoute returns only valid passes of that route",
                route12.size() == 2, route12.size() + " passes");

        Files.delete(f);
        try {
            store.find("P1001");
            check("T6 missing file", "missing file is reported", false, "no exception");
        } catch (NoSuchFileException e) {
            check("T6 missing file", "missing file raises NoSuchFileException for the caller to handle",
                    true, "NoSuchFileException: " + e.getMessage());
        }

        System.out.println("\nPassed " + passed + ", failed " + failed);
    }
}
