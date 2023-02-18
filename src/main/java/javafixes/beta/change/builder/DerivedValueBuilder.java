package javafixes.beta.change.builder;

import javafixes.beta.change.ChangingValue;
import javafixes.beta.change.DerivedValue;
import javafixes.beta.change.FailableValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ShouldReplaceOldValueCheck;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DerivedValueBuilder<T, SourceType> implements ChangingValueBuilder<T> {

    private final ChangingValue<SourceType> sourceValue;
    private final Function<FailableValue<SourceType>, ? extends T> valueMapper;

    private Optional<String> valueName;
    private Optional<ShouldReplaceOldValueCheck<T>> shouldReplaceOldValueCheck;
    private Optional<Consumer<T>> afterValueChangedFunction;
    private Optional<Consumer<T>> disposeFunction;
    private boolean prePopulateValueImmediately = false;

    public DerivedValueBuilder(
            ChangingValue<SourceType> sourceValue,
            Function<FailableValue<SourceType>, ? extends T> valueMapper
    ) {
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
    }

    @Override
    public DerivedValue<T, SourceType> build() {
        return new DerivedValue<>(
                valueName,
                sourceValue,
                new ChangingValueUpdateConfig<>(
                        shouldReplaceOldValueCheck,
                        afterValueChangedFunction,
                        disposeFunction
                ),
                valueMapper,
                prePopulateValueImmediately
        );
    }

    public DerivedValueBuilder<T, SourceType> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public DerivedValueBuilder<T, SourceType> withShouldReplaceOldValueCheck(ShouldReplaceOldValueCheck<T> shouldReplaceOldValueCheck) {
        this.shouldReplaceOldValueCheck = Optional.of(shouldReplaceOldValueCheck);
        return this;
    }

    public DerivedValueBuilder<T, SourceType> withAfterValueChangedFunction(Consumer<T> afterValueChangedFunction) {
        this.afterValueChangedFunction = Optional.of(afterValueChangedFunction);
        return this;
    }

    public DerivedValueBuilder<T, SourceType> withDisposeFunction(Consumer<T> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }

    public DerivedValueBuilder<T, SourceType> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }
}
