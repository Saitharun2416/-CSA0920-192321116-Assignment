package task_b4;

import common.DbConfig;
import common.PassValidator;
import task_b3.PassLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TASK B4.1 - migrate the existing pass file into the database using batches + ONE transaction,
 * and report every record that failed validation (line number, content, reason).
 */
public class MigrationTool {

    public static void main(String[] args) throws Exception {
        Path source = Path.of("legacy_passes.csv");
        Path report = Path.of("migration_failures.txt");
        if (args.length > 0) {
            source = Path.of(args[0]);
        } else {
            createSampleLegacyFile(source);
        }

        List<String[]> valid = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        int lineNo = 0;
        try (BufferedReader r = Files.newBufferedReader(source)) {
            String line;
            while ((line = r.readLine()) != null) {
                lineNo++;
                String problem = PassValidator.validate(line);
                if (problem == null) {
                    String[] f = line.split(",", -1);
                    if (!seen.add(f[0])) {                        // a duplicate would violate the primary key
                        problem = "duplicate pass id " + f[0];
                    } else {
                        valid.add(f);
                    }
                }
                if (problem != null) {
                    failures.add("line " + lineNo + ": " + problem + "   [" + line + "]");
                }
            }
        }

        System.out.println("Read " + lineNo + " lines: " + valid.size() + " valid, " + failures.size() + " rejected");

        try (Connection con = DbConfig.getConnection()) {
            DbConfig.createSchema(con);
            DbConfig.clear(con, "bus_pass");
            long t = System.nanoTime();
            int loaded = PassLoader.loadAll(con, valid);          // batches of 1,000 inside one transaction
            double ms = (System.nanoTime() - t) / 1e6;
            System.out.printf("Loaded %d rows into bus_pass in %.1f ms (rows now in table: %d)%n",
                    loaded, ms, DbConfig.count(con, "bus_pass"));
        }

        try (BufferedWriter w = Files.newBufferedWriter(report)) {
            for (String f : failures) {
                w.write(f);
                w.newLine();
            }
        }
        System.out.println("\n=== FAILURE REPORT (also saved to " + report.toAbsolutePath() + ") ===");
        failures.forEach(System.out::println);
    }

    /** Builds a sample "legacy" file: 5,000 good lines plus deliberately bad ones. */
    static void createSampleLegacyFile(Path file) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file)) {
            for (int i = 0; i < 5000; i++) {
                w.write(String.join(",", common.PassGenerator.pass(i)));
                w.newLine();
                if (i == 1000) { w.write("P777777,192399999,Broken,7"); w.newLine(); }              // 4 fields
                if (i == 2000) { w.write("P888888,192388888,BadAmount,7,ABC"); w.newLine(); }        // bad amount
                if (i == 3000) { w.newLine(); }                                                      // empty line
                if (i == 4000) { w.write("P100005,192300005,Duplicate,3,3500.0"); w.newLine(); }     // duplicate id
            }
        }
        System.out.println("Created sample legacy file " + file.toAbsolutePath());
    }
}
