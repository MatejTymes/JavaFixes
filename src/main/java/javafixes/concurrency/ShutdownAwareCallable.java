package javafixes.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * A task that returns a result and may throw an exception.
 * Implementors define a single method called {@code call} with {@link ShutdownInfo} as an argument.
 * <p></p>
 * <p>The {@link ShutdownAwareCallable} interface is similar to {@link Callable},
 * in that both are designed for classes whose instances are potentially executed by another thread.
 * A {@link ShutdownAwareCallable} however has an input argument {@link ShutdownInfo} which can be asked
 * if underlying {@link Executor} has received a shutdown signal.
 * <p></p>
 * <p>The class is intended to be use with custom executor wrapper {@link Runner}.
 *
 * @see Runner
 * @see ShutdownAwareCallable
 * @author mtymes
 * @since 06/20/19 02:40 AM
 * @param <V> the result type of method {@code call}
 */
@FunctionalInterface
public interface ShutdownAwareCallable<V> {

    /**
     * Computes a result or throws an exception if unable to do so.
     *
     * @param shutdownInfo provides info if underlying {@link Executor} received a shutdown signal
     * @return computed result
     * @throws Exception if unable to compute a result
     * @see Runner
     */
    V call(ShutdownInfo shutdownInfo) throws Exception;
}
