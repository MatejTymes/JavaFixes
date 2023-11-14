package javafixes.object.changing.builder;

import javafixes.object.changing.config.ChangingValueUpdateConfig;
import javafixes.object.changing.function.valueHandler.AfterValueChangedHandler;
import javafixes.object.changing.function.valueHandler.EachValueHandler;
import javafixes.object.changing.function.replacement.ValueReplacementRule;

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

    public ThisBuilder withNoValueName(String valueName) {
        this.valueName = Optional.empty();
        return thisBuilder();
    }

    public ThisBuilder withValueName(Optional<String> valueName) {
        this.valueName = valueName;
        return thisBuilder();
    }

    public ThisBuilder withValueReplacementRule(ValueReplacementRule<? super T> valueReplacementRule) {
        this.updateConfigBuilder = updateConfigBuilder.withValueReplacementRule(valueReplacementRule);
        return thisBuilder();
    }

    public ThisBuilder withValueReplacementRule(Optional<ValueReplacementRule<? super T>> valueReplacementRule) {
        this.updateConfigBuilder = updateConfigBuilder.withValueReplacementRule(valueReplacementRule);
        return thisBuilder();
    }

    // todo: mtymes - with EachPotentialValueHandler
    public ThisBuilder withEachValueHandler(EachValueHandler<? super T> eachValueHandler) {
        this.updateConfigBuilder = updateConfigBuilder.withEachValueHandler(eachValueHandler);
        return thisBuilder();
    }

    public ThisBuilder withEachValueHandler(Optional<EachValueHandler<? super T>> eachValueHandler) {
        this.updateConfigBuilder = updateConfigBuilder.withEachValueHandler(eachValueHandler);
        return thisBuilder();
    }

    // todo: mtymes - with AfterReplacedPreviousValueHandler
    public ThisBuilder withAfterValueChangedHandler(AfterValueChangedHandler<? super T> afterValueChangedHandler) {
        this.updateConfigBuilder = updateConfigBuilder.withAfterValueChangedHandler(afterValueChangedHandler);
        return thisBuilder();
    }

    public ThisBuilder withAfterValueChangedHandler(Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler) {
        this.updateConfigBuilder = updateConfigBuilder.withAfterValueChangedHandler(afterValueChangedHandler);
        return thisBuilder();
    }

    public ThisBuilder withDisposeFunction(Consumer<? super T> disposeFunction) {
        this.updateConfigBuilder = updateConfigBuilder.withDisposeFunction(disposeFunction);
        return thisBuilder();
    }

    public ThisBuilder withDisposeFunction(Optional<Consumer<? super T>> disposeFunction) {
        this.updateConfigBuilder = updateConfigBuilder.withDisposeFunction(disposeFunction);
        return thisBuilder();
    }

    public ThisBuilder withUpdateConfig(ChangingValueUpdateConfig<? super T> updateConfig) {
        this.updateConfigBuilder = updateConfigBuilder.withUpdateConfig(updateConfig);
        return thisBuilder();
    }
}
