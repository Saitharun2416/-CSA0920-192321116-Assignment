package a3.q6;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/** Q6 Part A - wallet transfers: a version that deadlocks, then one that cannot (lock ordering). */
public class DeadlockDemo {

    static class Wallet {
        final int id; int balance;
        Wallet(int id, int balance) { this.id = id; this.balance = balance; }
    }

    /** Deadlock-prone: locks "from" then "to". T1: A->B, T2: B->A  => circular wait. */
    static void unsafeTransfer(Wallet from, Wallet to, int amt) {
        synchronized (from) {
            pause(100);                       // makes the bad interleaving reliable
            synchronized (to) { from.balance -= amt; to.balance += amt; }
        }
    }

    /** Safe: always lock the wallet with the smaller id first (breaks circular wait). */
    static void safeTransfer(Wallet from, Wallet to, int amt) {
        Wallet first = from.id < to.id ? from : to;
        Wallet second = from.id < to.id ? to : from;
        synchronized (first) {
            pause(100);
            synchronized (second) { from.balance -= amt; to.balance += amt; }
        }
    }

    static void pause(long ms) { try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } }

    public static void main(String[] args) throws Exception {
        Wallet a = new Wallet(1, 1000), b = new Wallet(2, 1000);

        System.out.println("== Unsafe transfers (A->B and B->A at the same time) ==");
        Thread t1 = new Thread(() -> unsafeTransfer(a, b, 100), "T1 A->B");
        Thread t2 = new Thread(() -> unsafeTransfer(b, a, 50), "T2 B->A");
        t1.setDaemon(true); t2.setDaemon(true);     // daemon so the JVM can still exit
        t1.start(); t2.start();
        t1.join(1500); t2.join(1500);

        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] dead = mx.findDeadlockedThreads();
        if (dead != null) {
            System.out.println("DEADLOCK detected between:");
            for (var info : mx.getThreadInfo(dead)) {
                System.out.println("  " + info.getThreadName() + " waits for lock held by " + info.getLockOwnerName());
            }
            System.out.println("Four conditions: mutual exclusion (synchronized), hold-and-wait (holds 'from', wants 'to'),");
            System.out.println("no pre-emption (a monitor cannot be taken away), circular wait (T1->B->T2->A->T1).");
        } else {
            System.out.println("No deadlock this time (unexpected).");
        }

        System.out.println("\n== Safe transfers with lock ordering ==");
        Wallet c = new Wallet(3, 1000), d = new Wallet(4, 1000);
        Thread s1 = new Thread(() -> safeTransfer(c, d, 100), "S1 C->D");
        Thread s2 = new Thread(() -> safeTransfer(d, c, 50), "S2 D->C");
        s1.start(); s2.start(); s1.join(); s2.join();
        System.out.println("Both finished. C=" + c.balance + " D=" + d.balance + " (sum " + (c.balance + d.balance) + " = 2000)");
        System.exit(0);
    }
}
