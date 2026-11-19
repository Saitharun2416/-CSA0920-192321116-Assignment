package task_b3;

import common.DbConfig;
import common.PassGenerator;

import java.sql.*;
import java.util.List;

/** TASK B3.1 - insert 10 records with each approach and verify with SELECT + ResultSetMetaData. */
public class SmallDataCheck {

    public static void main(String[] args) throws Exception {
        List<String[]> ten = PassGenerator.generate(10);
        try (Connection con = DbConfig.getConnection()) {
            DbConfig.createSchema(con);

            DbConfig.clear(con, "bus_pass");
            System.out.println("Statement inserted      : " + InsertMethods.statement(con, ten));
            verify(con);

            DbConfig.clear(con, "bus_pass");
            System.out.println("\nPreparedStatement       : " + InsertMethods.prepared(con, ten));
            verify(con);

            DbConfig.clear(con, "bus_pass");
            System.out.println("\nBatch (single transaction, PassLoader): " + PassLoader.loadAll(con, ten));
            verify(con);
        }
    }

    static void verify(Connection con) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM bus_pass ORDER BY pass_id")) {
            ResultSetMetaData md = rs.getMetaData();
            StringBuilder cols = new StringBuilder("  columns: ");
            for (int i = 1; i <= md.getColumnCount(); i++) {
                cols.append(md.getColumnName(i)).append(" (").append(md.getColumnTypeName(i)).append(")  ");
            }
            System.out.println(cols);
            int rows = 0;
            while (rs.next()) {
                rows++;
                if (rows <= 3) {
                    System.out.println("  " + rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3)
                            + " | " + rs.getInt(4) + " | " + rs.getDouble(5));
                }
            }
            System.out.println("  ... total rows in table = " + rows + (rows == 10 ? "  (correct)" : "  (WRONG)"));
        }
    }
}
