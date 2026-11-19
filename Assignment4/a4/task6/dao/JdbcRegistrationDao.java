package a4.task6.dao;

import a4.task6.model.Registration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/** MySQL implementation. Schema: sql/schema.sql. All SQL uses PreparedStatement and try-with-resources. */
public class JdbcRegistrationDao implements RegistrationDao {
    private final String url, user, password;

    public JdbcRegistrationDao(String url, String user, String password) { this.url = url; this.user = user; this.password = password; }

    private Connection open() throws SQLException { return DriverManager.getConnection(url, user, password); }

    /** Throws if the database cannot be reached (used by the app to decide on fallback). */
    public void ping() throws SQLException { try (Connection c = open()) { c.isValid(2); } }

    private static boolean noFilter(String e) { return e == null || e.equals("All"); }

    @Override public List<String> events() throws DaoException {
        List<String> list = new ArrayList<>();
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement("SELECT name FROM events ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) { throw new DaoException("Cannot load events", e); }
        return list;
    }

    @Override public void insert(Registration r) throws DuplicateRegistrationException, DaoException {
        String sql = "INSERT INTO registrations(student_id, student_name, email, event_name) VALUES (?,?,?,?)";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.studentId()); ps.setString(2, r.studentName());
            ps.setString(3, r.email());     ps.setString(4, r.eventName());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {              // UNIQUE(student_id, event_name)
            throw new DuplicateRegistrationException(r.studentId() + " is already registered for " + r.eventName());
        } catch (SQLException e) { throw new DaoException("Cannot save registration", e); }
    }

    @Override public List<Registration> page(String event, int limit, int offset) throws DaoException {
        String sql = "SELECT student_id, student_name, email, event_name FROM registrations "
                + (noFilter(event) ? "" : "WHERE event_name = ? ") + "ORDER BY id LIMIT ? OFFSET ?";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            if (!noFilter(event)) ps.setString(i++, event);
            ps.setInt(i++, limit); ps.setInt(i, offset);
            return read(ps);
        } catch (SQLException e) { throw new DaoException("Cannot load registrations", e); }
    }

    @Override public int count(String event) throws DaoException {
        String sql = "SELECT COUNT(*) FROM registrations" + (noFilter(event) ? "" : " WHERE event_name = ?");
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (!noFilter(event)) ps.setString(1, event);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        } catch (SQLException e) { throw new DaoException("Cannot count registrations", e); }
    }

    @Override public List<Registration> all(String event) throws DaoException {
        String sql = "SELECT student_id, student_name, email, event_name FROM registrations "
                + (noFilter(event) ? "" : "WHERE event_name = ? ") + "ORDER BY id";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (!noFilter(event)) ps.setString(1, event);
            return read(ps);
        } catch (SQLException e) { throw new DaoException("Cannot export registrations", e); }
    }

    private static List<Registration> read(PreparedStatement ps) throws SQLException {
        List<Registration> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Registration(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
        }
        return list;
    }
}
