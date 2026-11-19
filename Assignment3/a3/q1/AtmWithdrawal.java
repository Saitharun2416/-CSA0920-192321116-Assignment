package a3.q1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Q1 - ATM withdrawal with try / catch (multi-catch) / finally.
 *
 * Failure points -> exception -> checked?
 *   1. letters typed as account/amount   NumberFormatException          unchecked
 *   2. amount too large (cents overflow) ArithmeticException            unchecked
 *   3. negative / zero amount            IllegalArgumentException       unchecked
 *   4. amount > balance                  InsufficientFundsException     CHECKED (custom)
 *   5. card-reader file missing          FileNotFoundException          CHECKED
 *   6. read error on card file           IOException                    CHECKED
 *   7. JVM running out of memory etc.    OutOfMemoryError               Error (never caught)
 *
 * Run with no arguments: automatic test table. Run with argument "interactive" for Scanner input.
 */
public class AtmWithdrawal {

    private static final Map<Integer, Integer> BALANCE = new HashMap<>();
    private static File cardDir;

    static {
        BALANCE.put(1001, 5000);
        BALANCE.put(1002, 200);
        BALANCE.put(1003, 9000);           // account exists but its card file will be missing
    }

    /** Throws checked + unchecked exceptions; does NOT handle them. */
    static int withdraw(String accountText, String amountText)
            throws InsufficientFundsException, IOException {
        int account = Integer.parseInt(accountText.trim());        // NumberFormatException
        int amount = Integer.parseInt(amountText.trim());          // NumberFormatException
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive: " + amount);
        }
        Math.multiplyExact(amount, 100);                            // ArithmeticException (overflow)

        // card reader: FileNotFoundException if the file is missing
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(cardDir, account + ".card")))) {
            String pin = reader.readLine();                         // IOException
            System.out.println("   card read OK (pin hash " + pin + ")");
        }

        int balance = BALANCE.getOrDefault(account, 0);
        if (amount > balance) {
            throw new InsufficientFundsException("Balance " + balance + " is less than " + amount);
        }
        BALANCE.put(account, balance - amount);
        return balance - amount;
    }

    /** Handles every failure. Catch order: specific/sub-class first, general last. */
    static void process(String account, String amount) {
        System.out.println("-> withdraw(" + account + ", " + amount + ")");
        try {
            int left = withdraw(account, amount);
            System.out.println("   Dispensed " + amount + ". Balance left = " + left);
        } catch (NumberFormatException | ArithmeticException e) {   // multi-catch (unrelated types)
            System.out.println("   [catch#1 NFE|Arithmetic] Invalid number: " + e.getMessage());
        } catch (IllegalArgumentException e) {                      // parent of NumberFormatException -> must come AFTER it
            System.out.println("   [catch#2 IllegalArgument] " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("   [catch#3 InsufficientFunds] " + e.getMessage());
        } catch (FileNotFoundException e) {                          // sub-class of IOException -> before it
            System.out.println("   [catch#4 FileNotFound] Card reader file missing: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("   [catch#5 IOException] " + e.getMessage());
        } finally {
            System.out.println("   [finally] Card returned / session closed.");
        }
    }

    /*
     * Broader-before-narrower demo (does NOT compile, which is the point):
     *
     *   try { ... }
     *   catch (IOException e)           { ... }
     *   catch (FileNotFoundException e) { ... }   // error: exception FileNotFoundException
     *                                             // has already been caught
     */

    /** Used for the "stack trace of an uncaught exception" evidence. */
    static void uncaughtDemo() {
        System.out.println("\n== Uncaught exception (read the stack trace bottom-up) ==");
        try {
            level1();
        } catch (RuntimeException e) {
            e.printStackTrace(System.out);   // shown here so the run continues; remove try/catch to make it truly uncaught
        }
    }
    static void level1() { level2(); }
    static void level2() { Integer.parseInt("12x"); }

    public static void main(String[] args) throws Exception {
        cardDir = Files.createTempDirectory("atm-cards").toFile();
        for (int acc : new int[]{1001, 1002}) {                      // 1003 intentionally has no card file
            Files.writeString(new File(cardDir, acc + ".card").toPath(), "pinhash" + acc + "\n");
        }

        if (args.length > 0 && args[0].equals("interactive")) {
            try (Scanner sc = new Scanner(System.in)) {
                System.out.print("Account: "); String a = sc.nextLine();
                System.out.print("Amount : "); String m = sc.nextLine();
                process(a, m);
            }
            return;
        }

        System.out.println("== Test table (each row triggers a different path) ==");
        process("1001", "500");            // success
        process("abc", "100");             // NumberFormatException
        process("1001", "-50");            // IllegalArgumentException
        process("1001", "99999");          // InsufficientFundsException
        process("1003", "100");            // FileNotFoundException
        process("1001", "30000000");       // ArithmeticException (overflow)
        uncaughtDemo();
    }
}
