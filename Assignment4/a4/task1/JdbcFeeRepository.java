package a4.task1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Real JDBC version (not used by default). Table: fees(reg_no VARCHAR(20), amount DOUBLE).
 * try-with-resources closes the connection; PreparedStatement prevents SQL injection.
 */
public class JdbcFeeRepository implements FeeRepository {
    private final String url, user, pwd;
    public JdbcFeeRepository(String url, String user, String pwd) { this.url = url; this.user = user; this.pwd = pwd; }

    @Override public void save(String regNo, double amount) throws Exception {
        try (Connection c = DriverManager.getConnection(url, user, pwd);
             PreparedStatement ps = c.prepareStatement("INSERT INTO fees(reg_no, amount) VALUES (?, ?)")) {
            ps.setString(1, regNo);
            ps.setDouble(2, amount);
            ps.executeUpdate();
        }
    }
}
