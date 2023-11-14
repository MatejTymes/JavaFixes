package javafixes.object.changing.function.valueHandler;

import javafixes.object.changing.FailableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafixes.common.util.AssertUtil.assertNotNull;

public class CompositeEachValueHandler<T> implements EachValueHandler<T> {

    private final List<EachValueHandler<T>> valueHandlers;

    public CompositeEachValueHandler(
            List<EachValueHandler<T>> valueHandlers
    ) {
        assertNotNull(valueHandlers, "valueHandlers", "CompositeEachValueHandler");

        this.valueHandlers = valueHandlers;
    }

    @Override
    public EachValueHandler<T> and(EachValueHandler<T> anotherHandler) {
        List<EachValueHandler<T>> expandedValueHandlers = new ArrayList<>(this.valueHandlers.size() + 1);
        expandedValueHandlers.addAll(valueHandlers);
        expandedValueHandlers.add(anotherHandler);
        return new CompositeEachValueHandler<>(expandedValueHandlers);
    }

    @Override
    public void handleEachValue(boolean willBeUsed, Optional<String> valueName, FailableValue<? extends T> value) {
        for (EachValueHandler<T> valueHandler : valueHandlers) {
            valueHandler.handleEachValue(willBeUsed, valueName, value);
        }
    }
}
