/**
 * CONCEPT TAUGHT: Stream partitioningBy Collector
 * 
 * WHY THIS WAS WRITTEN:
 * - Demonstrates partitioning stream elements into true/false lists based on a predicate.
 * 
 * KEY LESSONS:
 * - Collectors.partitioningBy() always returns a Map<Boolean, List<T>>.
 * - It is a specialized form of groupingBy optimized for binary conditions.
 */
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

record Employee(String name, String department, double salary){}

public class PartitionBySalary {
    
    public static Map<Boolean, List<Employee>> partitionBySalary(List<Employee> employees, double threshold) {
        return employees.stream()
        .sorted(Comparator.comparingDouble(Employee::salary).reversed())
        .collect(Collectors.partitioningBy(e -> e.salary() > threshold));

    }
}

