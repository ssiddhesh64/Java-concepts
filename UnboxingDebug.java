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
