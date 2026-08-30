package com.example.concepts.collections;

/**
 * CONCEPT TAUGHT: List SubList View Mechanics & Memory Leaks
 *
 * <p>WHY THIS WAS WRITTEN: - Shows why list.subList causes ConcurrentModificationException when the
 * parent list is modified, and explains its memory leak.
 *
 * <p>KEY LESSONS: - List.subList returns a view, not a copy. - Modifying the parent list
 * invalidates all sublist views. - SubList holds a strong reference to the parent. Detach them via
 * new ArrayList<>(subList) to prevent memory leaks.
 */
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SubListExample {

    public static List<Integer> getRandomHugeList(int size) {
        return IntStream.range(0, size)
                .map(i -> java.util.concurrent.ThreadLocalRandom.current().nextInt())
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
