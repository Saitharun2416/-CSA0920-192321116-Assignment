package q1;

/** Q1 design decision - several threads appending to a shared log. */
public class Q1ThreadSafety {
    public static void main(String[] args) throws Exception {
        int threads = 8, perThread = 500_000;
        StringBuilder unsafe = new StringBuilder();
        StringBuffer safe = new StringBuffer();
        Runnable r = () -> {
            for (int i = 0; i < perThread; i++) {
                try { unsafe.append("x"); } catch (RuntimeException e) { /* AIOOBE possible under races */ }
                safe.append("x");
            }
        };
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) { ts[i] = new Thread(r); ts[i].start(); }
        for (Thread t : ts) t.join();
        System.out.println("Expected length   : " + threads * perThread);
        System.out.println("StringBuilder     : " + unsafe.length() + "   <-- lost updates = " + (threads * perThread - unsafe.length()) + " (race: NOT guaranteed on every run, but possible; try running again)");
        System.out.println("StringBuffer      : " + safe.length() + "   <-- always correct (synchronized methods)");
        System.out.println("(Not-thread-safe code can pass many runs by luck - that is why it is dangerous.)");
        System.out.println("Decision: per-ticket text is local to one thread -> StringBuilder; shared log -> StringBuffer (or a lock/concurrent queue).");
    }
}
