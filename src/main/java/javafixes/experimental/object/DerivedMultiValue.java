package javafixes.experimental.object;

import javafixes.object.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static javafixes.collection.CollectionUtil.newList;
import static javafixes.object.Either.left;
import static javafixes.object.Either.right;

// todo: test
// todo: javadoc
// todo: add toString()
public class DerivedMultiValue<T> implements DynamicValue<List<T>> {

    private final List<DynamicValue<T>> sources;
    private final List<Either<RuntimeException, T>> sourceValues;
    private final List<Long> lastSourceVersions;

    private final AtomicReference<Either<RuntimeException, List<T>>> currentValue = new AtomicReference<>();
    private long valueVersion;


    DerivedMultiValue(List<DynamicValue<T>> sources) {
        this.sources = unmodifiableList(newList(sources));
        this.lastSourceVersions = new ArrayList<>(sources.size());
        this.sourceValues = new ArrayList<>(sources.size());

        RuntimeException foundRuntimeException = null;
        for (DynamicValue<T> source : sources) {
            lastSourceVersions.add(source.valueVersion());
            try {
                sourceValues.add(right(source.value()));
            } catch (RuntimeException e) {
                if (foundRuntimeException == null) {
                    foundRuntimeException = e;
                }
                sourceValues.add(left(e));
            }
        }
        if (foundRuntimeException == null) {
            currentValue.set(right(sourceValues.stream().map(Either::getRight).collect(toList())));
        } else {
            currentValue.set(left(foundRuntimeException));
        }

        this.valueVersion = 0;
    }

    @Override
    public Optional<String> name() {
        return Optional.empty();
    }

    @Override
    public List<T> value() {
        ensureIsDerivedFromLatestData();

        return currentValue.get()
                .ifLeftThrow(e -> e)
                .getRight();
    }

    @Override
    public long valueVersion() {
        ensureIsDerivedFromLatestData();

        return valueVersion;
    }

    private void ensureIsDerivedFromLatestData() {
        boolean wasModified = false;
        for (int i = 0; i < sources.size(); i++) {
            DynamicValue<T> source = sources.get(i);
            long lastSourceVersion = lastSourceVersions.get(i);
            if (lastSourceVersion != source.valueVersion()) {
                wasModified = true;
                break;
            }
        }

        if (wasModified) {
            synchronized (currentValue) {
                try {
                    RuntimeException foundRuntimeException = null;
                    for (int i = 0; i < sources.size(); i++) {
                        DynamicValue<T> source = sources.get(i);
                        long lastSourceVersion = lastSourceVersions.get(i);

                        if (lastSourceVersion != source.valueVersion()) {
                            try {
                                sourceValues.set(i, right(source.value()));
                            } catch (RuntimeException e) {
                                if (foundRuntimeException == null) {
                                    foundRuntimeException = e;
                                }
                                sourceValues.set(i, left(e));
                            }
                        } else {
                            if (foundRuntimeException == null) {
                                Either<RuntimeException, T> sourceValue = sourceValues.get(i);
                                if (sourceValue.isLeft()) {
                                    foundRuntimeException = sourceValue.getLeft();
                                }
                            }
                        }
                    }

                    if (foundRuntimeException == null) {
                        currentValue.set(right(sourceValues.stream().map(Either::getRight).collect(toList())));
                    } else {
                        currentValue.set(left(foundRuntimeException));
                    }
                } finally {
                    this.valueVersion++;
                }
            }
        }
    }
}
