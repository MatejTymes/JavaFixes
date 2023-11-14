package javafixes.object.changing.builder;

import javafixes.object.changing.config.ChangingValueUpdateConfig;
import javafixes.object.changing.function.valueHandler.AfterValueChangedHandler;
import javafixes.object.changing.function.valueHandler.EachValueHandler;
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
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withNoValueReplacementRule() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                Optional.empty(),
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withValueReplacementRule(Optional<ValueReplacementRule<? super T>> valueReplacementRule) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                valueReplacementRule,
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withEachValueHandler(EachValueHandler<? super T> eachValueHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                Optional.of(eachValueHandler),
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withNoEachValueHandler() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                Optional.empty(),
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withEachValueHandler(Optional<EachValueHandler<? super T>> eachValueHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                eachValueHandler,
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withAfterValueChangedHandler(AfterValueChangedHandler<? super T> afterValueChangedHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachValueHandler,
                Optional.of(afterValueChangedHandler),
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withNotAfterValueChangedHandler() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachValueHandler,
                Optional.empty(),
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withAfterValueChangedHandler(Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachValueHandler,
                afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                Optional.of(disposeFunction)
        );
        return this;
    }

    public UpdateConfigBuilder<T> withNoDisposeFunction() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                Optional.empty()
        );
        return this;
    }

    public UpdateConfigBuilder<T> withDisposeFunction(Optional<Consumer<? super T>> disposeFunction) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.valueReplacementRule,
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withUpdateConfig(ChangingValueUpdateConfig<? super T> updateConfig) {
        this.updateConfig = (ChangingValueUpdateConfig<T>) updateConfig;
        return this;
    }
}
