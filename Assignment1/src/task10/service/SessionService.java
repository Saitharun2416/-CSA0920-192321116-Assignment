package task10.service;

import task10.model.ParkingSession;
import java.util.HashMap;
import java.util.Map;

/** Service layer: business logic using the model through its public API only. */
public class SessionService {
    private final Map<String, ParkingSession> sessions = new HashMap<>();
    public void start(ParkingSession s) { sessions.put(s.getRegNo(), s); }
    public ParkingSession find(String regNo) { return sessions.get(regNo.trim().toUpperCase()); }
    public double fee(ParkingSession s, double rate) {
        return ((s.getDurationMinutes() + 59) / 60) * rate;
    }
}
