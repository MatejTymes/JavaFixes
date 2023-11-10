package javafixes.object.changing.builder;

import javafixes.object.changing.config.ChangingValueUpdateConfig;
import javafixes.object.changing.function.AfterValueChangedHandler;
import javafixes.object.changing.function.EachValueHandler;
import javafixes.object.changing.function.replacement.ReplaceOldValueIf;

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

    public UpdateConfigBuilder<T> withReplaceOldValueIf(ReplaceOldValueIf<? super T> replaceOldValueIf) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                Optional.of(replaceOldValueIf),
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withNoReplaceOldValueIf() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                Optional.empty(),
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withReplaceOldValueIf(Optional<ReplaceOldValueIf<? super T>> replaceOldValueIf) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                replaceOldValueIf,
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

    public UpdateConfigBuilder<T> withNoEachValueHandler() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.replaceOldValueIf,
                Optional.empty(),
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withEachValueHandler(Optional<EachValueHandler<? super T>> eachValueHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.replaceOldValueIf,
                eachValueHandler,
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

    public UpdateConfigBuilder<T> withNotAfterValueChangedHandler() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.replaceOldValueIf,
                updateConfig.eachValueHandler,
                Optional.empty(),
                updateConfig.disposeFunction
        );
        return this;
    }

    public UpdateConfigBuilder<T> withAfterValueChangedHandler(Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.replaceOldValueIf,
                updateConfig.eachValueHandler,
                afterValueChangedHandler,
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

    public UpdateConfigBuilder<T> withNoDisposeFunction() {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.replaceOldValueIf,
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                Optional.empty()
        );
        return this;
    }

    public UpdateConfigBuilder<T> withDisposeFunction(Optional<Consumer<? super T>> disposeFunction) {
        this.updateConfig = new ChangingValueUpdateConfig<>(
                updateConfig.replaceOldValueIf,
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
