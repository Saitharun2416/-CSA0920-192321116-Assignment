package task_b1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Operation (iii) of Task A1: list all passes on a given route. */
public final class PassFileOps {
    private PassFileOps() { }

    public static List<String[]> listByRoute(Path file, int route) throws IOException {
        List<String[]> result = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (f.length != 5) {
                    continue;                       // damaged line
                }
                try {
                    if (Integer.parseInt(f[3].trim()) == route) {
                        result.add(f);
                    }
                } catch (NumberFormatException ignored) {
                    // damaged route field: skip line
                }
            }
        }
        return result;
    }
}
