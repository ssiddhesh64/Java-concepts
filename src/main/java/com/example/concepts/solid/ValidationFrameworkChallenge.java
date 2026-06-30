package com.example.concepts.solid;

/**
 * CONCEPT TAUGHT: Reflection-Based Annotation Validation
 * 
 * WHY THIS WAS WRITTEN:
 * - Builds a custom framework that validates record constraints using Reflection and custom annotations.
 * 
 * KEY LESSONS:
 * - Reflection can read field annotations at runtime.
 * - Useful for building validation logic similar to Jakarta Bean Validation.
 */
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.*;

public class ValidationFrameworkChallenge {

    /**
     * Implement this method to validate an object using reflection:
     * 
     * 1. If the object's class is not annotated with `@Validate`, throw an 
     *    `IllegalArgumentException("Class must be annotated with @Validate")`.
     * 2. Inspect all declared fields of the object (including private ones!).
     * 3. For each field:
     *    - If annotated with `@NotBlank`: Ensure the value is a non-null, non-empty, non-whitespace String.
     *      If validation fails, add an error formatted as: "fieldName: message"
     *    - If annotated with `@Min`: Ensure the value (which is a Number/integer) is >= the annotation's value.
     *      If validation fails, add an error formatted as: "fieldName: message value" (e.g. "age: must be at least 18")
     *    - If annotated with `@Email`: Ensure the value is a String containing "@".
     *      If validation fails or is null, add an error formatted as: "fieldName: message"
     * 4. Return a List containing all validation error strings. Return an empty list if there are no errors.
     * 
     * @param obj the object to validate
     * @return list of validation error messages
     * @throws IllegalAccessException if field access is restricted
     */
    public static List<String> validate(Object obj) throws IllegalAccessException {
        // TODO: Implement reflection-based validation

        Class<?> c = obj.getClass();
        if(!c.isAnnotationPresent(Validate.class)) {
            throw new IllegalArgumentException("Class must be annotated with @Validate");
        }

        Field[] fields = c.getDeclaredFields();

        List<String> errors = new ArrayList<>();
        for(Field field : fields) {
            field.setAccessible(true);

            if(field.isAnnotationPresent(NotBlank.class)) {
                NotBlank notBlank = field.getAnnotation(NotBlank.class);
                Object rawValue = field.get(obj);
                if(rawValue == null || !(rawValue instanceof String) || ((String) rawValue).trim().isEmpty()) {
                    errors.add(field.getName() + ": " + notBlank.message());
                }
            } 

            if(field.isAnnotationPresent(Min.class)) {
                Min min = field.getAnnotation(Min.class);
                Object rawValue = field.get(obj);
                if(rawValue == null || !(rawValue instanceof Number) || ((Number) rawValue).longValue() < min.value()) {
                    errors.add(field.getName() + ": " + min.message() + " " + min.value());
                }
            }
            
            if(field.isAnnotationPresent(Email.class)) {
                Email email = field.getAnnotation(Email.class);
                Object rawValue = field.get(obj);
                if(rawValue == null || !(rawValue instanceof String) || !((String) rawValue).contains("@")) {
                    errors.add(field.getName() + ": " + email.message());
                }
            }
        }
        return errors;
    }

    // Test Harness
    public static void main(String[] args) {
        System.out.println("=== Starting Validation Framework Tests ===");

        // Test 1: Valid User
        System.out.println("\n--- Test 1: Valid User ---");
        try {
            User validUser = new User("JohnDoe", 20, "john@example.com");
            List<String> errors = validate(validUser);
            System.out.println("Errors: " + errors);
            if (errors.isEmpty()) {
                System.out.println("SUCCESS");
            } else {
                System.err.println("FAILURE: Expected no errors but got " + errors);
            }
        } catch (Exception e) {
            System.err.println("Test 1 Failed with Exception: " + e.getMessage());
        }

        // Test 2: Invalid User (All validations fail)
        System.out.println("\n--- Test 2: Invalid User (All validations fail) ---");
        try {
            User invalidUser = new User("  ", 15, "invalid-email");
            List<String> errors = validate(invalidUser);
            System.out.println("Errors: " + errors);

            boolean hasUsernameError = errors.stream().anyMatch(e -> e.contains("username: Username cannot be empty"));
            boolean hasAgeError = errors.stream().anyMatch(e -> e.contains("age: Age must be at least 18"));
            boolean hasEmailError = errors.stream().anyMatch(e -> e.contains("email: Email must be valid"));

            if (errors.size() == 3 && hasUsernameError && hasAgeError && hasEmailError) {
                System.out.println("SUCCESS");
            } else {
                System.err.println("FAILURE: Validation errors did not match expectations.");
            }
        } catch (Exception e) {
            System.err.println("Test 2 Failed with Exception: " + e.getMessage());
        }

        // Test 3: Unannotated Class
        System.out.println("\n--- Test 3: Class without @Validate annotation ---");
        try {
            validate(new UnvalidatedClass());
            System.err.println("Test 3 Failed: Expected IllegalArgumentException but execution succeeded");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
            System.out.println("SUCCESS");
        } catch (Exception e) {
            System.err.println("Test 3 Failed with wrong Exception: " + e.getClass().getName());
        }
    }

    // Nested helper class to prevent namespace conflicts
    // Define Custom Annotations
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
static @interface Validate {}

    // Nested helper class to prevent namespace conflicts
    @Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
static @interface NotBlank {
    String message() default "must not be blank";
}

    // Nested helper class to prevent namespace conflicts
    @Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
static @interface Min {
    int value();
    String message() default "must be at least";
}

    // Nested helper class to prevent namespace conflicts
    @Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
static @interface Email {
    String message() default "must be a valid email";
}

    // Nested helper class to prevent namespace conflicts
    // Sample Class to Validate
@Validate
static class User {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Min(value = 18, message = "Age must be at least")
    private int age;

    @Email(message = "Email must be valid")
    private String email;

    public User(String username, int age, String email) {
        this.username = username;
        this.age = age;
        this.email = email;
    }
}

    // Nested helper class to prevent namespace conflicts
    // Non-annotated Class to test validation rejection
static class UnvalidatedClass {
    private String name = "test";
}

}