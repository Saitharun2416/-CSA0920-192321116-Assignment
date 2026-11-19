package a4.task1;

import java.util.ArrayList;
import java.util.List;

public class InMemoryFeeRepository implements FeeRepository {
    public final List<String> rows = new ArrayList<>();
    @Override public synchronized void save(String regNo, double amount) { rows.add(regNo + ":" + amount); }
}
