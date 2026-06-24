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

