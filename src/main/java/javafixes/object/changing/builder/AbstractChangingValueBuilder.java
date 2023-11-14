package javafixes.object.changing.builder;

import javafixes.object.changing.config.ChangingValueUpdateConfig;
import javafixes.object.changing.function.valueHandler.AfterReplacedPreviousValueHandler;
import javafixes.object.changing.function.valueHandler.EachPotentialValueHandler;
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

    public ThisBuilder withEachPotentialValueHandler(EachPotentialValueHandler<? super T> eachPotentialValueHandler) {
        this.updateConfigBuilder = updateConfigBuilder.withEachPotentialValueHandler(eachPotentialValueHandler);
        return thisBuilder();
    }

    public ThisBuilder withEachPotentialValueHandler(Optional<EachPotentialValueHandler<? super T>> eachPotentialValueHandler) {
        this.updateConfigBuilder = updateConfigBuilder.withEachPotentialValueHandler(eachPotentialValueHandler);
        return thisBuilder();
    }

    public ThisBuilder withAfterReplacedPreviousValueHandler(AfterReplacedPreviousValueHandler<? super T> afterReplacedPreviousValueHandler) {
        this.updateConfigBuilder = updateConfigBuilder.withAfterReplacedPreviousValueHandler(afterReplacedPreviousValueHandler);
        return thisBuilder();
    }

    public ThisBuilder withAfterReplacedPreviousValueHandler(Optional<AfterReplacedPreviousValueHandler<? super T>> afterReplacedPreviousValueHandler) {
        this.updateConfigBuilder = updateConfigBuilder.withAfterReplacedPreviousValueHandler(afterReplacedPreviousValueHandler);
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
