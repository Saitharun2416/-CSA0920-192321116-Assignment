package a3.q2;

import java.util.Set;

/** Layer 2 - business rules. Declares (throws) checked exceptions and lets storage ones propagate. */
public class ExamService {
    private final StorageLayer storage = new StorageLayer();
    private final Set<String> registered = Set.of("S101", "S102");
    private boolean windowOpen = true;

    public void setWindowOpen(boolean open) { this.windowOpen = open; }

    public void submit(String studentId, String fileName, long sizeKb, boolean diskFails, boolean keepCause)
            throws ExamException {
        if (!windowOpen) throw new ExamWindowClosedException("The exam window is closed");
        if (!registered.contains(studentId)) throw new NotRegisteredException(studentId + " is not registered for this course");
        storage.save(fileName, sizeKb, diskFails, keepCause);      // propagates upward unchanged
    }
}
