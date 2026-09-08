package com.example.concepts.functionalInterfaces;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RetryExecutor {

    static boolean alwaysFail = true;
    public static void main(String[] args) {

        String res = executeWithRetry(() -> callExternalService(), 3, retry -> System.out.println("Retrying: " + retry));
        System.out.println(res);
    }

    static <T> T executeWithRetry(Supplier<T> function, Integer retryCount, Consumer<Integer> printRetryCount) {

        int attempt = 0;
        while (attempt <= retryCount) {
            try {
                return function.get();
            } catch (Exception ex) {
                if(attempt == retryCount) throw ex;
                attempt++;
                printRetryCount.accept(attempt);

            }
        }
        throw new IllegalStateException("Unreachable");
    }

    private static String callExternalService() {

        Random random = new Random();
        Integer r = random.nextInt(4);
        System.out.println("Random val is " + r);
        if(alwaysFail || r % 3 == 0) throw new RuntimeException("External Service failed");

        System.out.println("External service executed successfully!!");
        return "external service success!!";
    }
}
