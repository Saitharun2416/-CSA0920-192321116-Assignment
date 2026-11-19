package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * One place to configure the database for every JDBC task.
 *
 *  USE_H2 = true  -> runs immediately with an in-memory H2 database (no installation).
 *  USE_H2 = false -> uses your local MySQL server (required for the final submission of B3/B4).
 */
public final class DbConfig {

    public static final boolean USE_H2 = true;          // <-- change to false for MySQL

    // ---- MySQL settings: edit user / password to match your MySQL installation ----
    public static final String MYSQL_URL =
            "jdbc:mysql://localhost:3306/busdb?createDatabaseIfNotExist=true"
                    + "&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
                    + "&rewriteBatchedStatements=true";
    public static final String MYSQL_USER = "root";
    public static final String MYSQL_PASSWORD = "root";

    // ---- H2 settings (in-memory, survives as long as the JVM runs) ----
    public static final String H2_URL = "jdbc:h2:mem:busdb;DB_CLOSE_DELAY=-1";

    private DbConfig() { }

    public static Connection getConnection() throws SQLException {
        if (USE_H2) {
            return DriverManager.getConnection(H2_URL);
        }
        return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
    }

    /** Creates all tables used by the assignment (safe to call many times). */
    public static void createSchema(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS bus_pass ("
                    + "pass_id VARCHAR(12) PRIMARY KEY, reg_no VARCHAR(15) NOT NULL, "
                    + "name VARCHAR(60) NOT NULL, route_no INT NOT NULL, amount DOUBLE NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id VARCHAR(20) PRIMARY KEY, pwd VARCHAR(50) NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS student ("
                    + "reg_no VARCHAR(15) PRIMARY KEY, name VARCHAR(60), dept VARCHAR(30), cgpa DOUBLE)");
            st.execute("CREATE TABLE IF NOT EXISTS wallet ("
                    + "wallet_id VARCHAR(20) PRIMARY KEY, balance DOUBLE NOT NULL)");
        }
    }

    public static int count(Connection con, String table) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public static void clear(Connection con, String table) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM " + table);
        }
    }
}
