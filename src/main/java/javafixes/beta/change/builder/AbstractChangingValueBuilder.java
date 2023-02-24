package javafixes.beta.change.builder;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.AfterValueChangedHandler;
import javafixes.beta.change.function.EachValueHandler;
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

    public ThisBuilder withEachValueHandler(EachValueHandler<? super T> eachValueHandler) {
        this.updateConfig = updateConfig.copyWithEachValueHandler((EachValueHandler) eachValueHandler);
        return thisBuilder();
    }

    public ThisBuilder withAfterValueChangedHandler(AfterValueChangedHandler<? super T> afterValueChangedHandler) {
        this.updateConfig = updateConfig.copyWithAfterValueChangedHandler((AfterValueChangedHandler) afterValueChangedHandler);
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
