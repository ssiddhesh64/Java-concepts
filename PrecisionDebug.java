/**
 * CONCEPT TAUGHT: Floating-Point Precision & Rounding Issues
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates why comparing double values directly with `==` is dangerous due to base-2 binary floating-point representation limits, and how to safely compare them.
 * 
 * KEY LESSONS:
 * - Do not use double or float for precise values (like financial currencies). Use BigDecimal instead.
 * - For double comparisons, check if the difference is within a very small threshold (epsilon, e.g. 1e-9).
 */
public class PrecisionDebug {
    public static void main(String[] args) {
        double amount = 1.00;
        double cost = 0.90;
        double epsilon = 1e-9;

        System.out.println("Remaining amount: " + (amount - cost));
        if (Math.abs((amount - cost) - 0.10) <= epsilon) {
            System.out.println("Exactly 10 cents left!");
        } else {
            System.out.println("Unexpected amount left!");
        }
    }
}
