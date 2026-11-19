package a3.q2;

/** Root of the portal's exception hierarchy. CHECKED: callers must decide how to react. */
public class ExamException extends Exception {
    public ExamException(String message) { super(message); }
    public ExamException(String message, Throwable cause) { super(message, cause); }
}
