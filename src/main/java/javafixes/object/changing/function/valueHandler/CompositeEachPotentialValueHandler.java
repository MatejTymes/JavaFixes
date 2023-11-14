package javafixes.object.changing.function.valueHandler;

import javafixes.object.changing.FailableValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static javafixes.common.util.AssertUtil.assertNotNull;

public class CompositeEachPotentialValueHandler<T> implements EachPotentialValueHandler<T> {

    private final List<EachPotentialValueHandler<T>> handlers;

    public CompositeEachPotentialValueHandler(
            List<EachPotentialValueHandler<T>> handlers
    ) {
        assertNotNull(handlers, "valueHandlers", this.getClass());

        this.handlers = handlers;
    }

    @Override
    public EachPotentialValueHandler<T> and(EachPotentialValueHandler<T> anotherHandler) {
        List<EachPotentialValueHandler<T>> expandedHandlers = new ArrayList<>(this.handlers.size() + 1);
        expandedHandlers.addAll(handlers);
        expandedHandlers.add(anotherHandler);
        return new CompositeEachPotentialValueHandler<>(expandedHandlers);
    }

    @Override
    public void handlePotentialValue(boolean willBeUsed, Optional<String> valueName, FailableValue<? extends T> value) {
        for (EachPotentialValueHandler<T> valueHandler : handlers) {
            valueHandler.handlePotentialValue(willBeUsed, valueName, value);
        }
    }
}
