package mtymes.javafixes.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author mtymes
 * @since 07/10/16 00:10 AM
 */
// todo: javadoc
public class ReusableCountLatch {

    private final Sync sync;

    /**
     * Constructs a {@code ReusableCountLatch} initialized with the given count.
     *
     * @param initialCount the number of times {@link #decrement} must be invoked before threads can pass
     *                     through {@link #waitTillZero}. For each additional call of the {@link #increment}
     *                     method one more {@link #decrement} must be called.
     * @throws IllegalArgumentException if {@code initialCount} is negative
     */
    public ReusableCountLatch(int initialCount) {
        if (initialCount < 0) {
            throw new IllegalArgumentException("negative initial count '" + initialCount + "' is not allowed");
        }
        this.sync = new Sync(initialCount);
    }

    /**
     * Constructs a {@code ReusableCountLatch} with initialCount set to 0.
     */
    public ReusableCountLatch() {
        this(0);
    }


    /**
     * Returns the current count.
     *
     * @return the current count
     */
    public int getCount() {
        return sync.getCount();
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if
     * the count reaches zero.
     *
     * <p>If the current count is greater than zero then it is decremented.
     * If the new count is zero then all waiting threads are re-enabled for
     * thread scheduling purposes.
     *
     * <p>If the current count equals zero then nothing happens.
     */
    public void decrement() {
        sync.decrement();
    }

    /**
     * Increments the count of the latch, which will make it possible to block
     * all threads waiting till count reaches zero.
     */
    public void increment() {
        sync.increment();
    }

    // todo: javadoc
    public void waitTillZero() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    // todo: javadoc
    public boolean waitTillZero(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    /**
     * Synchronization control For ReusableCountLatch.
     * Uses AQS state to represent count.
     */
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 5970133580157457018L;

        Sync(int count) {
            setState(count);
        }

        protected int getCount() {
            return getState();
        }

        protected void increment() {
            for (; ; ) {
                int oldCount = getState();
                int newCount = oldCount + 1;
                if (compareAndSetState(oldCount, newCount)) {
                    return;
                }
            }
        }

        protected void decrement() {
            releaseShared(1);
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (; ; ) {
                int oldCount = getState();
                if (oldCount == 0) {
                    return false;
                }
                int newCount = oldCount - 1;
                if (compareAndSetState(oldCount, newCount)) {
                    return newCount == 0;
                }
            }
        }
    }
}
