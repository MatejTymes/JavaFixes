package javafixes.beta.change;

import javafixes.object.DataObject;
import javafixes.object.Either;
import javafixes.object.Value;

import java.util.function.Consumer;
import java.util.function.Function;

import static javafixes.common.Asserts.assertNotNull;
import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

public class FailableValue<T> extends DataObject implements Value<T> {

    private final Either<RuntimeException, T> value;

    public FailableValue(Either<RuntimeException, T> value) {
        assertNotNull(value, "value", "FailableValue");

        this.value = value;
    }

    public static <T> FailableValue<T> wrapValue(T value) {
        return new FailableValue<>(right(value));
    }

    public static <T> FailableValue<T> wrapFailure(RuntimeException exception) {
        return new FailableValue<>(left(exception));
    }

    @Override
    public T value() {
        return value
                .ifLeftThrow(e -> e)
                .getRight();
    }

    public RuntimeException failure() {
        return value
                .ifRightThrow(value -> new IllegalStateException("This value is not a failure instead it contains"))
                .getLeft();
    }

    public boolean isFailure() {
        return value.isLeft();
    }

    public boolean isNotFailure() {
        return value.isRight();
    }

    public void handleValue(Consumer<? super T> valueHandler) {
        value.handleRight(valueHandler::accept);
    }

    public void handleFailure(Consumer<RuntimeException> failureHandler) {
        value.handleLeft(failureHandler::accept);
    }

    public <T2> T2 fold(Function<T, T2> foldValue, Function<RuntimeException, T2> foldFailure) {
        return value.fold(foldFailure, foldValue);
    }
}
