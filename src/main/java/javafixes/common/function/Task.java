package javafixes.common.function;

import javafixes.concurrency.MonitoringTaskSubmitter;
import javafixes.concurrency.Runner;

/**
 * A task that has no result and may throw an exception.
 * Implementors define a single method with no arguments called {@code run}.
 * <p></p>
 * <p>The {@link Task} interface is similar to {@link Runnable},
 * in that both are designed for classes whose instances are potentially
 * executed by another thread.
 * A {@link Runnable} however can not throw a checked exception.
 * <p></p>
 * <p>The class is intended to be use with custom executor wrappers {@link Runner} and {@link MonitoringTaskSubmitter}.
 *
 * @see Runner
 * @see MonitoringTaskSubmitter
 * @author mtymes
 * @since 10/22/14 11:07 PM
 */
@FunctionalInterface
public interface Task {

    /**
     * Executes a task or throws an exception if unable to do so.
     *
     * @throws Exception if unable to execute task
     * @see Runner
     * @see MonitoringTaskSubmitter
     */
    void run() throws Exception;
}
