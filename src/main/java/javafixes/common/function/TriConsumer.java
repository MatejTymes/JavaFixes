package javafixes.common.function;

// todo: mtymes - add javadoc
@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(A a, B b, C c);
}
