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
