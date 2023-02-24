package javafixes.beta.change.builder;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.AfterValueChangedHandler;
import javafixes.beta.change.function.EachValueHandler;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

import static javafixes.beta.change.config.ChangingValueUpdateConfig.doNothingOnUpdateConfig;

public  class UpdateConfigBuilder<T> {

    protected ChangingValueUpdateConfig<T> updateConfig = doNothingOnUpdateConfig();

    public static <T> UpdateConfigBuilder<T> updateConfigBuilder() {
        return new UpdateConfigBuilder<>();
    }

    public ChangingValueUpdateConfig<? super T> build() {
        return updateConfig;
    }

    public UpdateConfigBuilder<T> withReplaceOldValueIf(ReplaceOldValueIf<? super T> replaceOldValueIf) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                Optional.of(replaceOldValueIf),
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withEachValueHandler(EachValueHandler<? super T> eachValueHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.replaceOldValueIf,
                Optional.of(eachValueHandler),
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withAfterValueChangedHandler(AfterValueChangedHandler<? super T> afterValueChangedHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.replaceOldValueIf,
                updateConfig.eachValueHandler,
                Optional.of(afterValueChangedHandler),
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.replaceOldValueIf,
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                Optional.of(disposeFunction)
        );
        return this;
    }

    public UpdateConfigBuilder<T> withUpdateConfig(ChangingValueUpdateConfig<? super T> updateConfig) {
        this.updateConfig = (ChangingValueUpdateConfig<T>) updateConfig;
        return this;
    }
}
