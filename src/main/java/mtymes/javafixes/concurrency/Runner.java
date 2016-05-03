package mtymes.javafixes.concurrency;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Runner extends MonitoringTaskSubmitter {

    public Runner(int threadCount) {
        super(newScheduledThreadPool(threadCount));
    }

    public static Runner runner(int threadCount) {
        return new Runner(threadCount);
    }


    public Runner shutdown() {
        executor.shutdown();
        return this;
    }

    public Runner shutdownNow() {
        executor.shutdownNow();
        return this;
    }

    public Runner awaitTermination(long timeout, TimeUnit unit) {
        try {
            executor.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public Runner shutdownAndAwaitTermination(long timeout, TimeUnit unit) {
        return shutdown().awaitTermination(timeout, unit);
    }

    public Runner shutdownAndAwaitTermination() {
        return shutdownAndAwaitTermination(5, SECONDS);
    }

    public Runner shutdownNowAndAwaitTermination(long timeout, TimeUnit unit) {
        return shutdownNow().awaitTermination(timeout, unit);
    }

    public Runner shutdownNowAndAwaitTermination() {
        return shutdownNowAndAwaitTermination(5, SECONDS);
    }
}
