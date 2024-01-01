package javafixes.object.changing.function.valueHandler;

import javafixes.common.function.TriConsumer;
import javafixes.object.changing.FailableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface EachPotentialValueHandler<T> {

    void handlePotentialValue(boolean willBeUsed, Optional<String> valueName, FailableValue<? extends T> value);

    default EachPotentialValueHandler<T> and(EachPotentialValueHandler<T> anotherHandler) {
        List<EachPotentialValueHandler<T>> handlers = new ArrayList<>();
        handlers.add(this);
        handlers.add(anotherHandler);
        return new CompositeEachPotentialValueHandler<>(handlers);
    }


    static <T> EachPotentialValueHandler<T> handleValue(
            TriConsumer<Boolean, Optional<String>, T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleValue(value -> consumer.accept(willBeUsed, valueName, value));
        };
    }

    static <T> EachPotentialValueHandler<T> handleValue(
            BiConsumer<Boolean, T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleValue(value -> consumer.accept(willBeUsed, value));
        };
    }

    static <T> EachPotentialValueHandler<T> handleUsedValue(
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

    static <T> EachPotentialValueHandler<T> handleUsedValue(
            Consumer<T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (willBeUsed) {
                failableValue.handleValue(consumer);
            }
        };
    }

    static <T> EachPotentialValueHandler<T> handleNOTUsedValue(
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

    static <T> EachPotentialValueHandler<T> handleNOTUsedValue(
            Consumer<T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (!willBeUsed) {
                failableValue.handleValue(consumer);
            }
        };
    }

    static <T> EachPotentialValueHandler<T> handleFailure(
            TriConsumer<Boolean, Optional<String>, RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleFailure(failure -> consumer.accept(willBeUsed, valueName, failure));
        };
    }

    static <T> EachPotentialValueHandler<T> handleFailure(
            BiConsumer<Boolean, RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleFailure(failure -> consumer.accept(willBeUsed, failure));
        };
    }

    static <T> EachPotentialValueHandler<T> handleUsedFailure(
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

    static <T> EachPotentialValueHandler<T> handleUsedFailure(
            Consumer<RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (willBeUsed) {
                failableValue.handleFailure(consumer);
            }
        };
    }

    static <T> EachPotentialValueHandler<T> handleNOTUsedFailure(
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

    static <T> EachPotentialValueHandler<T> handleNOTUsedFailure(
            Consumer<RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (!willBeUsed) {
                failableValue.handleFailure(consumer);
            }
        };
    }

    // todo: mtymes - add handleUsed
    // todo: mtymes - add handleNOTUsed
}
