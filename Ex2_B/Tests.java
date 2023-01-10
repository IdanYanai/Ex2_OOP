package Ex2_B;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import java.util.concurrent.*;

public class Tests {
    public static final Logger logger = LoggerFactory.getLogger(Tests.class);

    @Test
    public void partialTest(){
        CustomExecutor customExecutor = new CustomExecutor();
        var task = Task.createTask(()->{
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
            }
            return sum;
        }, TaskType.COMPUTATIONAL);
        var sumTask = customExecutor.submit(task);
        final int sum;
        try {
            sum = sumTask.get(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        logger.info(()-> "Sum of 1 through 10 = " + sum);
        Callable<Double> callable1 = ()-> {
            return 1000 * Math.pow(1.02, 5);
        };
        Callable<String> callable2 = ()-> {
            StringBuilder sb = new StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            return sb.reverse().toString();
        };
        // var is used to infer the declared type automatically
        var priceTask = customExecutor.submit(()-> {
            return 1000 * Math.pow(1.02, 5);
        }, TaskType.COMPUTATIONAL);
        var reverseTask = customExecutor.submit(callable2, TaskType.IO);
        final Double totalPrice;
        final String reversed;
        try {
            totalPrice = priceTask.get();
            reversed = reverseTask.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        logger.info(()-> "Reversed String = " + reversed);
        logger.info(()->String.valueOf("Total Price = " + totalPrice));
        logger.info(()-> "Current maximum priority = " +
                customExecutor.getCurrentMax());
        customExecutor.gracefullyTerminate();
    }

    /**
     * We fill the executor with tasks that take 3 seconds each. After that, we
     * submit even another task. Then, we shut down the executor and try to submit
     * a new task, we get an Error. Finally, we get all the results of the tasks
     * even after shutdown.
     */
    @Test
    public void testShutdown() {
        // Submitting enough tasks to fill the queue and saving their futures.
        CustomExecutor customExecutor = new CustomExecutor();
        Future<String>[] futures = new Future[Runtime.getRuntime().availableProcessors()-1];
        for(int i=0;i<futures.length;i++) {
            var task = Task.createTask(() -> {
                TimeUnit.SECONDS.sleep(3);
                return "finished";}
            );
            futures[i] = customExecutor.submit(task);
        }

        // Submitting a task after executor queue is full.
        Future<String> longTask = customExecutor.submit(Task.createTask(() -> {
            TimeUnit.SECONDS.sleep(3);
            return "finished";}
                ));

        // Shutdown executor.
        customExecutor.gracefullyTerminate();

        // Try submitting task after shutdown.
        try {
            Future<String> someTask = customExecutor.submit(Task.createTask(() -> {
                        TimeUnit.SECONDS.sleep(1);
                        return "something";
                    }
            ));
        }
        catch (RejectedExecutionException e) {
            System.out.println(e);
        }

        // Try getting all the results in the queue after shutdown.
        try {
            for (int i = 0; i < futures.length; i++)
                Assertions.assertEquals("finished", futures[i].get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // Try getting the result of the task in queue.
        try {
            Assertions.assertEquals("finished", longTask.get());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
