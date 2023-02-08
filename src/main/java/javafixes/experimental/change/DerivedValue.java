package javafixes.experimental.change;

import javafixes.experimental.change.config.ChangingValueUpdateConfig;
import javafixes.object.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static javafixes.experimental.change.ChangingValueUtil.handleNewValue;
import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

public class DerivedValue<T, SourceType> implements ChangingValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(DerivedValue.class);


    private final Optional<String> valueName;

    private final ChangingValue<SourceType> sourceValue;
    private final Function<SourceType, ? extends T> valueMapper;
    private final ChangingValueUpdateConfig<T> updateConfig;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastUsedSourceChangeVersion = new AtomicReference<>();

    public DerivedValue(
            Optional<String> valueName,
            ChangingValue<SourceType> sourceValue,
            Function<SourceType, ? extends T> valueMapper,
            boolean prePopulateValueImmediately,
            ChangingValueUpdateConfig<T> updateConfig
    ) {
        this.valueName = valueName;
        this.sourceValue = sourceValue;
        this.valueMapper = valueMapper;
        this.updateConfig = updateConfig;

        if (prePopulateValueImmediately) {
            populateWithLatestValue();
        }
    }


    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public VersionedValue<T> getVersionedValue() {
        populateWithLatestValue();

        return currentValueHolder.get();
    }

    private void populateWithLatestValue() {
        synchronized (currentValueHolder) {
            VersionedValue<SourceType> currentSourceValue = sourceValue.getVersionedValue();

            Long lastUsedSourceVersionNumber = lastUsedSourceChangeVersion.get();
            if (lastUsedSourceVersionNumber == null || lastUsedSourceVersionNumber < currentSourceValue.versionNumber) {
                Either<RuntimeException, T> newValue;

                if (currentSourceValue.value.isLeft()) {
                    newValue = left(currentSourceValue.value.getLeft());
                } else {
                    try {
                        newValue = right(valueMapper.apply(currentSourceValue.value.getRight()));
                    } catch (RuntimeException e) {
                        newValue = left(e);

                        try {
                            logger.error(
                                    "Failed to derive value"
                                            + valueName.map(name -> " '" + name + "'").orElse("")
                                            + sourceValue.name().map(name -> " from '" + name + "'").orElse(""),
                                    e
                            );
                        } catch (Exception unwantedException) {
                            unwantedException.printStackTrace();
                        }
                    }
                }

                handleNewValue(
                        newValue,
                        currentValueHolder,
                        valueName,
                        updateConfig,
                        logger
                );

                lastUsedSourceChangeVersion.set(currentSourceValue.versionNumber);
            }
        }
    }
}
