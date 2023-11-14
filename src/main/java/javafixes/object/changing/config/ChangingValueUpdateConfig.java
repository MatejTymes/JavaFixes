package javafixes.object.changing.config;

import javafixes.object.changing.function.valueHandler.AfterReplacedPreviousValueHandler;
import javafixes.object.changing.function.valueHandler.EachPotentialValueHandler;
import javafixes.object.changing.function.replacement.ValueReplacementRule;

import java.util.Optional;
import java.util.function.Consumer;

import static javafixes.common.util.AssertUtil.assertNotNull;

public class ChangingValueUpdateConfig<T> {

    public static final ChangingValueUpdateConfig<Object> DO_NOTHING_ON_UPDATE_CONFIG = new ChangingValueUpdateConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public final Optional<ValueReplacementRule<? super T>> valueReplacementRule;

    public final Optional<EachPotentialValueHandler<? super T>> eachPotentialValueHandler;
    public final Optional<AfterReplacedPreviousValueHandler<? super T>> afterReplacedPreviousValueHandler;
    public final Optional<Consumer<? super T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<ValueReplacementRule<? super T>> valueReplacementRule,
            Optional<EachPotentialValueHandler<? super T>> eachPotentialValueHandler,
            Optional<AfterReplacedPreviousValueHandler<? super T>> afterReplacedPreviousValueHandler,
            Optional<Consumer<? super T>> disposeFunction
    ) {
        assertNotNull(valueReplacementRule, "valueReplacementRule", this.getClass());
        assertNotNull(eachPotentialValueHandler, "eachPotentialValueHandler", this.getClass());
        assertNotNull(afterReplacedPreviousValueHandler, "afterReplacedPreviousValueHandler", this.getClass());
        assertNotNull(disposeFunction, "disposeFunction", this.getClass());

        this.eachPotentialValueHandler = eachPotentialValueHandler;
        this.valueReplacementRule = valueReplacementRule;
        this.afterReplacedPreviousValueHandler = afterReplacedPreviousValueHandler;
        this.disposeFunction = disposeFunction;
    }

    public static <T> ChangingValueUpdateConfig<T> doNothingOnUpdateConfig() {
        return (ChangingValueUpdateConfig<T>) DO_NOTHING_ON_UPDATE_CONFIG;
    }
}
