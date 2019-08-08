package javafixes.concurrency;

import javafixes.object.Tuple;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

import static javafixes.object.Tuple.tuple;

/**
 * A class that allows you to synchronize code on different objects (ids)
 * for whom the .equals() method returns true
 *
 * @author mtymes
 */
// todo: mtymes - change RuntimeException -> WrappedException
// todo: mtymes - introduce SynchronizationTimeoutException
public class Synchronizer<K> {

    private final Map<K, Tuple<AtomicInteger, StampedLock>> counterWithLocks = new ConcurrentHashMap<>();

    /**
     * Executes the provided {@link Callable} and returning its output value,
     * while making sure that only one action can be run for the provided {@code key}
     *
     * @param key value should be used for synchronization/locking purposes
     * @param action action that should be executed
     * @throws RuntimeException any thrown exception from the provided {@code action} is wrapped into a {@link RuntimeException}
     *
     * @return response generated from the provided {@code action} parameter
     */
    public <T> T synchronizeOn(K key, Callable<T> action) throws RuntimeException {
        StampedLock lock = acquireLock(key);
        try {
            long stamp = lock.writeLock();
            try {
                return action.call();
            } finally {
                lock.unlock(stamp);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute action", e);
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
     * @throws RuntimeException any thrown exception from the provided {@code action} is wrapped into a {@link RuntimeException}
     */
    public void synchronizeRunnableOn(K key, Runnable action) throws RuntimeException {
        StampedLock lock = acquireLock(key);
        try {
            long stamp = lock.writeLock();
            try {
                action.run();
            } finally {
                lock.unlock(stamp);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute action", e);
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
     * @throws RuntimeException any thrown exception from the provided {@code action} is wrapped into a {@link RuntimeException}
     */
    public void synchronizeOn(K key, Task action) throws RuntimeException {
        StampedLock lock = acquireLock(key);
        try {
            long stamp = lock.writeLock();
            try {
                action.run();
            } finally {
                lock.unlock(stamp);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute action", e);
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
