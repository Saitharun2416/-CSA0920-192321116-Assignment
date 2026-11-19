package a3.q2;

/** Q2 - scenarios (Student B test table), chaining evidence and lost-cause evidence. */
public class PortalDemo {
    public static void main(String[] args) {
        ExamController c = new ExamController();

        System.out.println("== Scenario table ==");
        System.out.println("1 success       : " + c.submit("S101", "ans1.pdf", 120, false, true));
        System.out.println("2 not registered: " + c.submit("S999", "ans2.pdf", 120, false, true));
        System.out.println("3 too large     : " + c.submit("S101", "ans3.pdf", 900, false, true));
        System.out.println("4 blank name    : " + c.submit("S101", " ", 10, false, true));
        c.service().setWindowOpen(false);
        System.out.println("5 window closed : " + c.submit("S101", "ans5.pdf", 10, false, true));
        c.service().setWindowOpen(true);

        System.out.println("\n== Disk failure WITH cause (full chained stack trace) ==");
        System.out.println("6 -> " + c.submit("S101", "ans6.pdf", 100, true, true));

        System.out.println("\n== Disk failure WITHOUT cause (information lost) ==");
        System.out.println("7 -> " + c.submit("S101", "ans7.pdf", 100, true, false));

        System.out.println("\n== getCause() demo ==");
        try {
            new StorageLayer().save("x.pdf", 10, true, true);
        } catch (ExamException e) {
            System.out.println("message = " + e.getMessage());
            System.out.println("cause   = " + e.getCause());
            System.out.println("root    = " + e.getCause().getMessage());
        }
    }
}
