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

}

record Employee(String dept, String name, double salary) {
}
