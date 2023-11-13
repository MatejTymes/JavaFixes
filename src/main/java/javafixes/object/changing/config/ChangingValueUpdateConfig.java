package javafixes.object.changing.config;

import javafixes.object.changing.function.AfterValueChangedHandler;
import javafixes.object.changing.function.EachValueHandler;
import javafixes.object.changing.function.replacement.ReplaceOldValueIf;

import java.util.Optional;
import java.util.function.Consumer;

import static javafixes.common.util.AssertUtil.assertNotNull;

public class ChangingValueUpdateConfig<T> {

    public static final ChangingValueUpdateConfig<Object> DO_NOTHING_ON_UPDATE_CONFIG = new ChangingValueUpdateConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public final Optional<ReplaceOldValueIf<? super T>> replaceOldValueIf;

    public final Optional<EachValueHandler<? super T>> eachValueHandler;
    public final Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler;
    public final Optional<Consumer<? super T>> disposeFunction;

    public ChangingValueUpdateConfig(
            Optional<ReplaceOldValueIf<? super T>> replaceOldValueIf,
            Optional<EachValueHandler<? super T>> eachValueHandler,
            Optional<AfterValueChangedHandler<? super T>> afterValueChangedHandler,
            Optional<Consumer<? super T>> disposeFunction
    ) {
        assertNotNull(replaceOldValueIf, "replaceOldValueIf", "ChangingValueUpdateConfig");
        assertNotNull(eachValueHandler, "eachValueHandler", "ChangingValueUpdateConfig");
        assertNotNull(afterValueChangedHandler, "afterValueChangedHandler", "ChangingValueUpdateConfig");
        assertNotNull(disposeFunction, "disposeFunction", "ChangingValueUpdateConfig");

        this.eachValueHandler = eachValueHandler;
        this.replaceOldValueIf = replaceOldValueIf;
        this.afterValueChangedHandler = afterValueChangedHandler;
        this.disposeFunction = disposeFunction;
    }

    public static <T> ChangingValueUpdateConfig<T> doNothingOnUpdateConfig() {
        return (ChangingValueUpdateConfig<T>) DO_NOTHING_ON_UPDATE_CONFIG;
    }
}
