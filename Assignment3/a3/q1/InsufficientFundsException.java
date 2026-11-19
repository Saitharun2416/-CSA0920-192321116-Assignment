package a3.q1;

/** Checked exception: a business rule that the caller is expected to handle. */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) { super(message); }
}
