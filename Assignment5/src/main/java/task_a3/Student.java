package task_a3;

/**
 * TASK A3.3 - object -> table mapping
 *
 *   Student field   | student column | type            | note
 *   ----------------+----------------+-----------------+-------------
 *   regNo           | reg_no         | VARCHAR(15)     | PRIMARY KEY
 *   name            | name           | VARCHAR(60)     |
 *   dept            | dept           | VARCHAR(30)     |
 *   cgpa            | cgpa           | DOUBLE          |
 */
public class Student {
    private final String regNo;
    private final String name;
    private final String dept;
    private final double cgpa;

    public Student(String regNo, String name, String dept, double cgpa) {
        this.regNo = regNo;
        this.name = name;
        this.dept = dept;
        this.cgpa = cgpa;
    }

    public String getRegNo() { return regNo; }
    public String getName() { return name; }
    public String getDept() { return dept; }
    public double getCgpa() { return cgpa; }

    @Override
    public String toString() {
        return "Student{" + regNo + ", " + name + ", " + dept + ", cgpa=" + cgpa + "}";
    }
}
