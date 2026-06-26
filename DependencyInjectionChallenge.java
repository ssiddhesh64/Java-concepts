import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

// Custom Annotations
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Component {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Autowired {}

// Managed Components
@Component
class DatabaseConnection {
    public String query() {
        return "Database Query Result";
    }
}

@Component
class PaymentService {
    @Autowired
    private DatabaseConnection db;

    public String processPayment() {
        return "Payment processed. DB query: " + db.query();
    }
}

@Component
class OrderService {
    @Autowired
    private PaymentService paymentService;

    public String placeOrder() {
        return "Order Placed. " + paymentService.processPayment();
    }
}

// Unannotated class for testing validation
class UnmanagedClass {}

public class DependencyInjectionChallenge {

    public static class DiContainer {
        private final Map<Class<?>, Object> beans = new HashMap<>();
        private final Set<Class<?>> registeredClasses = new HashSet<>();

        public void register(Class<?> clazz) {
            registeredClasses.add(clazz);
        }

        public <T> T getBean(Class<T> clazz) {
            return clazz.cast(beans.get(clazz));
        }

        /**
         * Implement this method to build a lightweight DI container:
         * 
         * 1. Validation: Verify that all registered classes in `registeredClasses` are 
         *    annotated with `@Component`. If any class is not, throw an 
         *    `IllegalArgumentException("Class must be annotated with @Component")`.
         * 2. Instantiation: Create a new instance of each registered class using its default constructor, 
         *    and store it in the `beans` map mapping Class to Instance.
         * 3. Injection: For each instantiated bean, search all of its declared fields 
         *    for the `@Autowired` annotation.
         * 4. Resolution: For each `@Autowired` field, locate the dependency bean in your `beans` map.
         *    - If a matching bean of the correct type is found, set it in the field using reflection.
         *    - If no matching bean is found, throw a `NoSuchElementException("No dependency found of type...")`.
         * 
         * @throws Exception if instantiation or field injection fails
         */
        public void instantiateAndInject() throws Exception {
            // TODO: Implement the DI container lifecycle
            for(Class<?> clazz : registeredClasses) {
                if(!clazz.isAnnotationPresent(Component.class)) {
                    throw new IllegalArgumentException("Class must be annotated with @Component");
                }
                beans.put(clazz, clazz.getDeclaredConstructor().newInstance());
            }

            for(Class<?> clazz : registeredClasses) {
                Object instance = beans.get(clazz);
                Field[] fields = clazz.getDeclaredFields();
                for(Field field : fields) {
                    if(field.isAnnotationPresent(Autowired.class)) {
                        Class<?> fieldType = field.getType();
                        Object dependency = beans.get(fieldType);
                        if(dependency == null) {
                            throw new NoSuchElementException("No dependency found of type " + fieldType.getName());
                        }
                        field.setAccessible(true);
                        field.set(instance, dependency);
                    }
                }
            }
        }
    }

    // Test Harness
    public static void main(String[] args) {
        System.out.println("=== Starting Dependency Injection Container Tests ===");

        // Test 1: Successful DI wiring
        System.out.println("\n--- Test 1: Successful DI Wiring ---");
        try {
            DiContainer container = new DiContainer();
            container.register(OrderService.class);
            container.register(PaymentService.class);
            container.register(DatabaseConnection.class);

            container.instantiateAndInject();

            OrderService orderService = container.getBean(OrderService.class);
            String output = orderService.placeOrder();
            System.out.println("Output: " + output);

            String expected = "Order Placed. Payment processed. DB query: Database Query Result";
            if (expected.equals(output)) {
                System.out.println("SUCCESS");
            } else {
                System.err.println("FAILURE: Output does not match expected result");
            }
        } catch (Exception e) {
            System.err.println("Test 1 Failed with Exception:");
            e.printStackTrace();
        }

        // Test 2: Validation of @Component annotation
        System.out.println("\n--- Test 2: Validation of @Component annotation ---");
        try {
            DiContainer container = new DiContainer();
            container.register(UnmanagedClass.class);
            container.instantiateAndInject();
            System.err.println("Test 2 Failed: Expected IllegalArgumentException but completed successfully");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
            System.out.println("SUCCESS");
        } catch (Exception e) {
            System.err.println("Test 2 Failed with wrong Exception: " + e.getClass().getName());
        }

        // Test 3: Unregistered Dependency Injection Error
        System.out.println("\n--- Test 3: Unregistered Dependency Injection Error ---");
        try {
            DiContainer container = new DiContainer();
            container.register(OrderService.class);
            // Missing DatabaseConnection and PaymentService registration
            container.instantiateAndInject();
            System.err.println("Test 3 Failed: Expected NoSuchElementException but completed successfully");
        } catch (NoSuchElementException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
            System.out.println("SUCCESS");
        } catch (Exception e) {
            System.err.println("Test 3 Failed with wrong Exception: " + e.getClass().getName());
        }
    }
}
