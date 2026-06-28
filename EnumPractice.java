enum BillingPlan {

    FLAT_RATE {
        @Override
        public double calculateBill(double baseAmount, int usageCount) {
            return baseAmount;
        }
    },

    USAGE_BASED {
        @Override
        public double calculateBill(double baseAmount, int usageCount) {
            return baseAmount + (usageCount * 1.5);
        }
    };

    public abstract double calculateBill(double baseAmount, int usageCount);
}

public class EnumPractice {
    
    public static void main(String[] args) {
        
        BillingPlan plan = BillingPlan.USAGE_BASED;
        System.out.println(plan.calculateBill(100, 10));
    }
}
