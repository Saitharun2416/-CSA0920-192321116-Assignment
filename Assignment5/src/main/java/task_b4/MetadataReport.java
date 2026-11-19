package task_b4;

import common.DbConfig;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * TASK B4.2 - report database product, driver version and tables with DatabaseMetaData.
 *
 * Why it is useful when the application moves to another server: the program can confirm it is connected
 * to the expected product/version, check that the JDBC driver is compatible, and verify that the required
 * tables exist BEFORE running a migration, instead of failing half-way with a confusing SQL error.
 */
public class MetadataReport {

    public static void main(String[] args) throws Exception {
        try (Connection con = DbConfig.getConnection()) {
            DbConfig.createSchema(con);
            DatabaseMetaData md = con.getMetaData();

            System.out.println("Database product : " + md.getDatabaseProductName() + " " + md.getDatabaseProductVersion());
            System.out.println("Driver           : " + md.getDriverName() + " " + md.getDriverVersion());
            System.out.println("JDBC URL         : " + md.getURL());
            System.out.println("User             : " + md.getUserName());
            System.out.println("Supports batch updates : " + md.supportsBatchUpdates());
            System.out.println("Supports transactions  : " + md.supportsTransactions());

            System.out.println("\nTables present:");
            try (ResultSet rs = md.getTables(con.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    System.out.println("  " + rs.getString("TABLE_NAME"));
                }
            }

            System.out.println("\nColumns of bus_pass:");
            for (String tableName : new String[]{"bus_pass", "BUS_PASS"}) {
                try (ResultSet rs = md.getColumns(con.getCatalog(), null, tableName, "%")) {
                    while (rs.next()) {
                        System.out.println("  " + rs.getString("COLUMN_NAME") + "  " + rs.getString("TYPE_NAME")
                                + "(" + rs.getInt("COLUMN_SIZE") + ")");
                    }
                }
            }
        }
    }
}
