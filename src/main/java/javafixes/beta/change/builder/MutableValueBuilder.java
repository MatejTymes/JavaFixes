package javafixes.beta.change.builder;

import javafixes.beta.change.FailableValue;
import javafixes.beta.change.MutableValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

import static javafixes.beta.change.FailableValue.wrapFailure;
import static javafixes.beta.change.FailableValue.wrapValue;

public class MutableValueBuilder<T> implements ChangingValueBuilder<T> {

    private final FailableValue<T> initialValue;

    private Optional<String> valueName = Optional.empty();
    private Optional<ReplaceOldValueIf<? super T>> replaceOldValueIf = Optional.empty();
    private Optional<Consumer<? super T>> afterValueChangedFunction = Optional.empty();
    private Optional<Consumer<? super T>> disposeFunction = Optional.empty();

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
                        replaceOldValueIf,
                        afterValueChangedFunction,
                        disposeFunction
                )
        );
    }

    public MutableValueBuilder<T> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public MutableValueBuilder<T> withReplaceOldValueIf(ReplaceOldValueIf<? super T> replaceOldValueIf) {
        this.replaceOldValueIf = Optional.of(replaceOldValueIf);
        return this;
    }

    public MutableValueBuilder<T> withAfterValueChangedFunction(Consumer<? super T> afterValueChangedFunction) {
        this.afterValueChangedFunction = Optional.of(afterValueChangedFunction);
        return this;
    }

    public MutableValueBuilder<T> withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }
}
