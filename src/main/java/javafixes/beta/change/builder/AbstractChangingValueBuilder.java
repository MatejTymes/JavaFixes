package javafixes.beta.change.builder;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.AfterValueChangedHandler;
import javafixes.beta.change.function.FailableValueHandler;
import javafixes.beta.change.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractChangingValueBuilder<T, ThisBuilder extends ChangingValueBuilder<T>> implements ChangingValueBuilder<T> {

    protected Optional<String> valueName = Optional.empty();
    protected ChangingValueUpdateConfig<? super T> updateConfig = ChangingValueUpdateConfig.DO_NOTHING_ON_UPDATE_CONFIG;


    protected abstract ThisBuilder thisBuilder();


    public ThisBuilder withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return thisBuilder();
    }

    public ThisBuilder withReplaceOldValueIf(ReplaceOldValueIf<? super T> replaceOldValueIf) {
        this.updateConfig = updateConfig.copyWithReplaceOldValueIf((ReplaceOldValueIf) replaceOldValueIf);
        return thisBuilder();
    }

    public ThisBuilder withForEachValueFunction(FailableValueHandler<? super T> forEachValueFunction) {
        this.updateConfig = updateConfig.copyWithForEachValueFunction((FailableValueHandler) forEachValueFunction);
        return thisBuilder();
    }

    public ThisBuilder withAfterValueChangedFunction(AfterValueChangedHandler<? super T> afterValueChangedFunction) {
        this.updateConfig = updateConfig.copyWithAfterValueChangedFunction((AfterValueChangedHandler) afterValueChangedFunction);
        return thisBuilder();
    }

    public ThisBuilder withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.updateConfig = updateConfig.copyWithDisposeFunction((Consumer) disposeFunction);
        return thisBuilder();
    }

    public ThisBuilder withUpdateConfig(ChangingValueUpdateConfig<? super T> updateConfig) {
        this.updateConfig = updateConfig;
        return thisBuilder();
    }
}
