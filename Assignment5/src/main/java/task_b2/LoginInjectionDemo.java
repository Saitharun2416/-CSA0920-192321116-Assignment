package task_b2;

import common.DbConfig;

import java.sql.*;
import java.util.regex.Pattern;

/**
 * TASK B2 - SQL injection against the unsafe login (A5.2), the PreparedStatement fix,
 * and the weaker alternatives (quote escaping / regex validation).
 * Uses ONLY the local test database of this assignment.
 */
public class LoginInjectionDemo {

    /** UNSAFE: user input is concatenated into the SQL text. */
    static boolean unsafeLogin(Connection con, String id, String pwd) throws SQLException {
        String sql = "SELECT * FROM users WHERE id='" + id + "' AND pwd='" + pwd + "'";
        System.out.println("      SQL sent -> " + sql);
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }

    /** SAFE: the SQL text is fixed; input is only ever bound as DATA. */
    static boolean safeLogin(Connection con, String id, String pwd) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? AND pwd = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, pwd);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ---------- weaker alternatives ----------
    static String escapeQuotes(String s) {
        return s.replace("'", "''");
    }

    static final Pattern SAFE_NUMBER = Pattern.compile("[0-9]+");

    public static void main(String[] args) throws Exception {
        try (Connection con = DbConfig.getConnection()) {
            DbConfig.createSchema(con);
            DbConfig.clear(con, "users");
            try (Statement st = con.createStatement()) {
                st.executeUpdate("INSERT INTO users(id, pwd) VALUES ('admin', 'S3cret#123')");
            }

            String[][] cases = {
                    {"valid credentials", "admin", "S3cret#123"},
                    {"wrong password", "admin", "wrong"},
                    {"INJECTION in password", "admin", "' OR '1'='1"},
                    {"INJECTION comment in id", "admin' -- ", "anything"},
            };

            System.out.println("================ UNSAFE (string concatenation) ================");
            for (String[] c : cases) {
                System.out.println("  case: " + c[0]);
                System.out.println("      login accepted = " + unsafeLogin(con, c[1], c[2]));
            }

            System.out.println("\n================ SAFE (PreparedStatement) ================");
            for (String[] c : cases) {
                System.out.println("  case: " + c[0] + "  -> login accepted = " + safeLogin(con, c[1], c[2]));
            }

            // ---------- weaker defence 1: escaping quotes ----------
            System.out.println("\n================ WEAKER: escaping quotes ================");
            System.out.println("  quote injection blocked? -> accepted = "
                    + unsafeLogin(con, "admin", escapeQuotes("' OR '1'='1")));

            DbConfig.clear(con, "bus_pass");
            try (Statement st = con.createStatement()) {
                st.executeUpdate("INSERT INTO bus_pass VALUES ('P1','1','A',1,100)");
                st.executeUpdate("INSERT INTO bus_pass VALUES ('P2','2','B',2,100)");
                st.executeUpdate("INSERT INTO bus_pass VALUES ('P3','3','C',3,100)");
            }
            String routeInput = "0 OR 1=1";                 // numeric context: NO quote characters needed
            String sql = "SELECT COUNT(*) FROM bus_pass WHERE route_no = " + escapeQuotes(routeInput);
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                rs.next();
                System.out.println("  numeric input '" + routeInput + "' passes the escaping, SQL = " + sql);
                System.out.println("  rows returned = " + rs.getInt(1) + " (should be 0) -> escaping is NOT enough");
            }

            // ---------- weaker defence 2: regex validation ----------
            System.out.println("\n================ WEAKER: regex whitelist ================");
            System.out.println("  '0 OR 1=1' valid? " + SAFE_NUMBER.matcher(routeInput).matches()
                    + "   '12' valid? " + SAFE_NUMBER.matcher("12").matches());
            System.out.println("  Works for a strict numeric field, but a NAME like O'Brien or a password with symbols"
                    + " cannot be whitelisted this tightly.");

            // ---------- time cost of validation vs PreparedStatement ----------
            int reps = 100_000;
            long t = System.nanoTime();
            for (int i = 0; i < reps; i++) {
                SAFE_NUMBER.matcher("12345").matches();
            }
            double regexUs = (System.nanoTime() - t) / 1e3 / reps;
            System.out.printf("%n  regex validation   : %.3f microseconds per check (O(length of input))%n", regexUs);

            t = System.nanoTime();
            for (int i = 0; i < 2000; i++) {
                safeLogin(con, "admin", "wrong");
            }
            double psUs = (System.nanoTime() - t) / 1e3 / 2000;
            System.out.printf("  PreparedStatement  : %.3f microseconds per complete query (includes database round trip)%n", psUs);
        }
    }
}
