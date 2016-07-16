package mtymes.javafixes.concurrency;

/**
 * A task that has no result and may throw an exception.
 * Implementors define a single method with no arguments called {@code run}.
 * <p>
 * <p>The {@code Task} interface is similar to {@link java.lang.Runnable},
 * in that both are designed for classes whose instances are potentially
 * executed by another thread.
 * A {@code Runnable} however can not throw a checked exception.
 * <p>
 * <p>The class is intended to be use with custom executor wrappers
 * {@link mtymes.javafixes.concurrency.Runner} and
 * {@link mtymes.javafixes.concurrency.MonitoringTaskSubmitter}.
 *
 * @author mtymes
 * @since 10/22/14 11:07 PM
 */
@FunctionalInterface
public interface Task {

    /**
     * Executes a task or throws an exception if unable to do so.
     *
     * @throws Exception if unable to execute task
     * @see mtymes.javafixes.concurrency.Runner
     * @see mtymes.javafixes.concurrency.MonitoringTaskSubmitter
     */
    void run() throws Exception;
}
