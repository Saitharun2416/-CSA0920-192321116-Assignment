package a3.q2;

/** CHECKED - business rule: student must be registered for the course. */
public class NotRegisteredException extends ExamException {
    public NotRegisteredException(String message) { super(message); }
}
