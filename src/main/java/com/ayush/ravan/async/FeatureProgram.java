package com.ayush.ravan.async;

import java.util.concurrent.*;

public class FeatureProgram {

    public static void main(String[] args) {
        Callable<Integer> task = () -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                return 1;
            } catch (InterruptedException e) {}
            return null;
        };
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Integer>  future =  executor.submit(task);
        System.out.println("isDone = "+ future.isDone());
        Integer result = null;
        try {
            result = future.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("isDone ? " + future.isDone());
        System.out.print("result: " + result);
    }
}
