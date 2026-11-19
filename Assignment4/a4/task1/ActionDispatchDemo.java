package a4.task1;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Alternative-concept measurement: if-else chain on command (O(k)) vs map of handlers (O(1)). No GUI needed. */
public class ActionDispatchDemo {
    public static void main(String[] args) {
        final int K = 50, N = 5_000_000;
        String[] cmds = new String[K];
        Map<String, Consumer<String>> map = new HashMap<>();
        long[] sink = new long[1];
        for (int i = 0; i < K; i++) { cmds[i] = "Cmd" + i; map.put(cmds[i], s -> sink[0]++); }
        String last = cmds[K - 1];

        long t0 = System.nanoTime();
        for (int i = 0; i < N; i++) {
            String c = new String(last);                    // not interned, like a real getActionCommand()
            for (int k = 0; k < K; k++) if (c.equals(cmds[k])) { sink[0]++; break; }   // up to k comparisons
        }
        long chain = System.nanoTime() - t0;

        t0 = System.nanoTime();
        for (int i = 0; i < N; i++) map.get(new String(last)).accept(last);            // O(1) expected
        long hash = System.nanoTime() - t0;

        System.out.printf("if-else chain (worst case, k=%d): %.1f ns/event%n", K, chain / (double) N);
        System.out.printf("map / own listener   (O(1))     : %.1f ns/event  [sink=%d]%n", hash / (double) N, sink[0]);
    }
}
