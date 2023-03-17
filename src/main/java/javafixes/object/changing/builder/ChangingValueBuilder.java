package javafixes.object.changing.builder;

import javafixes.object.changing.ChangingValue;

import static javafixes.object.changing.builder.CachedValueBuilder.cachedValueBuilder;

public interface ChangingValueBuilder<T> {

    ChangingValue<T> build();

    default CachedValueBuilder<T> asCachedValue() {
        return cachedValueBuilder(this);
    }
}
