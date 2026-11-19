package task_a3;

import common.DbConfig;

import java.sql.Connection;

/** TASK A3 - run the generic DAO. If a column is renamed, only StudentDao changes. */
public class DaoDemo {
    public static void main(String[] args) throws Exception {
        try (Connection con = DbConfig.getConnection()) {
            DbConfig.createSchema(con);
            DbConfig.clear(con, "student");

            Dao<Student, String> dao = new StudentDao(con);   // program to the interface
            dao.insert(new Student("192311001", "Arun", "CSE", 8.4));
            dao.insert(new Student("192311002", "Divya", "IT", 9.1));
            dao.insert(new Student("192311003", "Karthik", "ECE", 7.6));

            System.out.println("findAll:");
            dao.findAll().forEach(s -> System.out.println("  " + s));

            dao.update(new Student("192311003", "Karthik", "CSE", 8.0));
            System.out.println("after update: " + dao.findById("192311003").orElse(null));

            dao.delete("192311002");
            System.out.println("after delete, findById(192311002) present? " + dao.findById("192311002").isPresent());
        }
    }
}
