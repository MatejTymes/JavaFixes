package javafixes.concurrency;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class that allows you to synchronize code on different objects (ids)
 * for whom the .equals() method returns true
 *
 * @author mtymes
 */
public class Synchronizer<K> {

    private final Map<K, AtomicInteger> locks = new ConcurrentHashMap<>();

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
        AtomicInteger lock = acquireLock(key);
        try {
            synchronized (lock) {
                return action.call();
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
        AtomicInteger lock = acquireLock(key);
        try {
            synchronized (lock) {
                action.run();
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
        AtomicInteger lock = acquireLock(key);
        try {
            synchronized (lock) {
                action.run();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute action", e);
        } finally {
            releaseLock(key);
        }
    }

    private AtomicInteger acquireLock(K key) {
        synchronized (locks) {
            AtomicInteger counter = locks.computeIfAbsent(
                    key,
                    k -> new AtomicInteger(0)
            );
            counter.incrementAndGet();
            return counter;
        }
    }

    private void releaseLock(K key) {
        synchronized (locks) {
            AtomicInteger counter = locks.get(key);
            if (counter.decrementAndGet() == 0) {
                locks.remove(key);
            }
        }
    }
}
