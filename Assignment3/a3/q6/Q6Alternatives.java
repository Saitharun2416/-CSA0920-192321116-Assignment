package a3.q6;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/** Q6 alternatives: ArrayBlockingQueue (producer-consumer) and tryLock with timeout (deadlock avoidance). */
public class Q6Alternatives {

    static class Wallet {
        final ReentrantLock lock = new ReentrantLock(); int balance = 1000;
    }

    /** Returns true if transfer done; false -> caller may retry (no deadlock, but possible livelock). */
    static boolean transfer(Wallet from, Wallet to, int amt) throws InterruptedException {
        if (from.lock.tryLock(50, TimeUnit.MILLISECONDS)) {
            try {
                Thread.sleep(20);
                if (to.lock.tryLock(50, TimeUnit.MILLISECONDS)) {
                    try { from.balance -= amt; to.balance += amt; return true; }
                    finally { to.lock.unlock(); }
                }
            } finally { from.lock.unlock(); }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== ArrayBlockingQueue(5) producer-consumer ==");
        BlockingQueue<Integer> q = new ArrayBlockingQueue<>(5);
        int[] sum = new int[2];
        Thread cook = new Thread(() -> { try { for (int t = 1; t <= 20; t++) q.put(t); } catch (InterruptedException e) { } });
        Thread[] st = new Thread[2];
        for (int s = 0; s < 2; s++) {
            final int id = s;
            st[s] = new Thread(() -> { try { for (int k = 0; k < 10; k++) sum[id] += q.take(); } catch (InterruptedException e) { } });
        }
        cook.start(); for (Thread t : st) t.start();
        cook.join(); for (Thread t : st) t.join();
        System.out.println("Sum = " + (sum[0] + sum[1]) + " (expected 210)  - no wait/notify code at all");

        System.out.println("\n== tryLock with timeout instead of lock ordering ==");
        Wallet a = new Wallet(), b = new Wallet();
        Thread t1 = new Thread(() -> { try { System.out.println("A->B ok? " + retry(a, b, 100)); } catch (InterruptedException e) { } });
        Thread t2 = new Thread(() -> { try { System.out.println("B->A ok? " + retry(b, a, 50)); } catch (InterruptedException e) { } });
        t1.start(); t2.start(); t1.join(); t2.join();
        System.out.println("A=" + a.balance + " B=" + b.balance + " (sum 2000)");
    }

    static boolean retry(Wallet f, Wallet t, int amt) throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            if (transfer(f, t, amt)) return true;
            Thread.sleep((long) (Math.random() * 40));   // random back-off avoids livelock
        }
        return false;
    }
}
