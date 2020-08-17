package javafixes.experimental.object;

import javafixes.object.Either;

import java.util.Optional;
import java.util.function.Consumer;

import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

public class MutableValueBuilder<T> {

    private final Either<RuntimeException, T> initialValue;

    private Optional<String> valueName = Optional.empty();
    private Optional<Consumer<T>> disposeFunction;

    private MutableValueBuilder(Either<RuntimeException, T> initialValue) {
        this.initialValue = initialValue;
    }

    public static <T> MutableValueBuilder<T> usingInitialValue(T initialValue) {
        return new MutableValueBuilder<>(right(initialValue));
    }

    public static <T> MutableValueBuilder<T> usingInitializationFailure(RuntimeException initialException) {
        return new MutableValueBuilder<>(left(initialException));
    }

    public MutableValueBuilder<T> andMutableValueName(String mutableValueName) {
        this.valueName = Optional.of(mutableValueName);
        return this;
    }

    public MutableValueBuilder<T> andDisposeFunction(Consumer<T> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }

    public MutableValue<T> buildMutableValue() {
        return new MutableValue<>(
                valueName,
                initialValue,
                disposeFunction
        );
    }
}
