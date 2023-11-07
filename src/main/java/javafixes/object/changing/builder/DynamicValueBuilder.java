package javafixes.object.changing.builder;


import javafixes.object.changing.DynamicValue;

import java.util.function.Supplier;

public class DynamicValueBuilder<T> extends AbstractChangingValueBuilder<T, DynamicValueBuilder<T>> {

    private final Supplier<T> valueGenerator;

    public DynamicValueBuilder(
            Supplier<T> valueGenerator
    ) {
        this.valueGenerator = valueGenerator;
    }

    public static <T> DynamicValueBuilder<T> dynamicValueBuilder(
            Supplier<T> valueGenerator
    ) {
        return new DynamicValueBuilder<>(valueGenerator);
    }


    public static <T> DynamicValue<T> dynamicValue(
            Supplier<T> valueGenerator
    ) {
        return dynamicValueBuilder(valueGenerator).build();
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
