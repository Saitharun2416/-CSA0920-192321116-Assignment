package a4.task1;

/** Checked: invalid user input is an expected, recoverable situation. */
public class FeeValidationException extends Exception {
    public FeeValidationException(String message) { super(message); }
}
