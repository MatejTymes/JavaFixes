package javafixes.concurrency;

import javafixes.common.exception.WrappedException;
import javafixes.common.function.Task;
import javafixes.object.Tuple;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

import static javafixes.object.Tuple.tuple;

/**
 * A class that allows you to synchronize code on different objects (ids)
 * for whom the .equals() method returns true
 *
 * @author mtymes
 */
// todo: maybe rename to Locker<K>.
public class Synchronizer<K> {

    private final Map<K, Tuple<AtomicInteger, StampedLock>> counterWithLocks = new ConcurrentHashMap<>();

    /**
     * Executes the provided {@link Callable} and returning its output value,
     * while making sure that only one action can be run for the provided {@code key}
     *
     * @param key value should be used for synchronization/locking purposes
     * @param action action that should be executed
     * @param <T> actions response value type
     *
     * @throws WrappedException any exception thrown from the provided {@code action} is wrapped into a {@link WrappedException}
     *
     * @return response generated from the provided {@code action} parameter
     */
    public <T> T synchronizeOn(K key, Callable<T> action) throws WrappedException {
        StampedLock lock = acquireLock(key);
        try {
            long stamp = lock.writeLock();
            try {
                return action.call();
            } catch (Exception e) {
                throw new WrappedException("Failed to execute action", e);
            } finally {
                lock.unlock(stamp);
            }
        } finally {
            releaseLock(key);
        }
    }

    /**
     * Will attempt to executes the provided {@link Callable} and return its output value,
     * while making sure that only one action can be run for the provided {@code key}.
     * In case the lock can't be acquired within given time an {@link TimeoutException} is thrown.
     *
     * @param key value should be used for synchronization/locking purposes
     * @param time the maximum time to wait for the lock
     * @param unit the time unit of the {@code time} argument
     * @param action action that should be executed
     * @param <T> actions response value type
     *
     * @throws TimeoutException if we can't acquire lock within the defined time
     * @throws InterruptedException if the current thread is interrupted before acquiring the lock
     * @throws WrappedException any exception thrown from the provided {@code action} is wrapped into a {@link WrappedException}
     *
     * @return response generated from the provided {@code action} parameter
     */
    public <T> T synchronizeOn(K key, long time, TimeUnit unit, Callable<T> action) throws TimeoutException, InterruptedException, WrappedException {
        StampedLock lock = acquireLock(key);
        try {
            long stamp = lock.tryWriteLock(time, unit);
            if (stamp == 0) {
                throw new TimeoutException("Timed out while acquiring lock for key: " + key);
            }

            try {
                return action.call();
            } catch (Exception e) {
                throw new WrappedException("Failed to execute action", e);
            } finally {
                lock.unlock(stamp);
            }
        } finally {
            releaseLock(key);
        }
    }

    /**
     * Executes the provided {@link Runnable} while making sure that only one action
     * can be run for the provided {@code key}
     *
     * @param key value should be used for synchronization/locking purposes
     * @param action action that should be executed
     *
     * @throws WrappedException any exception thrown from the provided {@code action} is wrapped into a {@link WrappedException}
     */
    public void synchronizeRunnableOn(K key, Runnable action) throws WrappedException {
        StampedLock lock = acquireLock(key);
        try {
            long stamp = lock.writeLock();
            try {
                action.run();
            } catch (Exception e) {
                throw new WrappedException("Failed to execute action", e);
            } finally {
                lock.unlock(stamp);
            }
        } finally {
            releaseLock(key);
        }
    }

    /**
     * Will attempt to executes the provided {@link Runnable} while making sure that only one action
     * can be run for the provided {@code key}.
     * In case the lock can't be acquired within given time an {@link TimeoutException} is thrown.
     *
     * @param key value should be used for synchronization/locking purposes
     * @param time the maximum time to wait for the lock
     * @param unit the time unit of the {@code time} argument
     * @param action action that should be executed
     *
     * @throws TimeoutException if we can't acquire lock within the defined time
     * @throws InterruptedException if the current thread is interrupted before acquiring the lock
     * @throws WrappedException any exception thrown from the provided {@code action} is wrapped into a {@link WrappedException}
     *
     */
    public void synchronizeRunnableOn(K key, long time, TimeUnit unit, Runnable action) throws TimeoutException, InterruptedException, WrappedException {
        StampedLock lock = acquireLock(key);
        try {
            long stamp = lock.tryWriteLock(time, unit);
            if (stamp == 0) {
                throw new TimeoutException("Timed out while acquiring lock for key: " + key);
            }

            try {
                action.run();
            } catch (Exception e) {
                throw new WrappedException("Failed to execute action", e);
            } finally {
                lock.unlock(stamp);
            }
        } finally {
            releaseLock(key);
        }
    }

    /**
     * Executes the provided {@link Task} while making sure that only one action
     * can be run for the provided {@code key}
     *
     * @param key value should be used for synchronization/locking purposes
     * @param action action that should be executed
     *
     * @throws WrappedException any exception thrown from the provided {@code action} is wrapped into a {@link WrappedException}
     */
    public void synchronizeOn(K key, Task action) throws WrappedException {
        StampedLock lock = acquireLock(key);
        try {
            long stamp = lock.writeLock();
            try {
                action.run();
            } catch (Exception e) {
                throw new WrappedException("Failed to execute action", e);
            } finally {
                lock.unlock(stamp);
            }
        } finally {
            releaseLock(key);
        }
    }

    /**
     * Will attempt to executes the provided {@link Task} while making sure that only one action
     * can be run for the provided {@code key}.
     * In case the lock can't be acquired within given time an {@link TimeoutException} is thrown.
     *
     * @param key value should be used for synchronization/locking purposes
     * @param time the maximum time to wait for the lock
     * @param unit the time unit of the {@code time} argument
     * @param action action that should be executed
     *
     * @throws TimeoutException if we can't acquire lock within the defined time
     * @throws InterruptedException if the current thread is interrupted before acquiring the lock
     * @throws WrappedException any exception thrown from the provided {@code action} is wrapped into a {@link WrappedException}
     */
    public void synchronizeOn(K key, long time, TimeUnit unit, Task action) throws TimeoutException, InterruptedException, WrappedException {
        StampedLock lock = acquireLock(key);
        try {
            long stamp = lock.tryWriteLock(time, unit);
            if (stamp == 0) {
                throw new TimeoutException("Timed out while acquiring lock for key: " + key);
            }

            try {
                action.run();
            } catch (Exception e) {
                throw new WrappedException("Failed to execute action", e);
            } finally {
                lock.unlock(stamp);
            }
        } finally {
            releaseLock(key);
        }
    }

    private StampedLock acquireLock(K key) {
        synchronized (counterWithLocks) {
            Tuple<AtomicInteger, StampedLock> counterWithLock = counterWithLocks.computeIfAbsent(
                    key,
                    k -> tuple(new AtomicInteger(0), new StampedLock())
            );
            counterWithLock.a.incrementAndGet();
            return counterWithLock.b;
        }
    }

    private void releaseLock(K key) {
        synchronized (counterWithLocks) {
            Tuple<AtomicInteger, StampedLock> counterWithLock = counterWithLocks.get(key);
            if (counterWithLock.a.decrementAndGet() == 0) {
                counterWithLocks.remove(key);
            }
        }
    }
}
