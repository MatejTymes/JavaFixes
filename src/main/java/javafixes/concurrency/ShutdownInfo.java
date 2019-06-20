package javafixes.concurrency;

import java.util.concurrent.Executor;

/**
 * Allows anyone to figure out if a shutdown has been triggered on an executor.
 * This is useful in case a thread missed a shutdown signal as it can die when it does a check on this interface.
 *
 * @author mtymes
 */
public interface ShutdownInfo {

    /**
     * @return provides information if a shutdown signal has been send to underlying {@link Executor}
     */
    boolean wasShutdownTriggered();
}
