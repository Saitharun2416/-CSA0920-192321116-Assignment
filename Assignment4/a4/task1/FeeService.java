package a4.task1;

import java.util.regex.Pattern;

/** Task 1.2 - all validation and payment logic. No AWT/Swing imports: easy to unit-test. */
public class FeeService {
    private static final Pattern REG = Pattern.compile("[A-Za-z0-9]{6,15}");
    private final FeeRepository repository;

    public FeeService(FeeRepository repository) { this.repository = repository; }

    /** Validates input and returns the amount. */
    public double validate(String regNo, String amountText) throws FeeValidationException {
        if (regNo == null || !REG.matcher(regNo.trim()).matches())
            throw new FeeValidationException("Register number must be 6-15 letters/digits");
        if (amountText == null || amountText.isBlank())
            throw new FeeValidationException("Amount is required");
        double amount;
        try { amount = Double.parseDouble(amountText.trim()); }
        catch (NumberFormatException e) { throw new FeeValidationException("Amount must be a number"); }
        if (amount <= 0) throw new FeeValidationException("Amount must be greater than zero");
        return amount;
    }

    /** Pays and returns a receipt text. Simulates 1.5 s bank confirmation (must run off the EDT). */
    public String pay(String regNo, String amountText) throws Exception {
        double amount = validate(regNo, amountText);
        Thread.sleep(1500);
        repository.save(regNo.trim(), amount);
        return "RECEIPT\nReg No : " + regNo.trim() + "\nAmount : " + String.format("%.2f", amount) + "\nStatus : PAID";
    }
}
