import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

record Transaction(String merchant, String category, double amount) {}

public class GroupBy {

    public static Map<String, Double> getAverageAmountByCategory(List<Transaction> transactions) {

        if(transactions == null || transactions.isEmpty()) {
            return Map.of();
        }

        Map<String, Double> res = transactions.stream()
        .collect(Collectors.groupingBy(Transaction::category, Collectors.averagingDouble(Transaction::amount)));

        return res;
    } 
    
    public static void main(String[] args) {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("Amazon", "Electronics", 1000),
                new Transaction("Flipkart", "Electronics", 2000),
                new Transaction("Myntra", "Fashion", 3000),
                new Transaction("Ajio", "Fashion", 4000),
                new Transaction("Amazon", "Fashion", 5000));

                
    }
}
