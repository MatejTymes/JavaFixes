package javafixes.object.changing;

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
class DerivedMultiValue<T> implements ChangingValue<List<T>> {

    private final List<ChangingValue<T>> sources;
    private final List<Either<RuntimeException, T>> sourceValues;
    private final List<Long> lastSourceChangeVersions;

    private final AtomicReference<Either<RuntimeException, List<T>>> currentValue = new AtomicReference<>();
    private long changeVersion;


    DerivedMultiValue(List<ChangingValue<T>> sources) {
        this.sources = unmodifiableList(newList(sources));
        this.lastSourceChangeVersions = new ArrayList<>(sources.size());
        this.sourceValues = new ArrayList<>(sources.size());

        RuntimeException foundRuntimeException = null;
        for (ChangingValue<T> source : sources) {
            lastSourceChangeVersions.add(source.changeVersion());
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

        this.changeVersion = 0;
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
    public long changeVersion() {
        ensureIsDerivedFromLatestData();

        return changeVersion;
    }

    private void ensureIsDerivedFromLatestData() {
        boolean wasModified = false;
        for (int i = 0; i < sources.size(); i++) {
            ChangingValue<T> source = sources.get(i);
            long lastSourceVersion = lastSourceChangeVersions.get(i);
            if (lastSourceVersion != source.changeVersion()) {
                wasModified = true;
                break;
            }
        }

        if (wasModified) {
            synchronized (currentValue) {
                try {
                    RuntimeException foundRuntimeException = null;
                    for (int i = 0; i < sources.size(); i++) {
                        ChangingValue<T> source = sources.get(i);
                        long lastSourceVersion = lastSourceChangeVersions.get(i);

                        if (lastSourceVersion != source.changeVersion()) {
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
                    this.changeVersion++;
                }
            }
        }
    }
}
