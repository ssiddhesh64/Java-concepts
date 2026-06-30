package com.example.concepts.solid;

/**
 * CONCEPT TAUGHT: Polymorphic Enums with Checked Exceptions
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates how to delegate behavior to enum constants using abstract methods while correctly handling custom checked exceptions in the signature.
 * 
 * KEY LESSONS:
 * - Polymorphic enums allow you to define constant-specific behaviors instead of large switch-cases.
 * - Abstract methods declared on the enum class can throw checked exceptions that each constant must implement or declare.
 */
import java.util.*;
import java.util.stream.Stream;

public class ApplyExample {
    public static void main(String[] args) throws InvalidOperationException {
        System.out.println("ADD: " + Operation.ADD.apply(1, 2));
        System.out.println("SUBTRACT: " + Operation.SUBTRACT.apply(1, 2));
        System.out.println("MULTIPLY: " + Operation.MULTIPLY.apply(1, 2));
        System.out.println("DIVIDE: " + Operation.DIVIDE.apply(1, 2));
    }

    // Nested helper class to prevent namespace conflicts
    static class InvalidOperationException extends Exception {
    InvalidOperationException(String msg) {
        super(msg);
    }
}

    // Nested helper class to prevent namespace conflicts
    static enum Operation {
    
    ADD {
        public double apply(double a, double b) {
            return a + b;
        }
    },

    SUBTRACT {
        public double apply(double a, double b) {
            return a - b;   
        }
    },

    MULTIPLY {
        public double apply(double a, double b) {
            return a * b;   
        }
    },

    DIVIDE {
        public double apply(double a, double b) throws InvalidOperationException{
            if(b == 0) {
                throw new InvalidOperationException("Cannot divide by zero");
            }
            return a / b;   
        }
    };

    public abstract double apply(double a, double b) throws InvalidOperationException;

    public static Optional<Operation> fromString(String s) {
        if (s == null) {
            return Optional.empty();
        }
        return Stream.of(values())
            .filter(op -> op.name().equalsIgnoreCase(s))
            .findFirst();
    }
}

}