package javafixes.concurrency;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// todo: javadoc
public class Synchronizer<K> {

    private final Map<K, AtomicInteger> locks = new ConcurrentHashMap<>();

    public <T> T synchronizeOn(K key, Callable<T> action) {
        AtomicInteger lock = acquireLock(key);
        try {
            synchronized (lock) {
                return action.call();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            releaseLock(key);
        }
    }

//    public void synchronizeOn(K key, Runnable action) {
//        AtomicInteger lock = acquireLock(key);
//        try {
//            synchronized (lock) {
//                action.run();
//            }
//        } finally {
//            releaseLock(key);
//        }
//    }

    public void synchronizeOn(K key, Task action) {
        AtomicInteger lock = acquireLock(key);
        try {
            synchronized (lock) {
                action.run();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
