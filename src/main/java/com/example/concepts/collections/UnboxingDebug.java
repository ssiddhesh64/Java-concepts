package com.example.concepts.collections;

/**
 * CONCEPT TAUGHT: Auto-Unboxing NullPointerExceptions
 * 
 * WHY THIS WAS WRITTEN:
 * - Exposes how compiler auto-unboxing of wrapper nulls causes unexpected NullPointerExceptions.
 * 
 * KEY LESSONS:
 * - Unboxing a null wrapper (e.g. Integer which is null into a primitive int) throws NullPointerException.
 * - Check for null before assigning wrappers to primitives.
 */
import java.util.*;

public class UnboxingDebug {
    public static void main(String[] args) {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Maths", 90);
        scores.put("Science", null);
        
        Optional<Integer> scienceScore = Optional.ofNullable(scores.get("Science"));        
        System.out.println("Science Score: " + scienceScore.orElse(0));
    }

}