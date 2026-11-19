package common;

/** Validates one text line of the bus-pass file. */
public final class PassValidator {

    private PassValidator() { }

    /** Returns null when the line is valid, otherwise the reason it is damaged. */
    public static String validate(String line) {
        if (line == null || line.trim().isEmpty()) {
            return "empty line";
        }
        String[] f = line.split(",", -1);
        if (f.length != 5) {
            return "expected 5 fields but found " + f.length;
        }
        for (String s : f) {
            if (s.trim().isEmpty()) {
                return "blank field";
            }
        }
        try {
            Integer.parseInt(f[3].trim());
        } catch (NumberFormatException e) {
            return "route is not an integer: " + f[3];
        }
        try {
            double amt = Double.parseDouble(f[4].trim());
            if (amt <= 0) {
                return "amount must be positive: " + f[4];
            }
        } catch (NumberFormatException e) {
            return "amount is not numeric: " + f[4];
        }
        return null;
    }
}
