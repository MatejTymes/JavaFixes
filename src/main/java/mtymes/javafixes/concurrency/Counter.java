package mtymes.javafixes.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author mtymes
 * @since 07/10/15 00:10 AM
 */
// todo: add test
public class Counter {

    private final Sync sync;

    public Counter(int initialSize) {
        this.sync = new Sync(initialSize);
    }

    public Counter() {
        this(0);
    }

    public void increment() {
        sync.increment();
    }

    public void decrement() {
        sync.decrement();
    }

    public void waitTillCountedDown() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public boolean waitTillCountedDown(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    // class based on CountDownLatch.Sync
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
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
