package javafixes.experimental.object;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DerivedValueBuilder<T, SourceValue> {

    private final DynamicValue<SourceValue> sourceValue;
    private final Function<SourceValue, ? extends T> valueMapper;

    private Optional<String> valueName = Optional.empty();
    private Optional<Consumer<T>> disposeFunction = Optional.empty();

    DerivedValueBuilder(
            DynamicValue<SourceValue> sourceValue,
            Function<SourceValue, ? extends T> valueMapper
    ) {
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
    }

    public DerivedValueBuilder<T, SourceValue> andDerivedValueName(String derivedValueName) {
        this.valueName = Optional.of(derivedValueName);
        return this;
    }

    public DerivedValueBuilder<T, SourceValue> andDisposeFunction(Consumer<T> disposeFunction) {
        this.disposeFunction = Optional.of(disposeFunction);
        return this;
    }

    public DerivedValue<T, SourceValue> buildDerivedValue() {
        return new DerivedValue<>(
                valueName,
                sourceValue,
                valueMapper,
                disposeFunction
        );
    }
}
