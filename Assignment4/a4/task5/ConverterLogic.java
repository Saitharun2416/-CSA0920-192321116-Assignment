package a4.task5;

/** Shared, GUI-free conversion logic used by all four styles. */
public final class ConverterLogic {
    private ConverterLogic() { }
    public static double rate(String currency) { return currency.equals("USD") ? 0.012 : 0.011; }
    /** @throws NumberFormatException if the text is not a number */
    public static String convert(String amountText, String currency) {
        double a = Double.parseDouble(amountText.trim());
        return String.format("%.2f %s", a * rate(currency), currency);
    }
}
