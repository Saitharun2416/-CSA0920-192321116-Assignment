package a3.q5;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/** Q5 - race reproduction, synchronized method/block, Atomic and ReentrantLock, and timings. */
public class BookingExperiments {

    interface Booker { void book(int seat); int sold(); }

    /** (a) no synchronization. yield() widens the window between check and update so the race is reliable. */
    static class Unsafe implements Booker {
        final boolean[] booked; int sold = 0;
        Unsafe(int n) { booked = new boolean[n]; }
        public void book(int s) {
            if (!booked[s]) {            // CHECK
                Thread.yield();          // another thread can pass the same check here
                booked[s] = true;        // ACT   <- critical section is check + act + counter
                sold++;
            }
        }
        public int sold() { return sold; }
    }

    /** (b) synchronized method: lock = this, held for the whole method. */
    static class SyncMethod implements Booker {
        final boolean[] booked; int sold = 0;
        SyncMethod(int n) { booked = new boolean[n]; }
        public synchronized void book(int s) {
            if (!booked[s]) { Thread.yield(); booked[s] = true; sold++; }
        }
        public synchronized int sold() { return sold; }
    }

    /** (c) synchronized block: lock only the critical section. */
    static class SyncBlock implements Booker {
        final boolean[] booked; int sold = 0; final Object lock = new Object();
        SyncBlock(int n) { booked = new boolean[n]; }
        public void book(int s) {
            // non-critical work (e.g. validation, logging) can happen here without the lock
            synchronized (lock) {
                if (!booked[s]) { Thread.yield(); booked[s] = true; sold++; }
            }
        }
        public int sold() { synchronized (lock) { return sold; } }
    }

    /** (d) alternative: lock-free per seat using compareAndSet. */
    static class Atomic implements Booker {
        final AtomicBoolean[] booked; final AtomicInteger sold = new AtomicInteger();
        Atomic(int n) { booked = new AtomicBoolean[n]; for (int i = 0; i < n; i++) booked[i] = new AtomicBoolean(); }
        public void book(int s) { if (booked[s].compareAndSet(false, true)) sold.incrementAndGet(); }
        public int sold() { return sold.get(); }
    }

    /** (e) alternative: ReentrantLock. */
    static class Locked implements Booker {
        final boolean[] booked; int sold = 0; final ReentrantLock lock = new ReentrantLock();
        Locked(int n) { booked = new boolean[n]; }
        public void book(int s) {
            lock.lock();
            try { if (!booked[s]) { booked[s] = true; sold++; } } finally { lock.unlock(); }
        }
        public int sold() { lock.lock(); try { return sold; } finally { lock.unlock(); } }
    }

    /** Runs `requests` booking attempts over `seats` seats using `threads` threads; returns sold. */
    static int run(Booker b, int requests, int seats, int threads) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < requests; i++) { final int seat = i % seats; pool.execute(() -> b.book(seat)); }
        pool.shutdown();
        pool.awaitTermination(60, TimeUnit.SECONDS);
        return b.sold();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== Double-booking test: 100 requests, 10 seats (correct answer = 10), 10 runs each ==");
        String[] names = {"Unsafe", "SyncMethod", "SyncBlock", "Atomic", "Locked"};
        for (String n : names) {
            StringBuilder sb = new StringBuilder(String.format("%-11s", n));
            int wrong = 0;
            for (int rep = 0; rep < 10; rep++) {
                int sold = run(make(n, 10), 100, 10, 16);
                sb.append(' ').append(sold);
                if (sold != 10) wrong++;
            }
            System.out.println(sb + "   runs with double booking: " + wrong + "/10");
        }

        System.out.println("\n== Timing: 10,000 bookings over 10,000 seats, 8 threads (ms, best of 5) ==");
        for (String n : names) {
            long best = Long.MAX_VALUE;
            for (int rep = 0; rep < 5; rep++) {
                long t0 = System.nanoTime();
                run(make(n, 10_000), 10_000, 10_000, 8);
                best = Math.min(best, System.nanoTime() - t0);
            }
            System.out.printf("%-11s %8.2f ms%n", n, best / 1e6);
        }
    }

    static Booker make(String n, int seats) {
        switch (n) {
            case "Unsafe": return new Unsafe(seats);
            case "SyncMethod": return new SyncMethod(seats);
            case "SyncBlock": return new SyncBlock(seats);
            case "Atomic": return new Atomic(seats);
            default: return new Locked(seats);
        }
    }
}
