package javafixes.concurrency;

public class RunnerTest extends BaseExecutorTest {

    protected Runner executor;

    @Override
    protected void initializeExecutor() {
        // create executor with 10 threads
        executor = Runner.runner(10);
    }

    @Override
    protected void shutdownExecutor() throws InterruptedException {
        executor.shutdownAndAwaitTermination();
    }

    @Override
    protected MonitoringTaskSubmitter executor() {
        return executor;
    }
}