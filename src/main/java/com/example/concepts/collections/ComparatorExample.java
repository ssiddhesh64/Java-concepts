package com.example.concepts.collections;

/**
 * CONCEPT TAUGHT: Java 8+ Comparator Chaining
 * 
 * WHY THIS WAS WRITTEN:
 * - Shows how to chain multiple sort parameters in a declarative manner.
 * 
 * KEY LESSONS:
 * - Comparator.comparing() and thenComparing() chain sort orders.
 * - Use Comparator.reverseOrder() or reversed() for descending sorts.
 */
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ComparatorExample {

    static Comparator<Employee> cmp = Comparator.comparing(Employee::dept)
            .thenComparing(Employee::salary, Comparator.reverseOrder())
            .thenComparing(Employee::name);

    public static void main(String[] args) {

        List<Employee> employees = Arrays.asList(
                new Employee("HR", "John", 50000),
                new Employee("Engineering", "Jane", 60000),
                new Employee("HR", "Bob", 55000),
                new Employee("Engineering", "Alice", 65000));

        employees.sort(cmp);
        System.out.println(employees);
    }

    // Nested helper class to prevent namespace conflicts
    static record Employee(String dept, String name, double salary) {
    }

}