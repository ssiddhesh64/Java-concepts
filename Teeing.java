import java.util.*;
import java.util.stream.Collectors;

record MinMax(int min, int max) {
}

public class Teeing {

    public static MinMax findMinMax(List<Integer> numbers) {

        return numbers.stream()
                .collect(Collectors.teeing(
                        Collectors.minBy(Comparator.<Integer>naturalOrder()),
                        Collectors.maxBy(Comparator.<Integer>naturalOrder()),
                        (min, max) -> new MinMax(
                                min.orElse(Integer.MIN_VALUE),
                                max.orElse(Integer.MAX_VALUE))));
    }

    public static void main(String[] args) {
        System.out.println(findMinMax(List.of(1, 2, 3, 4, 5)));
    }
}