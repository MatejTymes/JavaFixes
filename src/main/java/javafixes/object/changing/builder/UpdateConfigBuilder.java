package javafixes.object.changing.builder;

import javafixes.object.changing.config.ChangingValueUpdateConfig;
import javafixes.object.changing.function.valueHandler.AfterReplacedPreviousValueHandler;
import javafixes.object.changing.function.valueHandler.EachPotentialValueHandler;
import javafixes.object.changing.function.replacement.ValueReplacementRule;

import java.util.Optional;
import java.util.function.Consumer;

public  class UpdateConfigBuilder<T> {

    protected ChangingValueUpdateConfig<T> updateConfig = ChangingValueUpdateConfig.doNothingOnUpdateConfig();

    public static <T> UpdateConfigBuilder<T> updateConfigBuilder() {
        return new UpdateConfigBuilder<>();
    }

    public ChangingValueUpdateConfig<? super T> build() {
        return updateConfig;
    }

    public UpdateConfigBuilder<T> withValueReplacementRule(ValueReplacementRule<? super T> valueReplacementRule) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                Optional.of(valueReplacementRule),
                updateConfig.eachPotentialValueHandler,
                updateConfig.afterReplacedPreviousValueHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withNOValueReplacementRule() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                Optional.empty(),
                updateConfig.eachPotentialValueHandler,
                updateConfig.afterReplacedPreviousValueHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withValueReplacementRule(Optional<ValueReplacementRule<? super T>> valueReplacementRule) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                valueReplacementRule,
                updateConfig.eachPotentialValueHandler,
                updateConfig.afterReplacedPreviousValueHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withEachPotentialValueHandler(EachPotentialValueHandler<? super T> eachPotentialValueHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                Optional.of(eachPotentialValueHandler),
                updateConfig.afterReplacedPreviousValueHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withNOEachPotentialValueHandler() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                Optional.empty(),
                updateConfig.afterReplacedPreviousValueHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withEachPotentialValueHandler(Optional<EachPotentialValueHandler<? super T>> eachPotentialValueHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                eachPotentialValueHandler,
                updateConfig.afterReplacedPreviousValueHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withAfterReplacedPreviousValueHandler(AfterReplacedPreviousValueHandler<? super T> afterReplacedPreviousValueHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachPotentialValueHandler,
                Optional.of(afterReplacedPreviousValueHandler),
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withNOAfterReplacedPreviousValueHandler() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachPotentialValueHandler,
                Optional.empty(),
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withAfterReplacedPreviousValueHandler(Optional<AfterReplacedPreviousValueHandler<? super T>> afterReplacedPreviousValueHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachPotentialValueHandler,
                afterReplacedPreviousValueHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachPotentialValueHandler,
                updateConfig.afterReplacedPreviousValueHandler,
                Optional.of(disposeFunction)
        );
        return this;
    }

    public UpdateConfigBuilder<T> withNODisposeFunction() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachPotentialValueHandler,
                updateConfig.afterReplacedPreviousValueHandler,
                Optional.empty()
        );
        return this;
    }

    public UpdateConfigBuilder<T> withDisposeFunction(Optional<Consumer<? super T>> disposeFunction) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachPotentialValueHandler,
                updateConfig.afterReplacedPreviousValueHandler,
                disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withUpdateConfig(ChangingValueUpdateConfig<? super T> updateConfig) {
        this.updateConfig = (ChangingValueUpdateConfig<T>) updateConfig;
        return this;
    }
}
