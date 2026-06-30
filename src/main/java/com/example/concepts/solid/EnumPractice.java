package com.example.concepts.solid;

public class EnumPractice {
    
    public static void main(String[] args) {
        
        BillingPlan plan = BillingPlan.USAGE_BASED;
        System.out.println(plan.calculateBill(100, 10));
    }

    // Nested helper class to prevent namespace conflicts
    /**
 * CONCEPT TAUGHT: Polymorphic Enum Pattern
 * 
 * WHY THIS WAS WRITTEN:
 * - Shows how to implement constant-specific behavior in Enums using abstract methods.
 * 
 * KEY LESSONS:
 * - Enums can declare abstract methods implemented by each constant.
 * - Align method visibility (e.g. public abstract) to prevent scope narrowing warnings.
 */
static enum BillingPlan {

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

}