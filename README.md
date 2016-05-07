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

*this feature is currently in a beta / WIP state - although feature and api complete still requires more testing and performance tuning for arithmetic operations*

Introducing new class `Decimal`, that should fix the troubles we're currently facing when dealing with `BigDecimal`.

The advantages it provides are:

* `equals` reflects the `compareTo` behavior (plus `hashCode` is fixed respectivelly)

```Java
        assertThat(decimal("-1.2").equals(decimal("-1.200")), is(true));
        assertThat(decimal("-1.2").hashCode(), equalTo(decimal("-1.200").hashCode()));
```

* sensible defaults - using rounding `HALF_UP` (the one we used in school) when doing math operation (of course this is just a default and you can always specify your own) and max(28, valueA.scale(), valueB.scale()) decimal places when doing division (I'm still deciding what should be the correct approach - or if to add ability to change the defaults)

* extendible (although not by you :D ) - `Decimal` is an abstract class, and currently supports two subtypes `LongDecimal` (for number with precision up to 19 digits - backed by `long`) and `HugeDecimal` for everything else (backed by `BigInteger`). The library handles the transitions between them seamlessly when doing math operation and always uses the least memory consuming type. There are plans the introduce additional types `InfinityDecimal` and `NANDecimal` (that will be disabled by default)

* can evolve without affecting you - creation using only static factory methods doesn't expose defined types (you always refer to them as `Decimal`), so that the library can evolve without any changes needed on the users/client side.

* readable - able to use underscores during creation to improve readability (as in Java 7). Also can use short creation method `d(...)`:

```Java
        Decimal value = decimal("29_013_903_171.22");

        Decimal sum = d("0.456").plus(value);
```

* groovy and kotlin operators (sorry scala you have too ugly syntax):

```Groovy
        def monthlyInterest = d("129_550.00") * d("0.03") / d("12");

        def expectedPayment = monthlyInterest.descaleTo(2, RoundingMode.UP)
```

```Kotlin
        val monthlyInterest = d("129_550.00") * d("0.03") / d("12");

        val expectedPayment = monthlyInterest.descaleTo(2, RoundingMode.UP)
```

Some possible future advantages might be addition of more mathematical functions.