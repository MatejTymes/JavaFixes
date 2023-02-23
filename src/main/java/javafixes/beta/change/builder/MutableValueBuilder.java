package javafixes.beta.change.builder;

import javafixes.beta.change.FailableValue;
import javafixes.beta.change.MutableValue;

import static javafixes.beta.change.FailableValue.wrapFailure;
import static javafixes.beta.change.FailableValue.wrapValue;

public class MutableValueBuilder<T> extends AbstractChangingValueBuilder<T, MutableValueBuilder<T>> {

    private final FailableValue<T> initialValue;

    public MutableValueBuilder(
            FailableValue<T> initialValue
    ) {
        this.initialValue = initialValue;
    }

    public static <T> MutableValueBuilder<T> mutableValueBuilder(FailableValue<T> value) {
        return new MutableValueBuilder<>(value);
    }

    public static <T> MutableValueBuilder<T> mutableValueBuilder(T value) {
        return new MutableValueBuilder<>(wrapValue(value));
    }

    public static <T> MutableValueBuilder<T> mutableValueBuilderWithFailure(RuntimeException failure) {
        return new MutableValueBuilder<>(wrapFailure(failure));
    }

    @Override
    public MutableValue<T> build() {
        return new MutableValue<>(
                valueName,
                initialValue,
                updateConfig
        );
    }

    @Override
    protected MutableValueBuilder<T> thisBuilder() {
        return this;
    }
}
