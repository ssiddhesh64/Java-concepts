import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.*;

record Employee(String name, String department, double salary) {

    Employee {
        if (name == null) {
            throw new IllegalArgumentException("name is mandatory");
        }
        if (department == null) {
            throw new IllegalArgumentException("department is mandatory");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("salary is mandatory");
        }
    }
}

record User(String id, Profile profile) {
}

record Profile(String name, Address address) {
}

record Address(String city, String zipCode) {
}

public class StreamPractice {

    public static Map<String, Double> getAverageSalaryByDept(List<Employee> employees) {
        return Optional.ofNullable(employees)
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Employee::department, Collectors.averagingDouble(Employee::salary)));
    }

    public static Optional<String> getCityForUser(User user) {
        return Optional.ofNullable(user)
                .map(User::profile)
                .map(Profile::address)
                .map(Address::city)
                .filter(city -> !city.isBlank());
    }

    public static boolean checkList(Stream<String> strs) {
        return strs
                .anyMatch(str -> str != null && !str.isBlank() && str.startsWith("J") && str.length() > 5);
    }

    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee("John", "Sales", 50000),
                new Employee("Jane", "HR", 60000),
                new Employee("Bob", "Sales", 70000),
                new Employee("Alice", "HR", 80000),
                null);

        System.out.println(getAverageSalaryByDept(employees));
        // --------------------------------------------------

        User user1 = new User("1",
                new Profile("John",
                        new Address("Mumbai", "400001")));

        User user2 = new User("2",
                new Profile("Jane",
                        new Address("   ", "400002")));

        User user3 = new User("3", null);

        System.out.println(getCityForUser(user1)); // Optional[Mumbai]
        System.out.println(getCityForUser(user2)); // Optional.empty
        System.out.println(getCityForUser(user3)); // Optional.empty
        System.out.println(getCityForUser(null)); // Optional.empty
        // --------------------------------------------------

    }
}
