package javafixes.object.changing.util;

import javafixes.object.changing.ChangingValue;
import javafixes.object.changing.FailableValue;
import javafixes.object.changing.function.valueHandler.EachValueHandler;
import javafixes.object.changing.function.replacement.ValueReplacementRule;
import javafixes.common.function.TriConsumer;
import javafixes.common.function.TriFunction;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.stream.Collectors.toList;

public class ChangingValueUtil {

    public static <ProxyInterface, T extends ProxyInterface> ProxyInterface mapToProxyInterface(
            ChangingValue<T> changingValue,
            Class<ProxyInterface> proxyInterfaceClass
    ) {
        return (ProxyInterface) newProxyInstance(
                ChangingValueUtil.class.getClassLoader(),
                new Class[]{proxyInterfaceClass},
                (proxy, method, args) -> {
                    Object value = changingValue.value();
                    try {
                        return method.invoke(value, args);
                    } catch (InvocationTargetException invocationTargetException) {
                        throw invocationTargetException.getCause();
                    }
                }
        );
    }

    public static <SourceType, OutputType> Function<FailableValue<SourceType>, OutputType> mappingValue(
            Function<? super SourceType, ? extends OutputType> valueMapper
    ) {
        return failableValue -> valueMapper.apply(failableValue.value());
    }

    public static <T> ValueReplacementRule<T> comparingValues(
            BiFunction<T, T, Boolean> valueCheck
    ) {
        return (oldValue, newValue) -> valueCheck.apply(oldValue.value(), newValue.value());
    }

    public static <T> EachValueHandler<T> handleValue(
            TriConsumer<Boolean, Optional<String>, T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleValue(value -> consumer.accept(willBeUsed, valueName, value));
        };
    }

    public static <T> EachValueHandler<T> handleValue(
            BiConsumer<Boolean, T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleValue(value -> consumer.accept(willBeUsed, value));
        };
    }

    public static <T> EachValueHandler<T> handleUsedValue(
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

    public static <T> EachValueHandler<T> handleUsedValue(
            Consumer<T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (willBeUsed) {
                failableValue.handleValue(consumer);
            }
        };
    }

    public static <T> EachValueHandler<T> handleNOTUsedValue(
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

    public static <T> EachValueHandler<T> handleNOTUsedValue(
            Consumer<T> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (!willBeUsed) {
                failableValue.handleValue(consumer);
            }
        };
    }

    public static <T> EachValueHandler<T> handleFailure(
            TriConsumer<Boolean, Optional<String>, RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleFailure(failure -> consumer.accept(willBeUsed, valueName, failure));
        };
    }

    public static <T> EachValueHandler<T> handleFailure(
            BiConsumer<Boolean, RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            failableValue.handleFailure(failure -> consumer.accept(willBeUsed, failure));
        };
    }

    public static <T> EachValueHandler<T> handleUsedFailure(
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

    public static <T> EachValueHandler<T> handleUsedFailure(
            Consumer<RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (willBeUsed) {
                failableValue.handleFailure(consumer);
            }
        };
    }

    public static <T> EachValueHandler<T> handleNOTUsedFailure(
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

    public static <T> EachValueHandler<T> handleNOTUsedFailure(
            Consumer<RuntimeException> consumer
    ) {
        return (willBeUsed, valueName, failableValue) -> {
            if (!willBeUsed) {
                failableValue.handleFailure(consumer);
            }
        };
    }

    public static <T1, T2, OutputType> BiFunction<FailableValue<T1>, FailableValue<T2>, OutputType> joiningValues(
            BiFunction<? super T1, ? super T2, ? extends OutputType> valuesMapper
    ) {
        return (value1, value2) -> valuesMapper.apply(value1.value(), value2.value());
    }

    public static <T1, T2, T3, OutputType> TriFunction<FailableValue<T1>, FailableValue<T2>, FailableValue<T3>, OutputType> joiningValues(
            TriFunction<? super T1, ? super T2, ? super T3, ? extends OutputType> valuesMapper
    ) {
        return (value1, value2, value3) -> valuesMapper.apply(value1.value(), value2.value(), value3.value());
    }

    public static <T, OutputType> Function<List<FailableValue<T>>, OutputType> joiningValues(
            Function<List<? super T>, ? extends OutputType> valuesMapper
    ) {
        return values -> valuesMapper.apply(values.stream().map(FailableValue::value).collect(toList()));
    }
}
