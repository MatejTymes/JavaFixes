package javafixes.beta.change.builder;

import javafixes.beta.change.ChangingValue;

public interface ChangingValueBuilder<T> {

    ChangingValue<T> build();

    default CachedChangingValueBuilder<T> asCachedValue() {
        return new CachedChangingValueBuilder<>(this.build());
    }
}
