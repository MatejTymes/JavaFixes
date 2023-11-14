package javafixes.object.changing;

import javafixes.object.changing.config.ChangingValueUpdateConfig;
import javafixes.object.changing.function.valueHandler.AfterValueChangedHandler;
import javafixes.object.changing.function.valueHandler.EachPotentialValueHandler;
import javafixes.object.changing.function.replacement.ValueReplacementRule;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javafixes.object.changing.function.replacement.ValueReplacementRule.alwaysReplaceOldValue;
import static javafixes.object.changing.function.replacement.ValueReplacementRule.replaceNonEqualOldValue;

class ChangingValueHelper {

    static <T> boolean handlePotentialNewValue(
            FailableValue<T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder,
            Optional<String> valueName,
            Optional<ValueReplacementRule<? super T>> valueReplacementRule,
            Optional<EachPotentialValueHandler<? super T>> eachPotentialValueHandler,
            Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler,
            Optional<Consumer<? super T>> disposeFunction,
            Logger logger
    ) {
        VersionedValue<T> oldValue = valueHolder.get();

        boolean shouldUpdate = shouldUpdate(
                oldValue,
                newValue,
                valueReplacementRule,
                valueName,
                logger
        );

        if (!shouldUpdate) {
            applyEachPotentialValueHandler(
                    false,
                    valueName,
                    newValue,
                    eachPotentialValueHandler,
                    logger
            );
        } else {
            setNewValue(
                    oldValue,
                    newValue,
                    valueHolder
            );

            applyEachPotentialValueHandler(
                    true,
                    valueName,
                    newValue,
                    eachPotentialValueHandler,
                    logger
            );

            applyAfterValueChangedHandler(
                    oldValue,
                    newValue,
                    afterValueChangedHandler,
                    valueName,
                    logger
            );

            applyDisposeFunction(
                    oldValue,
                    disposeFunction,
                    valueName,
                    logger
            );
        }

        return shouldUpdate;
    }

    static <T> boolean handlePotentialNewValue(
            FailableValue<T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder,
            Optional<String> valueName,
            ChangingValueUpdateConfig<? super T> updateConfig,
            Logger logger
    ) {
        return handlePotentialNewValue(
                newValue,
                valueHolder,
                valueName,
                (Optional) updateConfig.valueReplacementRule,
                (Optional) updateConfig.eachPotentialValueHandler,
                (Optional) updateConfig.afterValueChangedHandler,
                (Optional) updateConfig.disposeFunction,
                logger
        );
    }

    static <T> boolean handlePotentialNewValue(
            FailableValue<T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder,
            Optional<String> valueName,
            boolean ignoreDifferenceCheck,
            ChangingValueUpdateConfig updateConfig,
            Logger logger
    ) {
        return handlePotentialNewValue(
                newValue,
                valueHolder,
                valueName,
                ignoreDifferenceCheck ? Optional.of(alwaysReplaceOldValue()) : updateConfig.valueReplacementRule,
                updateConfig.eachPotentialValueHandler,
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction,
                logger
        );
    }

    static <T> boolean shouldUpdate(
            VersionedValue<T> oldValue,
            FailableValue<T> newValue,
            Optional<ValueReplacementRule<? super T>> valueReplacementRule,
            Optional<String> valueName,
            Logger logger
    ) {
        if (oldValue == null) {
            return true;
        }

        try {
            return valueReplacementRule
                    .orElse(replaceNonEqualOldValue())
                    .shouldReplaceOldValue(oldValue.value, newValue);
        } catch (Exception loggableException) {
            try {
                logger.error(
                        "Failed to evaluate if we should use new value for value" + valueName.map(name -> " for '" + name + "'").orElse(""),
                        loggableException
                );
            } catch (Exception unwantedException) {
                unwantedException.printStackTrace();
            }
            return true;
        }
    }

    static <T> void setNewValue(
            VersionedValue<T> oldValue,
            FailableValue<T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder
    ) {
        if (oldValue == null) {
            valueHolder.set(VersionedValue.initialValueVersion(newValue));
        } else {
            valueHolder.set(oldValue.nextVersion(newValue));
        }
    }

    static <T> void applyEachPotentialValueHandler(
            boolean willBeUse,
            Optional<String> valueName,
            FailableValue<T> newValue,
            Optional<EachPotentialValueHandler<? super T>> eachPotentialValueHandler,
            Logger logger
    ) {
        if (eachPotentialValueHandler.isPresent()) {
            try {
                eachPotentialValueHandler.get().handlePotentialValue(willBeUse, valueName, newValue);
            } catch (Exception loggableException) {
                try {
                    logger.error(
                            "Failed to apply eachPotentialValueHandler to new value" + valueName.map(name -> " for '" + name + "'").orElse(""),
                            loggableException
                    );
                } catch (Exception unwantedException) {
                    unwantedException.printStackTrace();
                }
            }
        }
    }

    static <T> void applyAfterValueChangedHandler(
            VersionedValue<T> oldValue,
            FailableValue<T> newValue,
            Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler,
            Optional<String> valueName,
            Logger logger
    ) {
        if (oldValue != null && afterValueChangedHandler.isPresent()) {
            try {
                afterValueChangedHandler.get().afterValueChanged(oldValue.value, newValue);
            } catch (Exception loggableException) {
                try {
                    logger.error(
                            "Failed to apply afterValueChangedHandler" + valueName.map(name -> " for '" + name + "'").orElse(""),
                            loggableException
                    );
                } catch (Exception unwantedException) {
                    unwantedException.printStackTrace();
                }
            }
        }
    }

    static <T> void applyDisposeFunction(
            VersionedValue<T> oldValue,
            Optional<Consumer<? super T>> disposeFunction,
            Optional<String> valueName,
            Logger logger
    ) {
        if (oldValue != null && oldValue.value.isNotFailure() && disposeFunction.isPresent()) {
            try {
                disposeFunction.get().accept(oldValue.value());
            } catch (Exception loggableException) {
                try {
                    logger.error(
                            "Failed to dispose old value" + valueName.map(name -> " for '" + name + "'").orElse(""),
                            loggableException
                    );
                } catch (Exception unwantedException) {
                    unwantedException.printStackTrace();
                }
            }
        }
    }
}
