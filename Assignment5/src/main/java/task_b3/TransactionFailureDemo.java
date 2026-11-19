package task_b3;

import common.DbConfig;
import common.PassGenerator;

import java.sql.Connection;
import java.util.List;

/**
 * TASK B3.3 / loadAll() requirement - force a failure half-way (an invalid amount) and show the table contents.
 *  - auto-commit batch : batches already executed stay in the table (partial data)
 *  - one transaction   : rollback leaves ZERO rows
 */
public class TransactionFailureDemo {

    public static void main(String[] args) throws Exception {
        int n = 3000;
        List<String[]> data = PassGenerator.generate(n);
        data.get(n / 2)[4] = "not-a-number";               // invalid amount exactly half-way (row 1500)

        try (Connection con = DbConfig.getConnection()) {
            DbConfig.createSchema(con);

            DbConfig.clear(con, "bus_pass");
            try {
                InsertMethods.batchAutoCommit(con, data);
            } catch (Exception e) {
                System.out.println("AUTO-COMMIT batch failed : " + e);
            }
            System.out.println("  rows left in table     : " + DbConfig.count(con, "bus_pass")
                    + "   <-- partial, half-loaded data (earlier batches were committed)");

            DbConfig.clear(con, "bus_pass");
            try {
                PassLoader.loadAll(con, data);
            } catch (Exception e) {
                System.out.println("\nTRANSACTION loadAll failed: " + e);
            }
            System.out.println("  rows left in table     : " + DbConfig.count(con, "bus_pass")
                    + "   <-- all-or-nothing: rollback removed the 1,000 rows already sent");
            System.out.println("  auto-commit restored to: " + con.getAutoCommit());
        }
    }
}
