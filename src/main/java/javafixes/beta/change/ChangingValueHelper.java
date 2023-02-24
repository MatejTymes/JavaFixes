package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.AfterValueChangedHandler;
import javafixes.beta.change.function.FailableValueHandler;
import javafixes.beta.change.function.ReplaceOldValueIf;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javafixes.beta.change.function.AlwaysReplaceOldValue.alwaysReplaceOldValue;
import static javafixes.beta.change.function.ReplaceDifferentOldValue.replaceDifferentOldValue;

class ChangingValueHelper {

    static <T> boolean handlePotentialNewValue(
            FailableValue<T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder,
            Optional<String> valueName,
            Optional<ReplaceOldValueIf<? super T>> replaceOldValueIf,
            Optional<FailableValueHandler<? super T>> eachValueHandler,
            Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler,
            Optional<Consumer<? super T>> disposeFunction,
            Logger logger
    ) {
        VersionedValue<T> oldValue = valueHolder.get();

        boolean shouldUpdate = shouldUpdate(
                oldValue,
                newValue,
                replaceOldValueIf,
                valueName,
                logger
        );

        if (shouldUpdate) {
            setNewValue(
                    oldValue,
                    newValue,
                    valueHolder
            );

            applyEachValueHandler(
                    valueName,
                    newValue,
                    eachValueHandler,
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
                (Optional) updateConfig.replaceOldValueIf,
                (Optional) updateConfig.eachValueHandler,
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
                ignoreDifferenceCheck ? Optional.of(alwaysReplaceOldValue()) : updateConfig.replaceOldValueIf,
                updateConfig.eachValueHandler,
                updateConfig.afterValueChangedHandler,
                updateConfig.disposeFunction,
                logger
        );
    }

    static <T> boolean shouldUpdate(
            VersionedValue<T> oldValue,
            FailableValue<T> newValue,
            Optional<ReplaceOldValueIf<? super T>> replaceOldValueIf,
            Optional<String> valueName,
            Logger logger
    ) {
        try {
            boolean shouldUpdate;
            if (oldValue == null) {
                shouldUpdate = true;
            } else {
                shouldUpdate = replaceOldValueIf
                        .orElse(replaceDifferentOldValue())
                        .replaceOldValueIf(oldValue.value, newValue);
            }
            return shouldUpdate;
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

    static <T> void applyEachValueHandler(
            Optional<String> valueName,
            FailableValue<T> newValue,
            Optional<FailableValueHandler<? super T>> eachValueHandler,
            Logger logger
    ) {
        try {
            eachValueHandler.ifPresent(handler -> handler.handle(valueName, newValue));
        } catch (Exception loggableException) {
            try {
                logger.error(
                        "Failed to apply eachValueHandler to new value" + valueName.map(name -> " for '" + name + "'").orElse(""),
                        loggableException
                );
            } catch (Exception unwantedException) {
                unwantedException.printStackTrace();
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
        if (oldValue != null) {
            try {
                afterValueChangedHandler.ifPresent(handler -> handler.afterValueChanged(oldValue.value, newValue));
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
        if (oldValue != null && oldValue.value.isNotFailure()) {
            try {
                disposeFunction.ifPresent(handler -> handler.accept(oldValue.value()));
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
