package javafixes.object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

// todo: test
// todo: javadoc
public class DerivedValue<T, SourceValue> implements DynamicValue<T> {

    private static final Logger logger = LoggerFactory.getLogger(DerivedValue.class);

    private final DynamicValue<SourceValue> sourceValue;
    private final Function<SourceValue, ? extends T> mapper;
    private long lastSourceValueVersion;

    private final Optional<String> valueName;
    private final AtomicReference<Either<RuntimeException, T>> value = new AtomicReference<>();
    private long valueVersion;

    DerivedValue(
            Optional<String> valueName,
            DynamicValue<SourceValue> sourceValue,
            Function<SourceValue, ? extends T> mapper
    ) {
        this.sourceValue = sourceValue;
        this.mapper = mapper;

        this.valueName = valueName;
        deriveValue();
        this.valueVersion = 0;
    }

    @Override
    public Optional<String> name() {
        return valueName;
    }

    @Override
    public T value() {
        ensureIsDerivedFromLatestVersion();

        return value.get()
                .ifLeftThrow(e -> e)
                .getRight();
    }

    @Override
    public long valueVersion() {
        ensureIsDerivedFromLatestVersion();

        return valueVersion;
    }

    @Override
    public <T2> DynamicValue<T2> map(Function<T, ? extends T2> mapper) {
        synchronized (value) {
            return new DerivedValue<>(Optional.empty(), this, mapper);
        }
    }

    @Override
    public <T2> DynamicValue<T2> map(String derivedValueName, Function<T, ? extends T2> mapper) {
        synchronized (value) {
            return new DerivedValue<>(Optional.of(derivedValueName), this, mapper);
        }
    }

    private void ensureIsDerivedFromLatestVersion() {
        if (lastSourceValueVersion != sourceValue.valueVersion()) {
            synchronized (value) {
                if (lastSourceValueVersion != sourceValue.valueVersion()) {
                    deriveValue();
                }
            }
        }
    }

    private void deriveValue() {
        this.lastSourceValueVersion = sourceValue.valueVersion();

        try {
            this.value.set(right(mapper.apply(sourceValue.value())));
        } catch (RuntimeException e) {
            logger.error(
                    "Failed to derive value"
                            + valueName.map(name -> " '" + name + "'")
                            + sourceValue.name().map(name -> " from '" + name + "'"),
                    e
            );
            this.value.set(left(e));
        }

        this.valueVersion++;
    }
}
