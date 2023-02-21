package javafixes.beta.change.builder;

import javafixes.beta.change.ChangingValue;
import javafixes.beta.change.DerivedJoinedValue;
import javafixes.beta.change.FailableValue;
import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DerivedJoinedValueBuilder<T> implements ChangingValueBuilder<T> {

    private final List<ChangingValue<Object>> sourceValues;
    private final Function<List<FailableValue<Object>>, ? extends T> valuesMapper;

    private Optional<String> valueName = Optional.empty();
    private Optional<ReplaceOldValueIf<? super T>> replaceOldValueIf = Optional.empty();
    private Optional<Consumer<? super T>> afterValueChangedFunction = Optional.empty();
    private Optional<Consumer<? super T>> disposeFunction = Optional.empty();
    private boolean prePopulateValueImmediately = false;

    public <SourceType> DerivedJoinedValueBuilder(
            List<ChangingValue<SourceType>> sourceValues,
            Function<List<FailableValue<SourceType>>, ? extends T> valuesMapper
    ) {
        this.sourceValues = (List) new ArrayList<>(sourceValues);
        this.valuesMapper = (Function) valuesMapper;
    }

    @Override
    public DerivedJoinedValue<T> build() {
        return new DerivedJoinedValue<>(
                valueName,
                sourceValues,
                valuesMapper,
                new ChangingValueUpdateConfig<>(
                        replaceOldValueIf,
                        afterValueChangedFunction,
                        disposeFunction
                ),
                prePopulateValueImmediately
        );
    }

    public DerivedJoinedValueBuilder<T> withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return this;
    }

    public DerivedJoinedValueBuilder<T> withReplaceOldValueIf(ReplaceOldValueIf<? super T> replaceOldValueIf) {
        this.replaceOldValueIf = Optional.of(replaceOldValueIf);
        return this;
    }

    public DerivedJoinedValueBuilder<T> withAfterValueChangedFunction(Consumer<? super T> afterValueChangedFunction) {
        this.afterValueChangedFunction = Optional.of(afterValueChangedFunction);
        return this;
    }

    public DerivedJoinedValueBuilder<T> withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }

    public DerivedJoinedValueBuilder<T> withPrePopulateValueImmediately(boolean prePopulateValueImmediately) {
        this.prePopulateValueImmediately = prePopulateValueImmediately;
        return this;
    }
}
