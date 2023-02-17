package javafixes.beta.change.builder;

import javafixes.beta.change.ChangingValue;

public interface ChangingValueBuilder<T> {

    ChangingValue<T> build();
}
