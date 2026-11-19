package task_b1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Alternative concept: read once at start-up into a HashMap -> O(n) load, then O(1) per search. */
public class CachedPassStore {
    private final Map<String, String[]> index = new HashMap<>();

    public CachedPassStore(Path file) throws IOException {
        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (f.length == 5) {
                    index.put(f[0], f);
                }
            }
        }
    }

    public String[] find(String passId) {
        return index.get(passId);
    }

    public int size() {
        return index.size();
    }
}
