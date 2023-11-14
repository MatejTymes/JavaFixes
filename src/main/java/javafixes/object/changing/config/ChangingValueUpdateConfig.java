package javafixes.object.changing.config;

import javafixes.object.changing.function.valueHandler.AfterValueChangedHandler;
import javafixes.object.changing.function.valueHandler.EachValueHandler;
import javafixes.object.changing.function.replacement.ValueReplacementRule;

import java.util.Optional;
import java.util.function.Consumer;

import static javafixes.common.util.AssertUtil.assertNotNull;

public class ChangingValueUpdateConfig<T> {

    public static final ChangingValueUpdateConfig<Object> DO_NOTHING_ON_UPDATE_CONFIG = new ChangingValueUpdateConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public final Optional<ValueReplacementRule<? super T>> valueReplacementRule;

    public final Optional<EachValueHandler<? super T>> eachValueHandler;
    public final Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler;
    public final Optional<Consumer<? super T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<ValueReplacementRule<? super T>> valueReplacementRule,
            Optional<EachValueHandler<? super T>> eachValueHandler,
            Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler,
            Optional<Consumer<? super T>> disposeFunction
    ) {
        assertNotNull(valueReplacementRule, "valueReplacementRule", "ChangingValueUpdateConfig");
        assertNotNull(eachValueHandler, "eachValueHandler", "ChangingValueUpdateConfig");
        assertNotNull(afterValueChangedHandler, "afterValueChangedHandler", "ChangingValueUpdateConfig");
        assertNotNull(disposeFunction, "disposeFunction", "ChangingValueUpdateConfig");

        this.eachValueHandler = eachValueHandler;
        this.valueReplacementRule = valueReplacementRule;
        this.afterValueChangedHandler = afterValueChangedHandler;
        this.disposeFunction = disposeFunction;
    }

    public static <T> ChangingValueUpdateConfig<T> doNothingOnUpdateConfig() {
        return (ChangingValueUpdateConfig<T>) DO_NOTHING_ON_UPDATE_CONFIG;
    }
}
