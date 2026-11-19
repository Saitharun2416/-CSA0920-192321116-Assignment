package task_a5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** TASK A5.1 - money transfer with and without a transaction. */
public class WalletTransfer {

    private static final String DEBIT  = "UPDATE wallet SET balance = balance - ? WHERE wallet_id = ? AND balance >= ?";
    private static final String CREDIT = "UPDATE wallet SET balance = balance + ? WHERE wallet_id = ?";

    /** One transaction: both updates succeed or neither does. */
    public static void transfer(Connection con, String from, String to, double amount, boolean crashBetween)
            throws SQLException {
        boolean old = con.getAutoCommit();
        con.setAutoCommit(false);                                   // BEGIN
        try {
            update(con, DEBIT, amount, from, amount, true);
            if (crashBetween) {
                throw new IllegalStateException("simulated power failure between the two updates");
            }
            update(con, CREDIT, amount, to, 0, false);
            con.commit();                                           // COMMIT
        } catch (SQLException | RuntimeException e) {
            con.rollback();                                         // ROLLBACK: undo the debit
            throw e;
        } finally {
            con.setAutoCommit(old);
        }
    }

    /** Alternative concept: two independent auto-commit statements. */
    public static void transferAutoCommit(Connection con, String from, String to, double amount, boolean crashBetween)
            throws SQLException {
        con.setAutoCommit(true);                                    // every statement commits immediately
        update(con, DEBIT, amount, from, amount, true);             // committed forever
        if (crashBetween) {
            throw new IllegalStateException("simulated power failure between the two updates");
        }
        update(con, CREDIT, amount, to, 0, false);
    }

    private static void update(Connection con, String sql, double amount, String id, double min, boolean debit)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, id);
            if (debit) {
                ps.setDouble(3, min);
            }
            if (ps.executeUpdate() != 1) {
                throw new SQLException("wallet not found or insufficient balance: " + id);
            }
        }
    }
}
