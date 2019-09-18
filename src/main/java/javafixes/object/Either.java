package javafixes.object;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// todo: add javadoc - un/non biased implementation
// todo: add into readme
public abstract class Either<L, R> extends DataObject {

    private Either() {
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Either.Right<L, R>(value);
    }

    public static <L, R> Either<L, R> left(L value) {
        return new Either.Left<L, R>(value);
    }

    public abstract Either<R, L> swap();

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract L getLeft() throws NoSuchElementException;

    public abstract R getRight() throws NoSuchElementException;

    public abstract <T extends Throwable> L getLeftOrThrow(Supplier<? extends T> exceptionSupplier) throws T;

    public abstract <T extends Throwable> R getRightOrThrow(Supplier<? extends T> exceptionSupplier) throws T;

    public abstract <L2, R2> Either<L2, R2> map(Function<L, L2> mapLeft, Function<R, R2> mapRight);

    public abstract <L2> Either<L2, R> mapLeft(Function<L, L2> mapLeft);

    public abstract <R2> Either<L, R2> mapRight(Function<R, R2> mapRight);

    public abstract <T> T fold(Function<L, T> foldLeft, Function<R, T> foldRight);

    /**
     * Executes provided {@code action} if value is defined as {@link Left Left} {@link Either}.
     * The {@code action} is ignored if value is defined as {@link Right Right} {@link Either}.
     *
     * @param action that is executed for {@link Left Left} {@link Either}
     * @return the same instance to allow method chaining
     */
    public abstract Either<L, R> ifLeft(Consumer<? super L> action);


    /**
     * Executes provided {@code action} if value is defined as {@link Right Right} {@link Either}.
     * The {@code action} is ignored if value is defined as {@link Left Left} {@link Either}.
     *
     * @param action that is executed for {@link Right Right} {@link Either}
     * @return the same instance to allow method chaining
     */
    public abstract Either<L, R> ifRight(Consumer<? super R> action);

    public abstract <T extends Throwable> Either<L, R> ifLeftThrow(Function<L, ? extends T> exceptionSupplier) throws T;

    public abstract <T extends Throwable> Either<L, R> ifRightThrow(Function<R, ? extends T> exceptionSupplier) throws T;

    /**
     * Returns left value if of type {@link Left} or right value if of type {@link Right}
     *
     * @return either left or right value (depending on which one is defined)
     */
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
        public <L2, R2> Either<L2, R2> map(Function<L, L2> mapLeft, Function<R, R2> mapRight) {
            return Either.right(mapRight.apply(value));
        }

        @Override
        public <L2> Either<L2, R> mapLeft(Function<L, L2> mapLeft) {
            return Either.right(value);
        }

        @Override
        public <R2> Either<L, R2> mapRight(Function<R, R2> mapRight) {
            return Either.right(mapRight.apply(value));
        }

        @Override
        public <T> T fold(Function<L, T> foldLeft, Function<R, T> foldRight) {
            return foldRight.apply(value);
        }

        @Override
        public Either<L, R> ifLeft(Consumer<? super L> action) {
            return this;
        }

        @Override
        public Either<L, R> ifRight(Consumer<? super R> action) {
            action.accept(value);
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
        public <L2, R2> Either<L2, R2> map(Function<L, L2> mapLeft, Function<R, R2> mapRight) {
            return Either.left(mapLeft.apply(value));
        }

        @Override
        public <L2> Either<L2, R> mapLeft(Function<L, L2> mapLeft) {
            return Either.left(mapLeft.apply(value));
        }

        @Override
        public <R2> Either<L, R2> mapRight(Function<R, R2> mapRight) {
            return Either.left(value);
        }

        @Override
        public <T> T fold(Function<L, T> foldLeft, Function<R, T> foldRight) {
            return foldLeft.apply(value);
        }

        @Override
        public Either<L, R> ifLeft(Consumer<? super L> action) {
            action.accept(value);
            return this;
        }

        @Override
        public Either<L, R> ifRight(Consumer<? super R> action) {
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
    }
}