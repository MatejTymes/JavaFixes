# JavaFixes

Adding some features which would be normally nice to have in Java

## Concurrency

### ReusableCountLatch

`CountDownLatch` is great but I always missed the possibility to countUp and reuse it or us it if initial count is not know upfront. And this is exactly what `ReusableCountLatch` gives you.

```Java
        ReusableCountLatch latch = new ReusableCountLatch(); // creates latch with initial count 0
        ReusableCountLatch latch = new ReusableCountLatch(10); // creates latch with initial count 10

        latch.increment(); // increments counter

        latch.decrement(); // decrement counter

        latch.waitTillZero(); // blocks until counts falls to zero

        boolean succeeded = latch.waitTillZero(200, MILLISECONDS); // waits for up to 200 milliseconds until count falls to zero

        int count = latch.getCount(); // gets actual count
```
The `ReusableCountLatch` exposes a method to get its count so you can actually monitor its progress. Important thing to note is that the count can't go bellow 0 and if and attempt is made to initialize it with negative value it throws an exception. Decrementing 0 count will NOT throw an exception but the count will stay on 0 instead.

### Runner

`Runner` custom executor that allows you to wait until submitted tasks (of type `Runnable`, `Callable` or `Task`) have finished or failed.

Great if number of scheduled task is not known upfront but you want to wait till all of them finish.

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

In case you would like to monitor task newly submitted to existing scheduled executor use `MonitoringTaskSubmitter` instead.

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

        def totalDebt = d("129_550.00") + monthlyInterest * d("36");
```

```Kotlin
        val monthlyInterest = d("129_550.00") * d("0.03") / d("12");

        val totalDebt = d("129_550.00") + monthlyInterest * d("36");
```

* handy constants - one of the confusing thing about `BigDecimal` for newcomers is what is the difference between scale and precision. To ease the understanding Decimal provides some readable constants on the `Scale` and `Precision` classes:

```Java
        d("123.4698").descaleTo(_2_DECIMAL_PLACES);  // = 123.46

        d("29_013_943_171.22").deprecisionTo(_7_SIGNIFICANT_DIGITS);  // = 29_013_940_000

        d("125_455_315").descaleTo(SCALE_OF_THOUSANDS);  // = 125_455_000
        d("125_455_315").descaleTo(SCALE_OF_MILLIONS);  // = 125_000_000
```

It is possible that you'll miss some math functions. To implement your own you can use these Decimal methods:
* `decimal.signum()` - will return -1 for negative value, 0 for zero and 1 for positive value
* `decimal.scale()` - will return you the number of decimal digits
* `decimal.unscaledValue()` - will return the unscaled value which can be of these two types: Long or BigInteger
* `decimal.precision()` - will return the number of significant digits

Also any improvements in forms of patches will be welcomed.