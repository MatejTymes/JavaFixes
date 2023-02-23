package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
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
            Optional<FailableValueHandler<? super T>> forEachValueFunction,
            Optional<Consumer<? super T>> afterValueChangedFunction,
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

            applyForEachValueFunction(
                    newValue,
                    forEachValueFunction,
                    valueName,
                    logger
            );

            if (oldValue != null) { // todo: mtymes - put this into the apply method
                applyAfterValueChangedFunction(
                        newValue,
                        afterValueChangedFunction,
                        valueName,
                        logger
                );
            }

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
                (Optional) updateConfig.forEachValueFunction,
                (Optional) updateConfig.afterValueChangedFunction,
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
                updateConfig.forEachValueFunction,
                updateConfig.afterValueChangedFunction,
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

    static <T> void applyForEachValueFunction(
            FailableValue<T> newValue,
            Optional<FailableValueHandler<? super T>> forEachValueFunction,
            Optional<String> valueName,
            Logger logger
    ) {
        try {
            forEachValueFunction.ifPresent(handler -> handler.handle(newValue));
        } catch (Exception loggableException) {
            try {
                logger.error(
                        "Failed to apply forEachValueFunction to new value" + valueName.map(name -> " for '" + name + "'").orElse(""),
                        loggableException
                );
            } catch (Exception unwantedException) {
                unwantedException.printStackTrace();
            }
        }
    }

    static <T> void applyAfterValueChangedFunction(
            FailableValue<T> newValue,
            Optional<Consumer<? super T>> afterValueChangedFunction,
            Optional<String> valueName,
            Logger logger
    ) {
        try {
            afterValueChangedFunction.ifPresent(function -> function.accept(newValue.value()));
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
            Optional<Consumer<? super T>> disposeFunction,
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
