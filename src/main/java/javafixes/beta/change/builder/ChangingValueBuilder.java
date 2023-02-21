package javafixes.beta.change.builder;

import javafixes.beta.change.ChangingValue;

import static javafixes.beta.change.builder.CachedValueBuilder.cachedValueBuilder;

public interface ChangingValueBuilder<T> {

    ChangingValue<T> build();

    default CachedValueBuilder<T> asCachedValue() {
        return cachedValueBuilder(this);
    }
}
