import java.util.*;

public class HashSetDebug {
    public static void main(String[] args) {
        Set<Employee> employees = new HashSet<>();
        
        Employee emp1 = new Employee(1, "John");
        Employee emp2 = new Employee(1, "John");
        
        employees.add(emp1);
        employees.add(emp2);
        
        System.out.println("Set size: " + employees.size());
    }
}

class Employee {
    private int id;
    private String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id && Objects.equals(name, employee.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Employee [id=" + id + ", name=" + name + "]";
    }
}
