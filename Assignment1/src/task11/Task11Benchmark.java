package task11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** TASK 11 alternative - finding an active ticket on exit: array vs ArrayList vs HashMap. */
public class Task11Benchmark {
    record Tk(String reg, int slot) { }

    public static void main(String[] args) {
        int[] sizes = {500, 5_000, 50_000};
        for (int n : sizes) {
            Tk[] arr = new Tk[n]; List<Tk> list = new ArrayList<>(); Map<String, Tk> map = new HashMap<>();
            for (int i = 0; i < n; i++) { Tk k = new Tk("TN" + i, i); arr[i] = k; list.add(k); map.put(k.reg(), k); }
            int lookups = 2000; String[] keys = new String[lookups];
            for (int i = 0; i < lookups; i++) keys[i] = "TN" + (int) (Math.random() * n);
            long sink = 0;
            long t0 = System.nanoTime();
            for (String key : keys) for (Tk k : arr) if (k.reg().equals(key)) { sink += k.slot(); break; }
            long a = System.nanoTime() - t0; t0 = System.nanoTime();
            for (String key : keys) for (Tk k : list) if (k.reg().equals(key)) { sink += k.slot(); break; }
            long b = System.nanoTime() - t0; t0 = System.nanoTime();
            for (String key : keys) sink += map.get(key).slot();
            long c = System.nanoTime() - t0;
            System.out.printf("n=%,7d  array %,10d ns/lookup (O(n))  ArrayList %,10d ns (O(n))  HashMap %,6d ns (O(1) avg)  [%d]%n",
                    n, a / lookups, b / lookups, c / lookups, sink % 10);
        }
        System.out.println("Space: array/ArrayList = n refs; HashMap = n entries (node ~32-48 B each) + table -> about 2-4x more, still O(n).");
    }
}
