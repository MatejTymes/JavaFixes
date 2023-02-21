package javafixes.beta.change.builder;

import javafixes.beta.change.FailableValue;
import javafixes.beta.change.MutableValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ReplaceOldValueCheck;

import java.util.Optional;
import java.util.function.Consumer;

import static javafixes.beta.change.FailableValue.wrapFailure;
import static javafixes.beta.change.FailableValue.wrapValue;

public class MutableValueBuilder<T> implements ChangingValueBuilder<T> {

    private final FailableValue<T> initialValue;

    private Optional<String> valueName = Optional.empty();
    private Optional<ReplaceOldValueCheck<T>> shouldReplaceOldValueCheck = Optional.empty();
    private Optional<Consumer<T>> afterValueChangedFunction = Optional.empty();
    private Optional<Consumer<T>> disposeFunction = Optional.empty();

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
                new ChangingValueUpdateConfig<>(
                        shouldReplaceOldValueCheck,
                        afterValueChangedFunction,
                        disposeFunction
                )
        );
    }

    public MutableValueBuilder<T> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public MutableValueBuilder<T> withReplaceOldValueCheck(ReplaceOldValueCheck<T> replaceOldValueCheck) {
        this.shouldReplaceOldValueCheck = Optional.of(replaceOldValueCheck);
        return this;
    }

    public MutableValueBuilder<T> withAfterValueChangedFunction(Consumer<T> afterValueChangedFunction) {
        this.afterValueChangedFunction = Optional.of(afterValueChangedFunction);
        return this;
    }

    public MutableValueBuilder<T> withDisposeFunction(Consumer<T> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }
}
