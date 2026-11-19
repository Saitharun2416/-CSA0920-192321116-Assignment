package a3.q2;

/** UNCHECKED - programming error (null/blank file name); nobody can "recover", so no throws clause. */
public class InvalidSubmissionException extends RuntimeException {
    public InvalidSubmissionException(String message) { super(message); }
}
