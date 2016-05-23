# JavaFixes

Adding some features which would be normally nice to have in Java

## Concurrency

`Runner` custom executor that allows you to wait until submitted tasks (of type `Runnable`, `Callable` or `Task`) have finished or failed

```Java
        Runner runner = Runner.runner(numberOfThreads);

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

*this feature is currently in a beta / WIP state - although feature complete it still requires more testing, api fine-tuning and tuning of performance for arithmetic operations*

Introducing new class `Decimal`, that should fix the troubles we're currently facing when dealing with `BigDecimal`. The new class was needed as `BigDecimal`'s behavior can't be changed/patched because of backwards compatibility.

The advantages it provides are:

* `equals` reflects the `compareTo` behavior (plus `hashCode` is fixed respectively)

```Java
        assertThat(decimal("-1.2").equals(decimal("-1.200")), is(true));
        assertThat(decimal("-1.2").hashCode(), equalTo(decimal("-1.200").hashCode()));
```

* sensible defaults - using rounding `HALF_UP` (the one we used in school) when doing math operation (this is a default and you can pass in your own value) and max(28, valueA.scale(), valueB.scale()) decimal places when doing division (this will change soon to be defined as precision of 34 - which means number will be rounded after 34 digits - no matter the scale)

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

* handy constants - one of the confusing thing about `BigDecimal` for newcomers is what is the difference between scale and precision. To ease the understanding Decimal provides some readable constants on the `Scale` and `Precision` classes:

```Java
        d("123.4698").descaleTo(_2_Decimal_Places);  // = 123.46

        d("29_013_943_171.22").deprecisionTo(_7_Significant_Digits);  // = 29_013_940_000

        d("125_455_315").descaleTo(Scale_Of_Thousands);  // = 125_455_000
        d("125_455_315").descaleTo(Scale_Of_Millions);  // = 125_000_000
```

It is possible that you'll miss some math functions. To implement your own you can use these Decimal methods:
* `decimal.signum()` - will return -1 for negative value, 0 for zero and 1 for positive value
* `decimal.scale()` - will return you the number of decimal digits
* `decimal.unscaledValue()` - will return the unscaled value which can be of these two types: Long or BigInteger

Also any improvements in forms of patches will be welcomed.