package javafixes.experimental.change;

import javafixes.experimental.change.config.ChangingValueUpdateConfig;
import javafixes.experimental.change.function.UseNewValueCheck;
import javafixes.object.Either;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static javafixes.experimental.change.function.UseDifferentValueCheck.equalsBasedChecker;

class ChangingValueUtil {

    static <T> boolean handleNewValue(
            Either<RuntimeException, T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder,
            Optional<String> valueName,
            Optional<UseNewValueCheck<T>> useNewValueCheck,
            Optional<Consumer<T>> afterValueChangedFunction,
            Optional<Consumer<T>> disposeFunction,
            Logger logger
    ) {
        VersionedValue<T> oldValue = valueHolder.get();

        boolean shouldUpdate = shouldUpdate(
                oldValue,
                newValue,
                useNewValueCheck,
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
            Either<RuntimeException, T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder,
            Optional<String> valueName,
            ChangingValueUpdateConfig updateConfig,
            Logger logger
    ) {
        return handleNewValue(
                newValue,
                valueHolder,
                valueName,
                updateConfig.useNewValueCheck,
                updateConfig.afterValueChangedFunction,
                updateConfig.disposeFunction,
                logger
        );
    }

    static <T> boolean shouldUpdate(
            VersionedValue<T> oldValue,
            Either<RuntimeException, T> newValue,
            Optional<UseNewValueCheck<T>> useNewValueCheck,
            Optional<String> valueName,
            Logger logger
    ) {
        try {
            boolean shouldUpdate;
            if (oldValue == null) {
                shouldUpdate = true;
            } else {
                shouldUpdate = useNewValueCheck
                        .orElse(equalsBasedChecker())
                        .useNewValue(oldValue.value, newValue);
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
            Either<RuntimeException, T> newValue,
            AtomicReference<VersionedValue<T>> valueHolder
    ) {
        if (oldValue == null) {
            valueHolder.set(VersionedValue.initialValueVersion(newValue));
        } else {
            valueHolder.set(oldValue.nextVersion(newValue));
        }
    }

    static <T> void applyAfterValueChangedFunction(
            Either<RuntimeException, T> currentValue,
            Optional<Consumer<T>> afterValueChangedFunction,
            Optional<String> valueName,
            Logger logger
    ) {
        try {
            afterValueChangedFunction.ifPresent(function -> {
                currentValue.handleRight(function::accept);
            });
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
                disposeFunction.ifPresent(function -> {
                    oldValue.value.handleRight(function::accept);
                });
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
