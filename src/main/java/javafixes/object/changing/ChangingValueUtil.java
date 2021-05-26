package javafixes.object.changing;

import javafixes.object.Either;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Consumer;

// todo: test
// todo: javadoc
public class ChangingValueUtil {

    public static <T> void applyOnValueChangedFunction(
            Either<RuntimeException, T> currentValue,
            Optional<Consumer<T>> onValueChangedFunction,
            Optional<String> valueName,
            Logger logger
    ) {
        try {
            onValueChangedFunction.ifPresent(function -> {
                currentValue.handleRight(function::accept);
            });
        } catch (Exception loggableException) {
            try {
                logger.error(
                        "Failed to apply onValueChangedFunction to new value" + valueName.map(name -> " for '" + name + "'").orElse(""),
                        loggableException
                );
            } catch (Exception unwantedException) {
                unwantedException.printStackTrace();
            }
        }
    }

    public static <T> void applyDisposeFunction(
            Either<RuntimeException, T> oldValue,
            Optional<Consumer<T>> disposeFunction,
            Optional<String> valueName,
            Logger logger
    ) {
        try {
            disposeFunction.ifPresent(function -> {
                oldValue.handleRight(function::accept);
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
