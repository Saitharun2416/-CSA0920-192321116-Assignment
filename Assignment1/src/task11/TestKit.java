package task11;

import java.util.function.Supplier;

/** Minimal test table printer (no JUnit needed). */
public class TestKit {
    private int pass, fail;
    public void check(String name, Object expected, Supplier<Object> action) {
        Object actual;
        try { actual = action.get(); } catch (RuntimeException e) { actual = "EXC:" + e.getClass().getSimpleName(); }
        report(name, String.valueOf(expected), String.valueOf(actual));
    }
    /** Expects an exception whose message contains the given text. */
    public void expectError(String name, String messagePart, Runnable action) {
        String actual;
        try { action.run(); actual = "no exception"; } catch (RuntimeException e) { actual = e.getMessage(); }
        boolean ok = actual != null && actual.contains(messagePart);
        report(name, "error containing '" + messagePart + "'", ok ? "error containing '" + messagePart + "'" : actual);
    }
    private void report(String name, String exp, String act) {
        boolean ok = exp.equals(act);
        if (ok) pass++; else fail++;
        System.out.printf("%-46s | %-28s | %-28s | %s%n", name, exp, act, ok ? "PASS" : "FAIL");
    }
    public void header() { System.out.printf("%-46s | %-28s | %-28s | %s%n", "TEST", "EXPECTED", "ACTUAL", "RESULT"); }
    public int passed() { return pass; }
    public int failed() { return fail; }
}
