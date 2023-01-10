package Ex2_B;

import java.util.Comparator;
import java.util.concurrent.Callable;

public class Task<T> implements Callable<T>, Comparable<Task<?>> {
    private final TaskType type;
    private final Callable<T> func;

    private Task(Callable<T> func, TaskType type) {
        this.type = type;
        this.func = func;
    }

    public static <T> Task<T> createTask(Callable<T> func, TaskType type) {
        return new Task<T>(func, type);
    }

    public static <T> Task<T> createTask(Callable<T> func) {
        return new Task<T>(func, TaskType.OTHER);
    }

    public Callable<T> getFunc() {
        return func;
    }

    public TaskType getType() {
        return type;
    }

    @Override
    public T call() throws Exception {
        return func.call();
    }

    @Override
    public int compareTo(Task<?> o) {
        return o.getType().getPriorityValue() - this.getType().getPriorityValue();
    }
}
