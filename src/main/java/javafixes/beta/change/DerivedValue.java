package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static javafixes.beta.change.ChangingValueUtil.handleNewValue;
import static javafixes.beta.change.FailableValue.wrapFailure;
import static javafixes.beta.change.FailableValue.wrapValue;

public class DerivedValue<T, SourceType> implements ChangingValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(DerivedValue.class);


    private final Optional<String> valueName;

    private final ChangingValue<SourceType> sourceValue;
    private final Function<FailableValue<SourceType>, ? extends T> valueMapper;
    private final ChangingValueUpdateConfig<T> updateConfig;

    private final AtomicReference<VersionedValue<T>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastUsedSourceChangeVersion = new AtomicReference<>();

    public DerivedValue(
            Optional<String> valueName,
            ChangingValue<SourceType> sourceValue,
            ChangingValueUpdateConfig<T> updateConfig,
            Function<FailableValue<SourceType>, ? extends T> valueMapper,
            boolean prePopulateValueImmediately
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
                FailableValue<T> newValue;

                if (currentSourceValue.value.isFailure()) {
                    newValue = wrapFailure(currentSourceValue.value.failure());
                } else {
                    try {
                        newValue = wrapValue(valueMapper.apply(currentSourceValue.value));
                    } catch (RuntimeException e) {
                        newValue = wrapFailure(e);

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
