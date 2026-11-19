package q2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/** Q2 - ArrayList vs LinkedList vs ArrayDeque vs plain array with manual shifting. Times in ms (avg of 3). */
public class Q2ListBench {
    record Song(int id) { }
    static final int RUNS = 3;
    static long sink;

    interface Op { void run(); }
    static double time(Op o) { o.run(); double t = 0; for (int i = 0; i < RUNS; i++) { long s = System.nanoTime(); o.run(); t += System.nanoTime() - s; } return t / RUNS / 1e6; }

    static List<Song> fill(List<Song> l, int n) { for (int i = 0; i < n; i++) l.add(new Song(i)); return l; }

    public static void main(String[] args) {
        int[] sizes = {10_000, 50_000, 200_000};
        int ops = 2_000;                                    // number of positional operations per test
        Random rnd = new Random(1);
        System.out.printf("%-9s %-10s | %9s %11s %12s %10s %9s%n", "size", "structure", "addEnd(n)", "insFront(k)", "insMiddle(k)", "get k-th", "iterate");
        for (int n : sizes) {
            int[] idx = new int[ops]; for (int i = 0; i < ops; i++) idx[i] = rnd.nextInt(n);
            for (String name : new String[]{"ArrayList", "LinkedList"}) {
                boolean arr = name.equals("ArrayList");
                double add = time(() -> { List<Song> l = arr ? new ArrayList<>() : new LinkedList<>(); fill(l, n); });
                List<Song> base = fill(arr ? new ArrayList<>() : new LinkedList<>(), n);
                double front = time(() -> { for (int i = 0; i < ops; i++) { base.add(0, new Song(i)); base.remove(0); } });
                double mid = time(() -> { for (int i = 0; i < ops; i++) { base.add(n / 2, new Song(i)); base.remove(n / 2); } });
                double get = time(() -> { long s = 0; for (int i = 0; i < ops; i++) s += base.get(idx[i]).id(); sink += s; });
                double it = time(() -> { long s = 0; for (Song x : base) s += x.id(); sink += s; });
                System.out.printf("%-9d %-10s | %9.2f %11.2f %12.2f %10.2f %9.2f%n", n, name, add, front, mid, get, it);
            }
            // ArrayDeque: no index access, O(1) at both ends
            double add = time(() -> { ArrayDeque<Song> d = new ArrayDeque<>(); for (int i = 0; i < n; i++) d.addLast(new Song(i)); });
            ArrayDeque<Song> dq = new ArrayDeque<>(); for (int i = 0; i < n; i++) dq.addLast(new Song(i));
            double front = time(() -> { for (int i = 0; i < ops; i++) { dq.addFirst(new Song(i)); dq.removeFirst(); } });
            double it = time(() -> { long s = 0; for (Song x : dq) s += x.id(); sink += s; });
            System.out.printf("%-9d %-10s | %9.2f %11.2f %12s %10s %9.2f%n", n, "ArrayDeque", add, front, "n/a", "n/a", it);
            // plain array with manual shifting
            Song[] a = new Song[n + 1]; for (int i = 0; i < n; i++) a[i] = new Song(i);
            double afront = time(() -> { for (int i = 0; i < ops; i++) { System.arraycopy(a, 0, a, 1, n); a[0] = new Song(i); System.arraycopy(a, 1, a, 0, n); } });
            double aget = time(() -> { long s = 0; for (int i = 0; i < ops; i++) s += a[idx[i]].id(); sink += s; });
            System.out.printf("%-9d %-10s | %9s %11.2f %12s %10.2f %9s%n%n", n, "plain[]", "-", afront, "~same", aget, "-");
        }
        System.out.println("Complexity table (n elements):");
        System.out.println("op              ArrayList        LinkedList        ArrayDeque     plain array (manual)");
        System.out.println("add at end      O(1) amortised   O(1)              O(1)           O(1) if capacity left");
        System.out.println("insert front    O(n)             O(1)              O(1)           O(n)");
        System.out.println("insert middle   O(n)             O(n) (find) + O(1)  n/a          O(n)");
        System.out.println("get k-th        O(1)             O(n)              n/a            O(1)");
        System.out.println("iterate         O(n) cache-fast  O(n) pointer-chasing  O(n)        O(n)");
        System.out.println("space/element   4-8 B slot       ~24-40 B node (2 refs + header)  4-8 B slot  4-8 B slot");
        System.out.println("\nANSWER: ArrayList is reasonable when the playlist is read/jumped-to often and edits are mostly at the END (or lists are small).");
        System.out.println("Stop recommending it when inserts/removes at the FRONT/MIDDLE dominate for large lists.");
        System.out.println("'Up Next' queue (add both ends, remove front, no random access): ArrayDeque - O(1) all ends, less memory than LinkedList.");
        System.out.println("(sink=" + sink + ")");
    }
}
