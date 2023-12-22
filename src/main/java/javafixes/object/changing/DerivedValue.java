package javafixes.object.changing;

import javafixes.object.changing.config.ChangingValueUpdateConfig;
import javafixes.object.changing.function.mapping.FailableValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static javafixes.object.changing.ChangingValueHelper.handlePotentialNewValue;
import static javafixes.object.changing.FailableValue.wrapFailure;
import static javafixes.object.changing.FailableValue.wrapValue;
import static javafixes.common.util.AssertUtil.assertNotNull;

public class DerivedValue<OutputType> implements ChangingValue<OutputType> {

    private static final Logger logger = LoggerFactory.getLogger(DerivedValue.class);


    private final Optional<String> valueName;
    private final ChangingValue<?> sourceValue;
    // todo: mtymes - change to Function<FailableValue<? super SourceType>, ? extends OutputType>
    private final FailableValueMapper<?, ? extends OutputType> valueMapper;
    private final ChangingValueUpdateConfig<? super OutputType> updateConfig;

    private final AtomicReference<VersionedValue<OutputType>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<Long> lastUsedSourceChangeVersion = new AtomicReference<>();

    public <SourceType> DerivedValue(
            Optional<String> valueName,
            ChangingValue<SourceType> sourceValue,
            ChangingValueUpdateConfig<? super OutputType> updateConfig,
            FailableValueMapper<SourceType, ? extends OutputType> valueMapper,
            boolean prePopulateValueImmediately
    ) {
        assertNotNull(valueName, "valueName", this.getClass());
        assertNotNull(sourceValue, "sourceValue", this.getClass());
        assertNotNull(updateConfig, "updateConfig", this.getClass());
        assertNotNull(valueMapper, "valueMapper", this.getClass());

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
    public VersionedValue<OutputType> versionedValue() {
        populateWithLatestValue();

        return currentValueHolder.get();
    }

    private void populateWithLatestValue() {
        synchronized (currentValueHolder) {
            VersionedValue currentSourceValue = sourceValue.versionedValue();

            Long lastUsedSourceVersionNumber = lastUsedSourceChangeVersion.get();
            if (lastUsedSourceVersionNumber == null || lastUsedSourceVersionNumber < currentSourceValue.versionNumber) {
                FailableValue<OutputType> newValue;

                try {
                    newValue = wrapValue((OutputType) valueMapper.map(currentSourceValue.failableValue()));
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
