# JavaFixes

Adding some features which would be normally nice to have in Java

## Concurrency

`Runner` custom executor that allows you to wait until submitted tasks (of type `Runnable`, `Callable` or `Task`) have finished or failed

```Java
        Runner runner = Runner.runner(10);

        runner.runIn(2, SECONDS, runnable);
        runner.run(runnable);


         // blocks until all tasks are finished (or failed)
        runner.waitTillDone();


        // and reuse it

        runner.runIn(500, MILLISECONDS, callable);

        runner.waitTillDone();

        runner.shutdownAndAwaitTermination();

```

In case you would like to just monitor task submitted to existing scheduled executor then use `MonitoringTaskSubmitter` instead.

## Math

*this feature is currently in beta / WIP state*

Introducing new class `Decimal`, that should fix the troubles we're currently facing when dealing with `BigDecimal`.

The advantages it provides are:

1. `equals` reflects the `compareTo` behavior (plus `hashCode` is fixed respectivelly)

```Java
        assertThat(decimal("-1.2").equals(decimal("-1.200")), is(true));
        assertThat(decimal("-1.2").hashCode(), equalTo(decimal("-1.200").hashCode()));
```

2. sensible defaults (rounding `HALF_UP` - the one we used in school) when doing math operation (this can be overridden)

3. approximately 50% faster creation times

4. operator overriding in kotlin

5. extendible - currently supports two subtypes `LongDecimal` (for number with precision up to 19 digits) and `HugeDecimal` for everything else. There are plans the introduce additional types `InfinityDecimal` and `NANDecimal` (that will be disabled by default)

6. able to use underscores during creation to improve readability (as in Java 7)

```Java
        Decimal value = decimal("29_013_903_171.22");
```
