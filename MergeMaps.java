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

    public static void main(String[] args) {

        Map<Integer, Integer> map1 = Map.of(1, 10, 2, 20);
        Map<Integer, Integer> map2 = Map.of(2, 30, 3, 40);

        Map<Integer, Integer> merged =
                mergeMaps(map1, map2, mergeFunction);

        System.out.println(merged);
        
    }
}
