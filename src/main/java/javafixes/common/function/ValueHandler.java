package javafixes.common.function;

// todo: add javadoc
@FunctionalInterface
public interface ValueHandler<V, T extends Throwable> {
    void handle(V value) throws T;
}
