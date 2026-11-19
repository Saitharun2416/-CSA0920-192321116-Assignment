package q3;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Q3 - Complete the Code. TODO 1 is completed below.
 * To record the "before" output: comment out the hashCode() method, run (prints 3), then restore it (prints 2).
 */
class Student {
    private final String regNo;
    private final String name;
    Student(String regNo, String name) { this.regNo = regNo; this.name = name; }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return regNo.equals(((Student) o).regNo);
    }
    // TODO 1: Override hashCode() so that it is consistent with equals()
    @Override public int hashCode() { return Objects.hash(regNo); }   // uses ONLY the field used in equals()
    @Override public String toString() { return regNo + " " + name; }
}

public class Attendance {
    public static void main(String[] args) {
        Set<Student> present = new HashSet<>();
        present.add(new Student("192311001", "Arun")); // scanned at Gate 1
        present.add(new Student("192311002", "Divya"));
        present.add(new Student("192311001", "Arun")); // scanned again at Gate 3
        System.out.println("Unique students: " + present.size());
    }
}
