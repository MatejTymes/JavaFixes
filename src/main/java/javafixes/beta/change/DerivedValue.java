package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static javafixes.beta.change.ChangingValueHelper.handlePotentialNewValue;
import static javafixes.beta.change.FailableValue.wrapFailure;
import static javafixes.beta.change.FailableValue.wrapValue;
import static javafixes.common.Asserts.assertNotNull;

// todo: mtymes - remove the SourceType generic parameter
public class DerivedValue<SourceType, OutputType> implements ChangingValue<OutputType> {

    private static final Logger logger = LoggerFactory.getLogger(DerivedValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<SourceType> sourceValue;
    // todo: mtymes - change to Function<FailableValue<? super SourceType>, ? extends OutputType>
    private final Function<FailableValue<SourceType>, ? extends OutputType> valueMapper;
    private final ChangingValueUpdateConfig<? super OutputType> updateConfig;

    private final AtomicReference<VersionedValue<OutputType>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastUsedSourceChangeVersion = new AtomicReference<>();

    public DerivedValue(
            Optional<String> valueName,
            ChangingValue<SourceType> sourceValue,
            ChangingValueUpdateConfig<? super OutputType> updateConfig,
            Function<FailableValue<SourceType>, ? extends OutputType> valueMapper,
            boolean prePopulateValueImmediately
    ) {
        assertNotNull(valueName, "valueName", "DerivedValue");
        assertNotNull(sourceValue, "sourceValue", "DerivedValue");
        assertNotNull(updateConfig, "updateConfig", "DerivedValue");
        assertNotNull(valueMapper, "valueMapper", "DerivedValue");

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
    public VersionedValue<OutputType> getVersionedValue() {
        populateWithLatestValue();

        return currentValueHolder.get();
    }

    private void populateWithLatestValue() {
        synchronized (currentValueHolder) {
            VersionedValue<SourceType> currentSourceValue = sourceValue.getVersionedValue();

            Long lastUsedSourceVersionNumber = lastUsedSourceChangeVersion.get();
            if (lastUsedSourceVersionNumber == null || lastUsedSourceVersionNumber < currentSourceValue.versionNumber) {
                FailableValue<OutputType> newValue;

                try {
                    newValue = wrapValue(valueMapper.apply(currentSourceValue.failableValue()));
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

                handlePotentialNewValue(
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
