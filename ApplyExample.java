import java.util.*;
import java.util.stream.Stream;
class InvalidOperationException extends Exception{
    InvalidOperationException(String msg) {
        super(msg);
    }
}

enum Operation {
    
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

public class ApplyExample {
    public static void main(String[] args) throws InvalidOperationException {
        System.out.println("ADD: " + Operation.ADD.apply(1, 2));
        System.out.println("SUBTRACT: " + Operation.SUBTRACT.apply(1, 2));
        System.out.println("MULTIPLY: " + Operation.MULTIPLY.apply(1, 2));
        System.out.println("DIVIDE: " + Operation.DIVIDE.apply(1, 2));
    }
}
