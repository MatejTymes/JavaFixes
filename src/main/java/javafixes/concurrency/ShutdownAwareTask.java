package javafixes.concurrency;

import javafixes.common.function.Task;

import java.util.concurrent.Executor;

/**
 * A task that has no result and may throw an exception.
 * Implementors define a single method called {@code run} with {@link ShutdownInfo} as an argument.
 * <p>
 * <p>The {@link ShutdownAwareTask} interface is similar to {@link Task},
 * in that both are designed for classes whose instances are potentially executed by another thread.
 * A {@link ShutdownAwareTask} however has an input argument {@link ShutdownInfo} which can be asked
 * if underlying {@link Executor} has received a shutdown signal.
 * <p>
 * <p>The class is intended to be use with custom executor wrapper {@link Runner}.
 *
 * @see Runner
 * @see ShutdownAwareCallable
 * @author mtymes
 * @since 06/20/19 02:40 AM
 */
@FunctionalInterface
public interface ShutdownAwareTask {

    /**
     * Executes a task or throws an exception if unable to do so.
     *
     * @param shutdownInfo provides info if underlying {@link Executor} received a shutdown signal
     * @throws Exception if unable to execute task
     * @see Runner
     */
    void run(ShutdownInfo shutdownInfo) throws Exception;
}
