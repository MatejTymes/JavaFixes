package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import javafixes.beta.change.function.ShouldReplaceOldValueCheck;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javafixes.beta.change.function.AlwaysReplaceOldValue.alwaysReplaceOldValue;
import static javafixes.beta.change.function.ReplaceDifferentOldValue.replaceDifferentOldValue;

class ChangingValueHelper {

    static <T> boolean handleNewValue(
            FailableValue<T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder,
            Optional<String> valueName,
            Optional<ShouldReplaceOldValueCheck<T>> shouldReplaceOldValueCheck,
            Optional<Consumer<T>> afterValueChangedFunction,
            Optional<Consumer<T>> disposeFunction,
            Logger logger
    ) {
        VersionedValue<T> oldValue = valueHolder.get();

        boolean shouldUpdate = shouldUpdate(
                oldValue,
                newValue,
                shouldReplaceOldValueCheck,
                valueName,
                logger
        );

        if (shouldUpdate) {
            setNewValue(
                    oldValue,
                    newValue,
                    valueHolder
            );

            applyAfterValueChangedFunction(
                    newValue,
                    afterValueChangedFunction,
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

    static <T> boolean handleNewValue(
            FailableValue<T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder,
            Optional<String> valueName,
            ChangingValueUpdateConfig updateConfig,
            Logger logger
    ) {
        return handleNewValue(
                newValue,
                valueHolder,
                valueName,
                updateConfig.shouldReplaceOldValueCheck,
                updateConfig.afterValueChangedFunction,
                updateConfig.disposeFunction,
                logger
        );
    }

    static <T> boolean handleNewValue(
            FailableValue<T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder,
            Optional<String> valueName,
            boolean ignoreDifferenceCheck,
            ChangingValueUpdateConfig updateConfig,
            Logger logger
    ) {
        return handleNewValue(
                newValue,
                valueHolder,
                valueName,
                ignoreDifferenceCheck ? Optional.of(alwaysReplaceOldValue()) : updateConfig.shouldReplaceOldValueCheck,
                updateConfig.afterValueChangedFunction,
                updateConfig.disposeFunction,
                logger
        );
    }

    static <T> boolean shouldUpdate(
            VersionedValue<T> oldValue,
            FailableValue<T> newValue,
            Optional<ShouldReplaceOldValueCheck<T>> shouldReplaceOldValueCheck,
            Optional<String> valueName,
            Logger logger
    ) {
        try {
            boolean shouldUpdate;
            if (oldValue == null) {
                shouldUpdate = true;
            } else {
                shouldUpdate = shouldReplaceOldValueCheck
                        .orElse(replaceDifferentOldValue())
                        .shouldReplaceOldValue(oldValue.value, newValue);
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

    static <T> void applyAfterValueChangedFunction(
            FailableValue<T> currentValue,
            Optional<Consumer<T>> afterValueChangedFunction,
            Optional<String> valueName,
            Logger logger
    ) {
        try {
            afterValueChangedFunction.ifPresent(currentValue::handleValue);
        } catch (Exception loggableException) {
            try {
                logger.error(
                        "Failed to apply afterValueChangedFunction to new value" + valueName.map(name -> " for '" + name + "'").orElse(""),
                        loggableException
                );
            } catch (Exception unwantedException) {
                unwantedException.printStackTrace();
            }
        }
    }

    static <T> void applyDisposeFunction(
            VersionedValue<T> oldValue,
            Optional<Consumer<T>> disposeFunction,
            Optional<String> valueName,
            Logger logger
    ) {
        if (oldValue != null) {
            try {
                disposeFunction.ifPresent(oldValue.value::handleValue);
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
