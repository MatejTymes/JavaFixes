package javafixes.object;

import javafixes.common.function.ValueHandler;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link Either} can wrap value of one of 2 types defined as {@link Left Left} or {@link Right Right}.
 * This is an unbiased implementation where both left and right side have the same methods and neither one is preferred.
 *
 * @param <L> the {@link Left Left} {@link Either} type
 * @param <R> the {@link Right Right} {@link Either} type
 * @author mtymes
 */
public abstract class Either<L, R> extends DataObject implements Value<Object> {

    private Either() {
    }

    /**
     * Generates {@link Either} of type {@link Right Right} wrapping the provided value
     *
     * @param value value to wrap
     * @param <L>   generic type of left side
     * @param <R>   generic type of right side
     * @return {@link Right Right} {@link Either} wrapping provided value
     */
    public static <L, R> Either<L, R> right(R value) {
        return new Either.Right<>(value);
    }

    /**
     * Generates {@link Either} of type {@link Left Left} wrapping the provided value
     *
     * @param value value to wrap
     * @param <L>   generic type of left side
     * @param <R>   generic type of right side
     * @return {@link Left Left} {@link Either} wrapping provided value
     */
    public static <L, R> Either<L, R> left(L value) {
        return new Either.Left<>(value);
    }

    /**
     * Swaps left and right value of an {@link Either}
     *
     * @return {@link Either} with swapped left and right values
     */
    public abstract Either<R, L> swap();

    /**
     * Returns true if value is wrapped into a {@link Left Left} {@link Either}
     *
     * @return true if is {@link Left Left} {@link Either}
     */
    public abstract boolean isLeft();

    /**
     * Returns true if value is wrapped into a {@link Right Right} {@link Either}
     *
     * @return true if is {@link Right Right} {@link Either}
     */
    public abstract boolean isRight();

    /**
     * Returns wrapped value in case the {@link Either} is of type {@link Left Left}.
     * Throws a {@link NoSuchElementException} in case the {@link Either} is of type {@link Right Right}.
     *
     * @return value in case the {@link Either} is of type {@link Left Left}
     * @throws NoSuchElementException in case the {@link Either} is of type {@link Right Right}
     */
    public abstract L getLeft() throws NoSuchElementException;

    /**
     * Returns wrapped value in case the {@link Either} is of type {@link Right Right}.
     * Throws a {@link NoSuchElementException} in case the {@link Either} is of type {@link Left Left}.
     *
     * @return value in case the {@link Either} is of type {@link Right Right}
     * @throws NoSuchElementException in case the {@link Either} is of type {@link Left Left}
     */
    public abstract R getRight() throws NoSuchElementException;

    /**
     * Returns wrapped value in case the {@link Either} is of type {@link Left Left}.
     * Throws {@link Exception} provided via {@code exceptionSupplier} if value is defined as {@link Right Right} {@link Either}.
     *
     * @param exceptionSupplier that should provide {@link Exception} in case of {@link Right Right} {@link Either}
     * @param <T>               type of {@link Exception} that will be provided by exceptionSupplier
     * @return value in case the {@link Either} is of type {@link Left Left}
     * @throws T {@link Exception} provided via {@code exceptionSupplier} if value is defined as {@link Right Right} {@link Either}.
     */
    public abstract <T extends Throwable> L getLeftOrThrow(Supplier<? extends T> exceptionSupplier) throws T;

    /**
     * Returns wrapped value in case the {@link Either} is of type {@link Right Right}.
     * Throws {@link Exception} provided via {@code exceptionSupplier} if value is defined as {@link Left Left} {@link Either}.
     *
     * @param exceptionSupplier that should provide {@link Exception} in case of {@link Left Left} {@link Either}
     * @param <T>               type of {@link Exception} that will be provided by exceptionSupplier
     * @return value in case the {@link Either} is of type {@link Right Right}
     * @throws T {@link Exception} provided via {@code exceptionSupplier} if value is defined as {@link Left Left} {@link Either}.
     */
    public abstract <T extends Throwable> R getRightOrThrow(Supplier<? extends T> exceptionSupplier) throws T;

    /**
     * Applies provided {@code leftValueMapper} to transform wrapped {@code value} if value is defined as {@link Left Left} {@link Either}.
     * Applies provided {@code rightValueMapper} to transform wrapped {@code value} if value is defined as {@link Right Right} {@link Either}.
     *
     * @param leftValueMapper  function that is applied {@link Left Left} {@link Either} {@code value}
     * @param rightValueMapper function that is applied {@link Right Right} {@link Either} {@code value}
     * @param <L2>             new type of {@link Left Left} {@link Either} {@code value}
     * @param <R2>             new type of {@link Right Right} {@link Either} {@code value}
     * @return modified {@link Either}
     */
    public abstract <L2, R2> Either<L2, R2> map(Function<L, L2> leftValueMapper, Function<R, R2> rightValueMapper);

    /**
     * Applies provided {@code leftValueMapper} to transform wrapped {@code value} if value is defined as {@link Left Left} {@link Either}.
     * The {@code leftValueMapper} is ignored if value is defined as {@link Right Right} {@link Either}.
     *
     * @param leftValueMapper function that is applied {@link Left Left} {@link Either} {@code value}
     * @param <L2>            new type of {@link Left Left} {@link Either} {@code value}
     * @return modified {@link Either}
     */
    public abstract <L2> Either<L2, R> mapLeft(Function<L, L2> leftValueMapper);

    /**
     * Applies provided {@code rightValueMapper} to transform wrapped {@code value} if value is defined as {@link Right Right} {@link Either}.
     * The {@code rightValueMapper} is ignored if value is defined as {@link Left Left} {@link Either}.
     *
     * @param rightValueMapper function that is applied {@link Right Right} {@link Either} {@code value}
     * @param <R2>             new type of {@link Right Right} {@link Either} {@code value}
     * @return modified {@link Either}
     */
    public abstract <R2> Either<L, R2> mapRight(Function<R, R2> rightValueMapper);

    /**
     * Transforms a value and returns it
     *
     * @param foldLeft  function to map {@link Left Left} value into output value
     * @param foldRight function to map {@link Right Right} value into output value
     * @param <T>       type of output value
     * @return transformed value
     */
    public abstract <T> T fold(Function<L, T> foldLeft, Function<R, T> foldRight);

    /**
     * Executes provided {@code leftValueHandler} if value is defined as {@link Left Left} {@link Either}.
     * The {@code action} is ignored if value is defined as {@link Right Right} {@link Either}.
     *
     * @param leftValueHandler action that is executed for {@link Left Left} {@link Either}
     * @param <T>              class of potential {@link Throwable} thrown by {@code leftValueHandler}
     * @return the same instance of {@link Either} to allow method chaining
     * @throws T in case the {@code leftValueHandler} throws T
     */
    public abstract <T extends Throwable> Either<L, R> handleLeft(ValueHandler<? super L, ? extends T> leftValueHandler) throws T;

    /**
     * Executes provided {@code rightValueHandler} if value is defined as {@link Right Right} {@link Either}.
     * The {@code action} is ignored if value is defined as {@link Left Left} {@link Either}.
     *
     * @param rightValueHandler action that is executed for {@link Right Right} {@link Either}
     * @param <T>               class of potential {@link Throwable} thrown by {@code rightValueHandler}
     * @return the same instance of {@link Either} to allow method chaining
     * @throws T in case the {@code rightValueHandler} throws T
     */
    public abstract <T extends Throwable> Either<L, R> handleRight(ValueHandler<? super R, ? extends T> rightValueHandler) throws T;

    /**
     * Executes provided {@code leftValueHandler} if value is defined as {@link Left Left} {@link Either}.
     * Executes provided {@code rightValueHandler} if value is defined as {@link Right Right} {@link Either}.
     *
     * @param leftValueHandler  action that is executed for {@link Left Left} {@link Either}
     * @param rightValueHandler action that is executed for {@link Right Right} {@link Either}
     * @param <TL>              class of potential {@link Throwable} thrown by {@code leftValueHandler}
     * @param <TR>              class of potential {@link Throwable} thrown by {@code rightValueHandler}
     * @return the same instance of {@link Either} to allow method chaining
     * @throws TL in case the {@code leftValueHandler} throws T
     * @throws TR in case the {@code rightValueHandler} throws T
     */
    public abstract <TL extends Throwable, TR extends Throwable> Either<L, R> handle(ValueHandler<? super L, ? extends TL> leftValueHandler, ValueHandler<? super R, ? extends TR> rightValueHandler) throws TL, TR;

    /**
     * Throws {@link Exception} provided via {@code exceptionSupplier} if value is defined as {@link Left Left} {@link Either}.
     * The {@code exceptionSupplier} is ignored if value is defined as {@link Right Right} {@link Either}.
     *
     * @param exceptionSupplier that should provide {@link Exception} in case of {@link Left Left} {@link Either}
     * @param <T>               type of {@link Exception} that will be provided by exceptionSupplier
     * @return the same instance to allow method chaining
     * @throws T {@link Exception} provided via {@code exceptionSupplier} if value is defined as {@link Left Left} {@link Either}.
     */
    public abstract <T extends Throwable> Either<L, R> ifLeftThrow(Function<L, ? extends T> exceptionSupplier) throws T;

    /**
     * Throws {@link Exception} provided via {@code exceptionSupplier} if value is defined as {@link Right Right} {@link Either}.
     * The {@code exceptionSupplier} is ignored if value is defined as {@link Left Left} {@link Either}.
     *
     * @param exceptionSupplier that should provide {@link Exception} in case of {@link Right Right} {@link Either}
     * @param <T>               type of {@link Exception} that will be provided by exceptionSupplier
     * @return the same instance to allow method chaining
     * @throws T {@link Exception} provided via {@code exceptionSupplier} if value is defined as {@link Right Right} {@link Either}
     */
    public abstract <T extends Throwable> Either<L, R> ifRightThrow(Function<R, ? extends T> exceptionSupplier) throws T;

    /**
     * Returns left value if of type {@link Left} or right value if of type {@link Right}
     *
     * @return either left or right value (depending on which one is defined)
     */
    @Override
    public abstract Object value();

    public static final class Right<L, R> extends Either<L, R> {

        private final R value;

        private Right(R value) {
            this.value = value;
        }

        @Override
        public Either<R, L> swap() {
            return Either.left(value);
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public L getLeft() throws NoSuchElementException {
            throw new NoSuchElementException("Left value not defined");
        }

        @Override
        public R getRight() {
            return value;
        }

        @Override
        public <T extends Throwable> L getLeftOrThrow(Supplier<? extends T> exceptionSupplier) throws T {
            throw exceptionSupplier.get();
        }

        @Override
        public <T extends Throwable> R getRightOrThrow(Supplier<? extends T> exceptionSupplier) {
            return value;
        }

        @Override
        public <L2, R2> Either<L2, R2> map(Function<L, L2> leftValueMapper, Function<R, R2> rightValueMapper) {
            return Either.right(rightValueMapper.apply(value));
        }

        @Override
        public <L2> Either<L2, R> mapLeft(Function<L, L2> leftValueMapper) {
            return Either.right(value);
        }

        @Override
        public <R2> Either<L, R2> mapRight(Function<R, R2> rightValueMapper) {
            return Either.right(rightValueMapper.apply(value));
        }

        @Override
        public <T> T fold(Function<L, T> foldLeft, Function<R, T> foldRight) {
            return foldRight.apply(value);
        }

        @Override
        public <T extends Throwable> Either<L, R> handleLeft(ValueHandler<? super L, ? extends T> leftValueHandler) {
            return this;
        }

        @Override
        public <T extends Throwable> Either<L, R> handleRight(ValueHandler<? super R, ? extends T> rightValueHandler) throws T {
            rightValueHandler.handle(value);
            return this;
        }

        @Override
        public <TL extends Throwable, TR extends Throwable> Either<L, R> handle(ValueHandler<? super L, ? extends TL> leftValueHandler, ValueHandler<? super R, ? extends TR> rightValueHandler) throws TR {
            rightValueHandler.handle(value);
            return this;
        }

        @Override
        public <T extends Throwable> Either<L, R> ifLeftThrow(Function<L, ? extends T> exceptionSupplier) {
            return this;
        }

        @Override
        public <T extends Throwable> Either<L, R> ifRightThrow(Function<R, ? extends T> exceptionSupplier) throws T {
            throw exceptionSupplier.apply(value);
        }

        /**
         * Returns right value
         *
         * @return right value
         */
        @Override
        public R value() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Right<?, ?> right = (Right<?, ?>) o;
            return Objects.equals(value, right.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static final class Left<L, R> extends Either<L, R> {

        private final L value;

        private Left(L value) {
            this.value = value;
        }

        @Override
        public Either<R, L> swap() {
            return Either.right(value);
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public L getLeft() {
            return value;
        }

        @Override
        public R getRight() throws NoSuchElementException {
            throw new NoSuchElementException("Right value not defined");
        }

        @Override
        public <T extends Throwable> L getLeftOrThrow(Supplier<? extends T> exceptionSupplier) {
            return value;
        }

        @Override
        public <T extends Throwable> R getRightOrThrow(Supplier<? extends T> exceptionSupplier) throws T {
            throw exceptionSupplier.get();
        }

        @Override
        public <L2, R2> Either<L2, R2> map(Function<L, L2> leftValueMapper, Function<R, R2> rightValueMapper) {
            return Either.left(leftValueMapper.apply(value));
        }

        @Override
        public <L2> Either<L2, R> mapLeft(Function<L, L2> leftValueMapper) {
            return Either.left(leftValueMapper.apply(value));
        }

        @Override
        public <R2> Either<L, R2> mapRight(Function<R, R2> rightValueMapper) {
            return Either.left(value);
        }

        @Override
        public <T> T fold(Function<L, T> foldLeft, Function<R, T> foldRight) {
            return foldLeft.apply(value);
        }

        @Override
        public <T extends Throwable> Either<L, R> handleLeft(ValueHandler<? super L, ? extends T> leftValueHandler) throws T {
            leftValueHandler.handle(value);
            return this;
        }

        @Override
        public <T extends Throwable> Either<L, R> handleRight(ValueHandler<? super R, ? extends T> rightValueHandler) {
            return this;
        }

        @Override
        public <TL extends Throwable, TR extends Throwable> Either<L, R> handle(ValueHandler<? super L, ? extends TL> leftValueHandler, ValueHandler<? super R, ? extends TR> rightValueHandler) throws TL {
            leftValueHandler.handle(value);
            return this;
        }

        @Override
        public <T extends Throwable> Either<L, R> ifLeftThrow(Function<L, ? extends T> exceptionSupplier) throws T {
            throw exceptionSupplier.apply(value);
        }

        @Override
        public <T extends Throwable> Either<L, R> ifRightThrow(Function<R, ? extends T> exceptionSupplier) {
            return this;
        }

        /**
         * Returns left value
         *
         * @return left value
         */
        @Override
        public L value() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Left<?, ?> left = (Left<?, ?>) o;
            return Objects.equals(value, left.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}