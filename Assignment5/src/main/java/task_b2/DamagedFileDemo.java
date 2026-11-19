package task_b2;

import common.PassValidator;
import task_b1.PassFileOps;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** TASK B2 - a deliberately damaged bus-pass file and how the file store responds. */
public class DamagedFileDemo {

    public static void main(String[] args) throws IOException {
        Path f = Files.createTempFile("damaged", ".csv");
        Files.writeString(f, String.join("\n",
                "P1001,192311001,Arun,12,4500.0",            // 1 valid
                "P1002,192311002,Meena,7",                   // 2 wrong number of fields (4)
                "P1003,192311003,Divya,7,ABC",               // 3 non-numeric amount
                "",                                          // 4 empty line
                "P1005,192311005,Karthik,SEVEN,3900.0",      // 5 non-numeric route
                "P1006,192311006,Kavya,5,3900.0") + "\n");   // 6 valid

        System.out.println("=== Line-by-line validation (what a strict reader should report) ===");
        try (BufferedReader r = Files.newBufferedReader(f)) {
            String line;
            int n = 0;
            while ((line = r.readLine()) != null) {
                n++;
                String problem = PassValidator.validate(line);
                System.out.printf("line %d: %-40s -> %s%n", n, "[" + line + "]", problem == null ? "OK" : "REJECT: " + problem);
            }
        }

        System.out.println("\n=== Behaviour of PassStore.find() (checks only the field COUNT) ===");
        // PassStore has a package-private constructor, so a public wrapper is used:
        task_b1.PassStoreAccess store = new task_b1.PassStoreAccess(f);
        for (String id : new String[]{"P1001", "P1002", "P1003", "P1005", "P1006"}) {
            System.out.println("find(" + id + ") = " + Arrays.toString(store.find(id)));
        }

        System.out.println("\nlistByRoute(7) returns " + PassFileOps.listByRoute(f, 7).size() + " pass(es)");
        System.out.println("\nWhat this reveals: P1003 (amount 'ABC') is RETURNED by find() because it has 5 fields."
                + "\nThe original design validates structure but not content; PassValidator closes that gap.");
        Files.delete(f);
    }
}
