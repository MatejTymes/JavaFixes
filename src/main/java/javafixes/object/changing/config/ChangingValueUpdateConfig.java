package javafixes.object.changing.config;

import javafixes.object.changing.function.valueHandler.AfterValueChangedHandler;
import javafixes.object.changing.function.valueHandler.EachPotentialValueHandler;
import javafixes.object.changing.function.replacement.ValueReplacementRule;

import java.util.Optional;
import java.util.function.Consumer;

import static javafixes.common.util.AssertUtil.assertNotNull;

public class ChangingValueUpdateConfig<T> {

    public static final ChangingValueUpdateConfig<Object> DO_NOTHING_ON_UPDATE_CONFIG = new ChangingValueUpdateConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public final Optional<ValueReplacementRule<? super T>> valueReplacementRule;

    public final Optional<EachPotentialValueHandler<? super T>> eachPotentialValueHandler;
    public final Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler;
    public final Optional<Consumer<? super T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<ValueReplacementRule<? super T>> valueReplacementRule,
            Optional<EachPotentialValueHandler<? super T>> eachPotentialValueHandler,
            Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler,
            Optional<Consumer<? super T>> disposeFunction
    ) {
        assertNotNull(valueReplacementRule, "valueReplacementRule", this.getClass());
        assertNotNull(eachPotentialValueHandler, "eachPotentialValueHandler", this.getClass());
        assertNotNull(afterValueChangedHandler, "afterValueChangedHandler", this.getClass());
        assertNotNull(disposeFunction, "disposeFunction", this.getClass());

        this.eachPotentialValueHandler = eachPotentialValueHandler;
        this.valueReplacementRule = valueReplacementRule;
        this.afterValueChangedHandler = afterValueChangedHandler;
        this.disposeFunction = disposeFunction;
    }

    public static <T> ChangingValueUpdateConfig<T> doNothingOnUpdateConfig() {
        return (ChangingValueUpdateConfig<T>) DO_NOTHING_ON_UPDATE_CONFIG;
    }
}
