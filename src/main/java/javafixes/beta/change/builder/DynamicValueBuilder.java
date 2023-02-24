package javafixes.beta.change.builder;


import javafixes.beta.change.DynamicValue;

import java.util.function.Supplier;

public class DynamicValueBuilder<T> extends AbstractChangingValueBuilder<T, DynamicValueBuilder<T>> {

    private final Supplier<T> valueGenerator;

    public DynamicValueBuilder(
            Supplier<T> valueGenerator
    ) {
        this.valueGenerator = valueGenerator;
    }

    public static <T> DynamicValueBuilder<T> dynamicValueBuilder(Supplier<T> valueGenerator) {
        return new DynamicValueBuilder<>(valueGenerator);
    }

    @Override
    public DynamicValue<T> build() {
        return new DynamicValue<>(
                valueName,
                valueGenerator,
                updateConfig()
        );
    }

    @Override
    protected DynamicValueBuilder<T> thisBuilder() {
        return this;
    }
}
