package task_b3;

import common.DbConfig;
import common.PassGenerator;

import java.sql.Connection;
import java.util.List;

/**
 * TASK B3.2 - insert 1,000 / 10,000 / 100,000 records with:
 *   (a) Statement  (b) PreparedStatement  (c) batch, auto-commit ON  (d) batch inside ONE transaction (PassLoader).
 * With MySQL, (a) and (b) at 100,000 rows can take several minutes - this is the result the task wants you to observe.
 */
public class JdbcBenchmark {

    public static void main(String[] args) throws Exception {
        int[] sizes = {1_000, 10_000, 100_000};
        System.out.println("Database: " + (DbConfig.USE_H2 ? "H2 (in-memory)" : "MySQL"));
        System.out.printf("%-10s %-16s %-18s %-20s %-22s%n",
                "rows", "Statement ms", "Prepared ms", "Batch autocommit ms", "Batch 1 transaction ms");

        try (Connection con = DbConfig.getConnection()) {
            DbConfig.createSchema(con);
            for (int n : sizes) {
                List<String[]> data = PassGenerator.generate(n);

                double a = run(con, data, 0);
                double b = run(con, data, 1);
                double c = run(con, data, 2);
                double d = run(con, data, 3);

                System.out.printf("%-10d %-16.1f %-18.1f %-20.1f %-22.1f%n", n, a, b, c, d);
            }
        }
    }

    static double run(Connection con, List<String[]> data, int method) throws Exception {
        DbConfig.clear(con, "bus_pass");
        long t = System.nanoTime();
        int inserted;
        switch (method) {
            case 0: inserted = InsertMethods.statement(con, data); break;
            case 1: inserted = InsertMethods.prepared(con, data); break;
            case 2: inserted = InsertMethods.batchAutoCommit(con, data); break;
            default: inserted = PassLoader.loadAll(con, data); break;
        }
        double ms = (System.nanoTime() - t) / 1e6;
        int inTable = DbConfig.count(con, "bus_pass");
        if (inserted != data.size() || inTable != data.size()) {
            System.out.println("  !! verification failed for method " + method + ": inserted=" + inserted + ", table=" + inTable);
        }
        return ms;
    }
}
