package task_a3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** The ONLY place that knows the SQL and column names of the student table. */
public class StudentDao implements Dao<Student, String> {

    private static final String INSERT = "INSERT INTO student(reg_no, name, dept, cgpa) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ONE = "SELECT reg_no, name, dept, cgpa FROM student WHERE reg_no = ?";
    private static final String SELECT_ALL = "SELECT reg_no, name, dept, cgpa FROM student ORDER BY reg_no";
    private static final String UPDATE = "UPDATE student SET name = ?, dept = ?, cgpa = ? WHERE reg_no = ?";
    private static final String DELETE = "DELETE FROM student WHERE reg_no = ?";

    private final Connection con;

    public StudentDao(Connection con) {
        this.con = con;
    }

    /** The repeated "read ResultSet into object" step lives in exactly one method. */
    private Student map(ResultSet rs) throws SQLException {
        return new Student(rs.getString("reg_no"), rs.getString("name"),
                rs.getString("dept"), rs.getDouble("cgpa"));
    }

    @Override
    public void insert(Student s) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            ps.setString(1, s.getRegNo());
            ps.setString(2, s.getName());
            ps.setString(3, s.getDept());
            ps.setDouble(4, s.getCgpa());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Student> findById(String regNo) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SELECT_ONE)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public void update(Student s) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getDept());
            ps.setDouble(3, s.getCgpa());
            ps.setString(4, s.getRegNo());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String regNo) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(DELETE)) {
            ps.setString(1, regNo);
            ps.executeUpdate();
        }
    }
}
