package a3.q6;

/**
 * Q6 - "change while to if" experiment. With if, a thread woken by notifyAll() does not re-check the
 * condition: a consumer can take from an EMPTY buffer (count becomes -1, stale/duplicate tokens) or a
 * producer can overwrite a full buffer. A watchdog stops the run if it hangs.
 */
public class TokenCounterIfDemo {
    static class IfCounter {
        private final int[] buffer = new int[5];
        private int count = 0, in = 0, out = 0, violations = 0;

        synchronized void put(int token) throws InterruptedException {
            if (count == buffer.length) wait();                 // BUG: if instead of while
            buffer[in] = token; in = (in + 1) % buffer.length; count++;
            if (count > 5) violations++;
            notifyAll();
        }
        synchronized int take() throws InterruptedException {
            if (count == 0) wait();                             // BUG: if instead of while
            int token = buffer[out]; out = (out + 1) % buffer.length; count--;
            if (count < 0) violations++;
            notifyAll();
            return token;
        }
    }

    public static void main(String[] args) throws Exception {
        for (int run = 1; run <= 5; run++) {
            IfCounter c = new IfCounter();
            int[] collected = new int[3];
            Thread cook = new Thread(() -> { try { for (int t = 1; t <= 30; t++) c.put(t); } catch (InterruptedException e) { } });
            Thread[] st = new Thread[3];
            for (int s = 0; s < 3; s++) {
                final int id = s;
                st[s] = new Thread(() -> { try { for (int k = 0; k < 10; k++) collected[id] += c.take(); } catch (InterruptedException e) { } });
                st[s].setDaemon(true);
            }
            cook.setDaemon(true);
            cook.start(); for (Thread t : st) t.start();
            cook.join(2000); for (Thread t : st) t.join(2000);
            boolean hung = cook.isAlive() || st[0].isAlive() || st[1].isAlive() || st[2].isAlive();
            System.out.println("run " + run + ": sum=" + (collected[0] + collected[1] + collected[2])
                    + " (expected 465), violations=" + c.violations + (hung ? ", HUNG/incomplete" : ""));
        }
        System.exit(0);
    }
}
