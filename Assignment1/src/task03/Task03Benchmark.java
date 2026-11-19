package task03;

/** TASK 3 - Benchmarks scanning vs free-stack allocation on a nearly-full facility. */
public class Task03Benchmark {
    public static void main(String[] args) {
        int[][] configs = { {20}, {50, 50, 50}, {100, 100, 100, 100, 100} };
        String[] names = { "1x20", "3x50", "5x100" };
        for (int c = 0; c < configs.length; c++) {
            int total = 0; for (int s : configs[c]) total += s;
            long scan = 0, fast = 0; int reps = 2000;
            for (int r = 0; r < reps; r++) {
                ParkingFacility a = new ParkingFacility(configs[c]);
                FastFacility b = new FastFacility(configs[c]);
                long t = System.nanoTime();
                for (int i = 0; i < total; i++) a.allocate("V" + i);
                scan += System.nanoTime() - t;
                t = System.nanoTime();
                for (int i = 0; i < total; i++) b.allocate("V" + i);
                fast += System.nanoTime() - t;
            }
            System.out.printf("%-6s fill-all: scan %,10d ns   stack %,10d ns  (avg of %d)%n",
                    names[c], scan / reps, fast / reps, reps);
        }
    }
}
