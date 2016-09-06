package javafixes.concurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonitoringTaskSubmitterTest extends BaseExecutorTest {

    private MonitoringTaskSubmitter executor;
    private ScheduledExecutorService scheduledExecutor;

    @Override
    protected void initializeExecutor() {
        scheduledExecutor = Executors.newScheduledThreadPool(10);
        executor = new MonitoringTaskSubmitter(scheduledExecutor);
    }

    @Override
    protected void shutdownExecutor() throws InterruptedException {
        scheduledExecutor.shutdown();
        scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Override
    protected MonitoringTaskSubmitter executor() {
        return executor;
    }
}