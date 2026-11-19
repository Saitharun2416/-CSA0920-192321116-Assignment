package task_b1;

import java.io.IOException;
import java.nio.file.Path;

/**
 * PassStore has a package-private constructor (as given in the assignment), so classes in other packages
 * (for example task_b2) use this public wrapper.
 */
public class PassStoreAccess {
    private final PassStore store;

    public PassStoreAccess(Path file) { this.store = new PassStore(file); }

    public String[] find(String id) throws IOException { return store.find(id); }

    public void add(String passId, String regNo, String name, int route, double amount) throws IOException {
        store.add(passId, regNo, name, route, amount);
    }
}
