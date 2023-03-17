package javafixes.object.changing.builder;

import javafixes.object.changing.config.ChangingValueUpdateConfig;
import javafixes.object.changing.function.AfterValueChangedHandler;
import javafixes.object.changing.function.EachValueHandler;
import javafixes.object.changing.function.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

import static javafixes.object.changing.builder.UpdateConfigBuilder.updateConfigBuilder;

public abstract class AbstractChangingValueBuilder<T, ThisBuilder extends ChangingValueBuilder<T>> implements ChangingValueBuilder<T> {

    protected Optional<String> valueName = Optional.empty();
    protected UpdateConfigBuilder<T> updateConfigBuilder = updateConfigBuilder();


    protected abstract ThisBuilder thisBuilder();

    protected ChangingValueUpdateConfig<? super T> updateConfig() {
        return updateConfigBuilder.build();
    }


    public ThisBuilder withValueName(String valueName) {
        this.valueName = Optional.of(valueName);
        return thisBuilder();
    }

    public ThisBuilder withReplaceOldValueIf(ReplaceOldValueIf<? super T> replaceOldValueIf) {
        this.updateConfigBuilder = updateConfigBuilder.withReplaceOldValueIf(replaceOldValueIf);
        return thisBuilder();
    }

    public ThisBuilder withEachValueHandler(EachValueHandler<? super T> eachValueHandler) {
        this.updateConfigBuilder = updateConfigBuilder.withEachValueHandler(eachValueHandler);
        return thisBuilder();
    }

    public ThisBuilder withAfterValueChangedHandler(AfterValueChangedHandler<? super T> afterValueChangedHandler) {
        this.updateConfigBuilder = updateConfigBuilder.withAfterValueChangedHandler(afterValueChangedHandler);
        return thisBuilder();
    }

    public ThisBuilder withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.updateConfigBuilder = updateConfigBuilder.withDisposeFunction(disposeFunction);
        return thisBuilder();
    }

    public ThisBuilder withUpdateConfig(ChangingValueUpdateConfig<? super T> updateConfig) {
        this.updateConfigBuilder = updateConfigBuilder.withUpdateConfig(updateConfig);
        return thisBuilder();
    }
}
