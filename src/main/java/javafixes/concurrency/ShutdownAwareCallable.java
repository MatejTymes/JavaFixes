package javafixes.concurrency;

// todo: doc
public interface ShutdownAwareCallable<V> {

    V call(ShutdownInfo shutdownInfo) throws Exception;
}
