package javafixes.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * A synchronization aid that allows one or more threads to wait until
 * a set of operations being performed in other threads completes.
 * <p></p>
 * <p>A {@link ReusableCountLatch} is initialized with a given <em>count</em>.
 * The {@link #waitTillZero} methods block until the current count reaches
 * zero due to invocations of the {@link #decrement} method, after which
 * all waiting threads are released. If zero has been reached any subsequent
 * invocations of {@link #waitTillZero} return immediately.
 * The coun cen be increased calling the {@link #increment()} method and
 * any subsequent thread calling the {@link #waitTillZero} method will be block
 * again until another zero is reached.
 * <p></p>
 * <p>{@link ReusableCountLatch} provides more versatility than
 * {@link java.util.concurrent.CountDownLatch CountDownLatch} as the count
 * doesn't have to be known upfront and you can reuse this class as many times
 * as you want to.
 * It is also better than a {@link java.util.concurrent.Phaser Phaser} whose count
 * is limited to 65_535. {@link ReusableCountLatch} instead can count up to
 * 2_147_483_647 (2^31-1).
 * <p></p>
 * <p>Great use case for {@link ReusableCountLatch} is when you wait for tasks on
 * other threads to finish, but these tasks could trigger more tasks and it is
 * not know upfront how many will be triggered in total. To simplify this scenario even further
 * take a look at our implementations of {@link javafixes.concurrency.Runner Runner}
 * of {@link javafixes.concurrency.MonitoringTaskSubmitter}.
 *
 * @author mtymes
 * @since 07/10/16 00:10 AM
 */
public class ReusableCountLatch {

    private final Sync sync;

    /**
     * Constructs a {@link ReusableCountLatch} initialized with the given count.
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
     * Constructs a {@link ReusableCountLatch} with initial count set to 0.
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
     * <p></p>
     * <p>If the current count is greater than zero then it is decremented.
     * If the new count is zero then all waiting threads are re-enabled for
     * thread scheduling purposes.
     * <p></p>
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


    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is {@linkplain Thread#interrupt interrupted}.
     * <p></p>
     * <p>If the current count is zero then this method returns immediately.
     * <p></p>
     * <p>If the current count is greater than zero then the current
     * thread becomes disabled for thread scheduling purposes and lies
     * dormant until one of two things happen:
     * <ul>
     * <li>The count reaches zero due to invocations of the {@link #decrement} method; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the current thread.
     * </ul>
     * <p></p>
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public void waitTillZero() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is {@linkplain Thread#interrupt interrupted},
     * or the specified waiting time elapses.
     * <p></p>
     * <p>If the current count is zero then this method returns immediately
     * with the value {@code true}.
     * <p></p>
     * <p>If the current count is greater than zero then the current
     * thread becomes disabled for thread scheduling purposes and lies
     * dormant until one of three things happen:
     * <ul>
     * <li>The count reaches zero due to invocations of the {@link #decrement()} method; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the current thread; or
     * <li>The specified waiting time elapses.
     * </ul>
     * <p></p>
     * <p>If the count reaches zero then the method returns with the value {@code true}.
     * <p></p>
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     * <p></p>
     * <p>If the specified waiting time elapses then the value {@code false}
     * is returned.  If the time is less than or equal to zero, the method
     * will not wait at all.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the {@code timeout} argument
     * @return {@code true} if the count reached zero and {@code false}
     * if the waiting time elapsed before the count reached zero
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
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
