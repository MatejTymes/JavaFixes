package javafixes.object.changing.function.valueHandler;

import javafixes.common.function.TriConsumer;
import javafixes.object.changing.FailableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface EachValueHandler<T> {

    void handleEachValue(boolean willBeUsed, Optional<String> valueName, FailableValue<? extends T> value);

    default EachValueHandler<T> and(EachValueHandler<T> anotherHandler) {
        List<EachValueHandler<T>> handlers = new ArrayList<>();
        handlers.add(this);
        handlers.add(anotherHandler);
        return new CompositeEachValueHandler<>(handlers);
    }


    static <T> EachValueHandler<T> handleValue(
            TriConsumer<Boolean, Optional<String>, T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleValue(value -> consumer.accept(willBeUsed, valueName, value));
        };
    }

    static <T> EachValueHandler<T> handleValue(
            BiConsumer<Boolean, T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleValue(value -> consumer.accept(willBeUsed, value));
        };
    }

    static <T> EachValueHandler<T> handleUsedValue(
            BiConsumer<Optional<String>, T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (willBeUsed) {
                failableValue.handleValue(
                        value -> consumer.accept(valueName, value)
                );
            }
        };
    }

    static <T> EachValueHandler<T> handleUsedValue(
            Consumer<T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (willBeUsed) {
                failableValue.handleValue(consumer);
            }
        };
    }

    static <T> EachValueHandler<T> handleNOTUsedValue(
            BiConsumer<Optional<String>, T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (!willBeUsed) {
                failableValue.handleValue(
                        value -> consumer.accept(valueName, value)
                );
            }
        };
    }

    static <T> EachValueHandler<T> handleNOTUsedValue(
            Consumer<T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (!willBeUsed) {
                failableValue.handleValue(consumer);
            }
        };
    }

    static <T> EachValueHandler<T> handleFailure(
            TriConsumer<Boolean, Optional<String>, RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleFailure(failure -> consumer.accept(willBeUsed, valueName, failure));
        };
    }

    static <T> EachValueHandler<T> handleFailure(
            BiConsumer<Boolean, RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleFailure(failure -> consumer.accept(willBeUsed, failure));
        };
    }

    static <T> EachValueHandler<T> handleUsedFailure(
            BiConsumer<Optional<String>, RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (willBeUsed) {
                failableValue.handleFailure(
                        failure -> consumer.accept(valueName, failure)
                );
            }
        };
    }

    static <T> EachValueHandler<T> handleUsedFailure(
            Consumer<RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (willBeUsed) {
                failableValue.handleFailure(consumer);
            }
        };
    }

    static <T> EachValueHandler<T> handleNOTUsedFailure(
            BiConsumer<Optional<String>, RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (!willBeUsed) {
                failableValue.handleFailure(
                        failure -> consumer.accept(valueName, failure)
                );
            }
        };
    }

    static <T> EachValueHandler<T> handleNOTUsedFailure(
            Consumer<RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (!willBeUsed) {
                failableValue.handleFailure(consumer);
            }
        };
    }
}
