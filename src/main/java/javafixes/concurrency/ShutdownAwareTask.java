package javafixes.concurrency;

// todo: doc
public interface ShutdownAwareTask {

    void run(ShutdownInfo shutdownInfo) throws Exception;
}
