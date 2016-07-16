package mtymes.javafixes.common;

/**
 * @author mtymes
 * @since 07/15/16 11:53 PM
 */
// todo: add javadoc
@FunctionalInterface
public interface TriFunction<A, B, C, D> {

    D apply(A a, B b, C c);
}
