package javafixes.concurrency;

/**
 * Allows anyone to figure out if a shutdown has been triggered on an executor.
 * This is useful in case a thread missed a shutdown signal as it can
 *
 * @author mtymes
 */
public interface ShutdownInfo {

    /**
     * @return provides information if a shutdown signal has been received
     */
    boolean wasShutdownTriggered();
}
