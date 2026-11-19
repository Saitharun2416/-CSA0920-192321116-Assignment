package a3.q4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Q4 - Thread vs Runnable, start/run, join, isAlive, priority, daemon, ExecutorService. */
public class Q4Demo {

    static final AtomicInteger TOTAL = new AtomicInteger();

    /** (1) extends Thread */
    static class StationThread extends Thread {
        private final int scripts;
        StationThread(String name, int scripts) { super(name); this.scripts = scripts; }
        @Override public void run() { scan(getName(), scripts); }
    }

    /** (2) implements Runnable - preferred: class can still extend something else, task is reusable with pools */
    static class StationTask implements Runnable {
        private final String name; private final int scripts;
        StationTask(String name, int scripts) { this.name = name; this.scripts = scripts; }
        @Override public void run() { scan(name, scripts); }
    }

    static void scan(String station, int scripts) {
        try {
            for (int i = 1; i <= scripts; i++) Thread.sleep(20 + (int) (Math.random() * 30));   // TIMED_WAITING
            TOTAL.addAndGet(scripts);
            System.out.println(station + " finished " + scripts + " scripts on thread [" + Thread.currentThread().getName() + "]");
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== 1. extends Thread ==");
        TOTAL.set(0);
        Thread[] t = new Thread[4];
        for (int i = 0; i < 4; i++) t[i] = new StationThread("Station-" + (i + 1), 10 + i);
        for (Thread x : t) System.out.println(x.getName() + " state=" + x.getState());           // NEW
        for (Thread x : t) x.start();
        System.out.println("alive right after start: " + t[0].isAlive() + ", state=" + t[0].getState());
        for (Thread x : t) x.join();                                                              // main waits (WAITING)
        System.out.println("alive after join: " + t[0].isAlive() + ", state=" + t[0].getState()); // TERMINATED
        System.out.println("TOTAL scripts (printed only after all joined) = " + TOTAL.get());

        System.out.println("\n== 2. implements Runnable ==");
        TOTAL.set(0);
        Thread[] r = new Thread[4];
        for (int i = 0; i < 4; i++) r[i] = new Thread(new StationTask("Station-" + (i + 1), 10 + i), "R-" + (i + 1));
        for (Thread x : r) x.start();
        for (Thread x : r) x.join();
        System.out.println("TOTAL = " + TOTAL.get());

        System.out.println("\n== 3. run() instead of start(): NO new thread ==");
        Thread bad = new StationThread("Station-X", 2);
        bad.run();   // executes in main; the log line shows thread [main]
        System.out.println("state after run(): " + bad.getState() + " (still NEW - never started)");

        System.out.println("\n== 4. Priorities (hint only; scheduler may ignore) ==");
        Thread lo = new StationThread("Low", 5), hi = new StationThread("High", 5);
        lo.setPriority(Thread.MIN_PRIORITY); hi.setPriority(Thread.MAX_PRIORITY);
        lo.start(); hi.start(); lo.join(); hi.join();

        System.out.println("\n== 5. Daemon thread: JVM does not wait for it ==");
        Thread daemon = new Thread(() -> {
            try { Thread.sleep(2000); System.out.println("daemon finished (you will normally NOT see this)"); }
            catch (InterruptedException ignored) { }
        }, "Daemon-Station");
        daemon.setDaemon(true);
        daemon.start();
        System.out.println("main ends now; daemon is still running -> JVM exits without it");

        System.out.println("\n== 6. Alternative: ExecutorService, fixed pool of 2 threads for 4 stations ==");
        ExecutorService pool = Executors.newFixedThreadPool(2);
        TOTAL.set(0);
        for (int i = 0; i < 4; i++) pool.submit(new StationTask("Station-" + (i + 1), 10 + i));
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("TOTAL = " + TOTAL.get());
    }
}
