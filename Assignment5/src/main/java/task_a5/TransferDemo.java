package task_a5;

import common.DbConfig;

import java.sql.*;

/** TASK A5 - shows lost money without a transaction and a safe rollback with one. */
public class TransferDemo {

    public static void main(String[] args) throws Exception {
        try (Connection con = DbConfig.getConnection()) {
            DbConfig.createSchema(con);

            System.out.println("--- 1. Two auto-commit statements, crash between them ---");
            reset(con);
            try { WalletTransfer.transferAutoCommit(con, "HOSTEL", "CANTEEN", 500, true); }
            catch (RuntimeException e) { System.out.println("FAILED: " + e.getMessage()); }
            show(con);

            System.out.println("\n--- 2. One transaction, crash between the updates ---");
            reset(con);
            try { WalletTransfer.transfer(con, "HOSTEL", "CANTEEN", 500, true); }
            catch (RuntimeException e) { System.out.println("FAILED: " + e.getMessage()); }
            show(con);

            System.out.println("\n--- 3. One transaction, no crash ---");
            reset(con);
            WalletTransfer.transfer(con, "HOSTEL", "CANTEEN", 500, false);
            show(con);
        }
    }

    static void reset(Connection con) throws SQLException {
        DbConfig.clear(con, "wallet");
        try (Statement st = con.createStatement()) {
            st.executeUpdate("INSERT INTO wallet(wallet_id, balance) VALUES ('HOSTEL', 1000)");
            st.executeUpdate("INSERT INTO wallet(wallet_id, balance) VALUES ('CANTEEN', 200)");
        }
        System.out.println("start: HOSTEL=1000, CANTEEN=200, total=1200");
    }

    static void show(Connection con) throws SQLException {
        double total = 0;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT wallet_id, balance FROM wallet ORDER BY wallet_id DESC")) {
            StringBuilder sb = new StringBuilder("now  : ");
            while (rs.next()) {
                sb.append(rs.getString(1)).append('=').append(rs.getDouble(2)).append(", ");
                total += rs.getDouble(2);
            }
            System.out.println(sb + "total=" + total + (total == 1200 ? "  (correct)" : "  (MONEY LOST!)"));
        }
    }
}
