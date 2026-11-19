package task_b4;

import common.DbConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TASK B4.3 - several counters issuing passes at the same time against ONE shared database.
 * The database (not the application) guarantees that no pass ID is issued twice.
 */
public class ConcurrentCountersDemo {

    public static void main(String[] args) throws Exception {
        try (Connection setup = DbConfig.getConnection()) {
            DbConfig.createSchema(setup);
            DbConfig.clear(setup, "bus_pass");
        }

        // Part 1: 4 counters, each with its own connection, each issuing 250 DIFFERENT passes
        List<Thread> threads = new ArrayList<>();
        for (int c = 0; c < 4; c++) {
            final int counter = c;
            Thread t = new Thread(() -> {
                try (Connection con = DbConfig.getConnection();
                     PreparedStatement ps = con.prepareStatement(
                             "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES (?,?,?,?,?)")) {
                    for (int i = 0; i < 250; i++) {
                        ps.setString(1, "C" + counter + "-" + i);
                        ps.setString(2, "1923" + counter + i);
                        ps.setString(3, "Student");
                        ps.setInt(4, counter + 1);
                        ps.setDouble(5, 3500);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.out.println("counter " + counter + " error: " + e.getMessage());
                }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) t.join();
        try (Connection con = DbConfig.getConnection()) {
            System.out.println("Part 1: 4 counters x 250 passes -> rows in table = " + DbConfig.count(con, "bus_pass")
                    + " (expected 1000)");
        }

        // Part 2: all 4 counters try to issue the SAME pass id at the same moment
        AtomicInteger success = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();
        threads.clear();
        for (int c = 0; c < 4; c++) {
            Thread t = new Thread(() -> {
                try (Connection con = DbConfig.getConnection();
                     PreparedStatement ps = con.prepareStatement(
                             "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES ('P-SAME','1','X',1,100)")) {
                    ps.executeUpdate();
                    success.incrementAndGet();
                } catch (SQLException e) {
                    rejected.incrementAndGet();            // duplicate primary key
                }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) t.join();
        System.out.println("Part 2: same pass id from 4 counters -> accepted = " + success.get()
                + ", rejected by PRIMARY KEY = " + rejected.get() + " (expected 1 and 3)");
    }
}
