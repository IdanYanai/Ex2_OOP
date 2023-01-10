package Ex2_B;

import java.util.Objects;
import java.util.concurrent.*;

public class CustomExecutor{
    private final ExecutorService exe; // the custom executor.
    private int currentMax; // holds current max priority in queue.
    private boolean isShutdown;

    /**
     * Constructor
     */
    public CustomExecutor() {
        isShutdown = false;
        int corePoolSize = Runtime.getRuntime().availableProcessors()/2;
        int maxPoolSize = Runtime.getRuntime().availableProcessors()-1;
        PriorityBlockingQueue<Runnable> q = new PriorityBlockingQueue<>(Runtime.getRuntime().availableProcessors()-1, new TaskComparator());
        exe = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 300L, TimeUnit.MILLISECONDS, q);
    }

    /**
     * Submitting a Task type to the executor.
     * @param t Task to execute.
     * @param <T> return type of Task.
     * @return Future value of Task.
     */
    public <T> Future<T> submit(Task<T> t) {
        if(isShutdown)
            throw new RejectedExecutionException("Executor already shutdown");
        if(t.getType().getPriorityValue() > currentMax)
            currentMax = t.getType().getPriorityValue();
        return exe.submit(t.getFunc());
    }

    /**
     * Creates a new Task with a given function and a TaskType and submitting it
     * to the executor.
     * @param func function to execute.
     * @param type TaskType priority
     * @param <T> return type of the function.
     * @return Future value of Task.
     */
    public <T> Future<T> submit(Callable<T> func, TaskType type) {
        if(isShutdown)
            throw new RejectedExecutionException("Executor already shutdown");
        Task<T> t = Task.createTask(func, type);
        if(type.getPriorityValue() > currentMax)
            currentMax = type.getPriorityValue();
        return exe.submit(t.getFunc());
    }

    /**
     * Creates a new Task with a given Callable function and a default TaskType.OTHER.
     * And then submitting it to the executor.
     * @param func function to execute.
     * @param <T> return type of the function.
     * @return Future value of Task.
     */
    public <T> Future<T> submit(Callable<T> func) {
        if(isShutdown)
            throw new RejectedExecutionException("Executor already shutdown");
        Task<T> t = Task.createTask(func, TaskType.OTHER);
        if(TaskType.OTHER.getPriorityValue() > currentMax)
            currentMax = TaskType.OTHER.getPriorityValue();
        return exe.submit(t.getFunc());
    }

    /**
     * Getter for currentMax
     * @return current max priority in queue.
     */
    public int getCurrentMax() {
        return currentMax;
    }

    /**
     * Disables submitting any further Tasks to the executor. Finishes all the
     * remaining Tasks left in queue and running, before finally shutting down.
     */
    public void gracefullyTerminate() {
        exe.shutdown();
        isShutdown = true;
    }

    @Override
    public String toString() {
        return "CustomExecutor{" +
                "exe=" + exe +
                ", currentMax=" + currentMax +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomExecutor that = (CustomExecutor) o;
        return currentMax == that.currentMax && Objects.equals(exe, that.exe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exe, currentMax);
    }
}
