package task_b3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/** The three insertion approaches compared in Task B3 (all with auto-commit ON). */
public final class InsertMethods {
    private InsertMethods() { }

    private static final String SQL = "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES (?, ?, ?, ?, ?)";

    /** (a) Statement: builds and parses a new SQL string for every row. */
    public static int statement(Connection con, List<String[]> passes) throws SQLException {
        con.setAutoCommit(true);
        int n = 0;
        try (Statement st = con.createStatement()) {
            for (String[] p : passes) {
                st.executeUpdate("INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES ('"
                        + p[0] + "','" + p[1] + "','" + p[2] + "'," + p[3] + "," + p[4] + ")");
                n++;
            }
        }
        return n;
    }

    /** (b) PreparedStatement: SQL compiled once, one round trip per row. */
    public static int prepared(Connection con, List<String[]> passes) throws SQLException {
        con.setAutoCommit(true);
        int n = 0;
        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            for (String[] p : passes) {
                bind(ps, p);
                ps.executeUpdate();
                n++;
            }
        }
        return n;
    }

    /** (c) PreparedStatement + addBatch()/executeBatch(), auto-commit ON (each batch commits by itself). */
    public static int batchAutoCommit(Connection con, List<String[]> passes) throws SQLException {
        con.setAutoCommit(true);
        int n = 0;
        try (PreparedStatement ps = con.prepareStatement(SQL)) {
            for (String[] p : passes) {
                bind(ps, p);
                ps.addBatch();
                n++;
                if (n % 1000 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        }
        return n;
    }

    private static void bind(PreparedStatement ps, String[] p) throws SQLException {
        ps.setString(1, p[0]);
        ps.setString(2, p[1]);
        ps.setString(3, p[2]);
        ps.setInt(4, Integer.parseInt(p[3]));
        ps.setDouble(5, Double.parseDouble(p[4]));
    }
}
