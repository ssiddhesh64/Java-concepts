import java.util.*;
import java.util.function.BiFunction;

public class MergeMaps {
    
    public static <K, V> Map<K, V> mergeMaps(
    Map<? extends K, ? extends V> first,
    Map<? extends K, ? extends V> second,
    BiFunction<? super V, ? super V, ? extends V> mergeFunction) {

        Map<K, V> result = new HashMap<>(first); // Copy constructor is cleaner than first.forEach(result::put)
        second.forEach((key, value) -> result.merge(key, value, mergeFunction));
        return result;
    }

    static BiFunction<Integer, Integer, Integer> mergeFunction = (first, second) -> first + second;

    // static BiFunction<Integer, Integer, Integer> mergeFunction2 = (first, second) -> {
    //     if(first == null && second == null) return 0;
    //     if(first == null) return second;
    //     if(second == null) return first;

    //     return Math.max(first, second);
    // };

    static BiFunction<Integer, Integer, Integer> mergeFunction2 = Math::max;

    public static Map<String, Integer> mergeGrades(Map<String, Integer> math, Map<String, Integer> science) {
        Map<String, Integer> result = new HashMap<>(Optional.ofNullable(math).orElse(Map.of()));
        Optional.ofNullable(science).orElse(Map.of()).forEach((key,value) -> result.merge(key, value, Math::max));
        return result;
    }

    public static void main(String[] args) {

        Map<Integer, Integer> map1 = Map.of(1, 10, 2, 20);
        Map<Integer, Integer> map2 = Map.of(2, 30, 3, 40);

        Map<Integer, Integer> merged =
                mergeMaps(map1, map2, mergeFunction);

        System.out.println(merged);
        
    }
}
