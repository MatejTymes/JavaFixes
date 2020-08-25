package javafixes.common.function;

// todo: add javadoc
@FunctionalInterface
public interface ValueMapper<I, O, T extends Throwable> {
    O map(I value) throws T;
}
