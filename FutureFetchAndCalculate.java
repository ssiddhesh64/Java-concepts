
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FutureFetchAndCalculate {
    
    public static CompletableFuture<Double> fetchAndCalculate(
        Supplier<Double> source1,
        Supplier<Double> source2,
        BiFunction<Double, Double, Double> combiner
    ) {
        CompletableFuture<Double> future1 = CompletableFuture.supplyAsync(source1);
        CompletableFuture<Double> future2 = CompletableFuture.supplyAsync(source2);
        
        return future1.thenCombine(future2, combiner)
        .whenComplete((res, ex) -> {
            if(ex != null) {
                System.out.println("Computation Failed: " + ex.getMessage());
                // Exception will be thrown automatically
                // try {
                //     throw ex;
                // } catch (Throwable ex1) {
                //     System.getLogger(FutureFetchAndCalculate.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex1);
                // }
            }
        });
    }
}
