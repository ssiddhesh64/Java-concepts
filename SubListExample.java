import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.List;
import java.util.stream.Collectors;

public class SubListExample {

    public static List<Integer> getRandomHugeList(int size) {
        return IntStream.range(0, size)
                .map(i -> (int) (Math.random() * Integer.MAX_VALUE))
                .boxed()
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {

        List<Integer> parentList = getRandomHugeList(10);
        List<Integer> subs = parentList.subList(4, 7);

        parentList.add(100);

        System.out.println("Parent list");
        parentList.forEach(System.out::println);
        System.out.println();
        System.out.println("Sub list");
        subs.forEach(System.out::println);

        // subs.add(100);

        // parentList.add(100);

        // System.out.println("Parent list");
        // parentList.forEach(System.out::println);
        // System.out.println();
        // System.out.println("Sub list");
        // subs.forEach(System.out::println);

        // subs.add(999);
        // System.out.println("Parent list");
        // parentList.forEach(System.out::println);
        // System.out.println();
        // System.out.println("Sub list");
        // subs.forEach(System.out::println);

    }

}
