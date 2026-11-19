package a3.q2;

/** CHECKED - wraps low-level IOException; keeps the original as the cause (chaining). */
public class SubmissionStorageException extends ExamException {
    public SubmissionStorageException(String message, Throwable cause) { super(message, cause); }
    /** Deliberately has no cause: used to show what is lost when the cause is dropped. */
    public SubmissionStorageException(String message) { super(message); }
}
