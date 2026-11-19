package a4.task6.dao;

import a4.task6.model.Registration;

import java.util.ArrayList;
import java.util.List;

/** Fallback so the GUI can be demonstrated without MySQL. Same contract (including duplicate rule). */
public class InMemoryRegistrationDao implements RegistrationDao {
    private final List<Registration> rows = new ArrayList<>();
    private final List<String> events = List.of("Cultural Fest", "Hackathon", "Music Night", "Quiz Bowl");

    @Override public List<String> events() { return events; }

    @Override public synchronized void insert(Registration r) throws DuplicateRegistrationException {
        for (Registration x : rows)
            if (x.studentId().equals(r.studentId()) && x.eventName().equals(r.eventName()))
                throw new DuplicateRegistrationException(r.studentId() + " is already registered for " + r.eventName());
        rows.add(r);
    }

    private synchronized List<Registration> filtered(String event) {
        List<Registration> out = new ArrayList<>();
        for (Registration r : rows) if (event == null || event.equals("All") || r.eventName().equals(event)) out.add(r);
        return out;
    }

    @Override public List<Registration> page(String event, int limit, int offset) {
        List<Registration> f = filtered(event);
        return f.subList(Math.min(offset, f.size()), Math.min(offset + limit, f.size()));
    }
    @Override public int count(String event) { return filtered(event).size(); }
    @Override public List<Registration> all(String event) { return filtered(event); }
}
