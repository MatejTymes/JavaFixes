package javafixes.beta.change;

import javafixes.beta.change.config.ChangingValueUpdateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static javafixes.beta.change.ChangingValueHelper.handleNewValue;
import static javafixes.beta.change.FailableValue.wrapFailure;
import static javafixes.beta.change.FailableValue.wrapValue;

public class DerivedJoinedValue<OutputType> implements ChangingValue<OutputType> {

    private static final Logger logger = LoggerFactory.getLogger(DerivedJoinedValue.class);


    private final Optional<String> valueName;
    private final List<ChangingValue> sourceValues;
    private final Function<List<FailableValue>, ? extends OutputType> valuesMapper;
    private final ChangingValueUpdateConfig<OutputType> updateConfig;

    private final AtomicReference<VersionedValue<OutputType>> currentValueHolder = new AtomicReference<>();
    private final AtomicReference<List<Long>> lastUsedSourceChangeVersions = new AtomicReference<>();

    public <SourceType> DerivedJoinedValue(
            Optional<String> valueName,
            List<ChangingValue<? extends SourceType>> sourceValues,
            Function<List<FailableValue<? super SourceType>>, ? extends OutputType> valuesMapper,
            ChangingValueUpdateConfig<OutputType> updateConfig,
            boolean prePopulateImmediately
    ) {
        this.valueName = valueName;
        this.sourceValues = new ArrayList<>(sourceValues);
        this.valuesMapper = (Function) valuesMapper;
        this.updateConfig = updateConfig;

        if (prePopulateImmediately) {
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
            List<Long> lastUsedSourceVersionNumbers = lastUsedSourceChangeVersions.get();

            boolean shouldUpdate = false;
            List<FailableValue> currentSourceValues = new ArrayList<>();
            List<Long> currentSourceVersionNumbers = new ArrayList<>();
            for (int i = 0; i < sourceValues.size(); i++) {
                VersionedValue sourceValue = sourceValues.get(i).getVersionedValue();

                currentSourceValues.add(sourceValue.failableValue());
                currentSourceVersionNumbers.add(sourceValue.versionNumber);

                if (lastUsedSourceVersionNumbers == null || lastUsedSourceVersionNumbers.get(i) != sourceValue.versionNumber) {
                    shouldUpdate = true;
                }
            }

            if (shouldUpdate) {
                FailableValue<OutputType> newValue;

                try {
                    newValue = wrapValue(valuesMapper.apply(currentSourceValues));
                } catch (RuntimeException e) {
                    newValue = wrapFailure(e);

                    try {
                        logger.error(
                                "Failed to derive value"
                                        + valueName.map(name -> " '" + name + "'").orElse("")
                                        // todo: mtymes - can this be implemented in some sensible way
//                                        + sourceValue.name().map(name -> " from '" + name + "'").orElse("")
                                ,
                                e
                        );
                    } catch (Exception unwantedException) {
                        unwantedException.printStackTrace();
                    }
                }

                handleNewValue(
                        newValue,
                        currentValueHolder,
                        valueName,
                        updateConfig,
                        logger
                );

                lastUsedSourceChangeVersions.set(currentSourceVersionNumbers);
            }
        }
    }
}
