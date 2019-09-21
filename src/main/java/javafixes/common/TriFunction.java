package javafixes.common;

import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is the three-arity specialization of {@link Function}.
 * <p>
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object)}.
 *
 * @param <A> the type of the first argument to the function
 * @param <B> the type of the second argument to the function
 * @param <C> the type of the third argument to the function
 * @param <D> the type of the result of the function
 * @author mtymes
 * @see java.util.function.Function
 * @see java.util.function.BiFunction
 * @since 07/15/16 11:53 PM
 */
// todo: move into common.function
@FunctionalInterface
public interface TriFunction<A, B, C, D> {

    /**
     * Applies this function to the given arguments.
     *
     * @param a the first function argument
     * @param b the second function argument
     * @param c the third function argument
     * @return the function result
     */
    D apply(A a, B b, C c);
}
