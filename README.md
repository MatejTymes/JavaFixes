# JavaFixes

Adding some features which would be normally nice to have in Java

You can get it via Gradle:
`compile 'com.github.matejtymes:javafixes:1.4.0.0'`
or Maven
```Xml
<dependency>
    <groupId>com.github.matejtymes</groupId>
    <artifactId>javafixes</artifactId>
    <version>1.4.0.0</version>
</dependency>
```

## Concurrency

### ReusableCountLatch

`CountDownLatch` is great but I always missed the possibility to countUp and reuse it or use it if initial count is not know upfront. `Phaser` should fulfil this but can only count up to 65,535. And this is exactly where `ReusableCountLatch` comes in to save us as it can count up to 2,147,483,647.

```Java
    ReusableCountLatch latch = new ReusableCountLatch(); // creates latch with initial count 0
    ReusableCountLatch latch = new ReusableCountLatch(10); // creates latch with initial count 10

    latch.increment(); // increments counter

    latch.decrement(); // decrement counter

    latch.waitTillZero(); // blocks until counts falls to zero

    boolean succeeded = latch.waitTillZero(200, MILLISECONDS); // waits for up to 200 milliseconds until count falls to zero

    int count = latch.getCount(); // gets actual count
```
The `ReusableCountLatch` exposes a method to get its count so you can monitor its progress. Important thing to note is that the count can't go bellow 0 and if and attempt is made to initialize it with negative value it throws an exception. Decrementing 0 count will NOT throw an exception but the count will stay on 0 instead.

### Runner

`Runner` is a reusable executor that allows you to wait until submitted tasks (of type `Runnable`, `Callable` or `Task`) have finished or failed.

Great if number of scheduled task is not known upfront but you want to wait till all of them finish.

```Java
    Runner runner = Runner.runner(numberOfThreads);

    runner.runIn(2, SECONDS, callable);
    runner.run(callable);


    // blocks until all tasks are finished (or failed)
    runner.waitTillDone();


    // and reuse it

    runner.runRunnableIn(500, MILLISECONDS, runnable);

    runner.waitTillDone();
    
    // or just repeat tasks until shutdown is triggered
    
    runner.run(shutdownInfo -> {
        while (shutdownInfo.wasShutdownTriggered() == false) {
            // do some cyclical task
        }
    })

    runner.shutdownAndAwaitTermination();

```

In case you would like to monitor task newly submitted to existing scheduled executor use `MonitoringTaskSubmitter` instead.

### Synchronizer

Adds ability to synchronize code on different objects for which the .equals() == true (e.g.: account number)

```Java
    Synchronizer<AccountId> synchronizer = new Synchronizer();
    
    ...

    // first thread
    synchronizer.synchronizeOn(accountId("accAAA"), () -> {
        long balance = loadBalance("accAAA")
        if (balance > 10_000) {
            decrementBalance("accAAA", 10_000)
        }
    })
    
    ...
    
    // second thread
    synchronizer.synchronizeOn(accountId("accAAA"), () -> {
        long balance = loadBalance("accAAA")
        if (balance > 2_000) {
            decrementBalance("accAAA", 2_000)
        }
    })
    
    ...
    
    // third thread - won't be blocked by previous threads
    synchronizer.synchronizeOn(accountId("accXYZ"), () -> {
        long balance = loadBalance("accXYZ")
        if (balance > 3_500) {
            decrementBalance("accXYZ", 3_500)
        }
    })
```

As both first and second thread are for the same id (accAAA) the second thread will be blocked until the first one finishes (so only one operation will run on the account accAAA).

On the other hand the third thread won't be blocked by either the first or the second thread and will run with them in parallel as it is executed for a different id (accXYZ).

## Objects

### Lazy

Allows to create objects whose initialization is called only once we call it's `value()` method.

```Java
    Lazy<HeavyObject> lazyValue = lazy(() -> heavyInitializationMethod());

    lazyValue.isInitialized();  // false - as not initialized yet
    
    HeavyObject actualValue = lazyValue.value();  // calls heavyInitializationMethod()
    lazyValue.isInitialized();  // true
    
    lazyValue.value();  // does NOT call heavyInitializationMethod() anymore (as already initialized)


    Lazy<String> derivedValue = lazyValue.map(heavyObject -> heavyObject.someValueOnHeavyObject());
```

Once initialization is successful the value is cached and initialization is not called anymore.
This class is thread/concurrent safe and guarantees to execute only one successful initialization. 

### Either

Unbiased implementation of Either. Usable if you'd like to return one of two different types.

Here are some example usages:

```Java
    Either<Exception, Response> result = methodReturningEither();

    String outcomeMessage = result
        .fold(
            exception -> exception.getMessage(),
            response -> response.getBody().toString(),
        );

    if (result.isRight()) {
        System.out.println("Yeaaah, we've got a response");
   }

    ResponseBody body = result
        .handleRight(response -> log(response)) // if we had right value
        .mapRight(response -> response.getBody())
        .ifLeftThrow(exception -> exception) // if we had an exception
        .swap()
        .getLeft();
```

### DataObject

Adds `equals`, `hashCode` and `toString` methods to domain objects:
```Java
    public class User extends DataObject {

        public final String firstName;
        public final String lastName;

        public User(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    new User("Terry", "Pratchett").equals(new User("Terry", "Pratchett"))
```

### Microtype

Immutable Single value holder. Microtypes help you with error-prone multi-parameter methods like for example constructor with 6 Strings parameters, where you can provide typed parameters instead, like: AccountNumber, UserId, Nationality, ...
It also implements `equals`, `hashCode` and `toString`.
```Java
    public class UserId extends Microtype<String> {

        public UserId(String value) {
            super(value);
        }

        public static UserId userId(String value) {
            return (value == null) ? null : new UserId(value);
        }
    }

    userId("mtymes").equals(userId("bjohnes"));

    userId("mtymes").value();

    String surname = userId("mtymes").map(value -> value.substring(1));
```
`Microtype` can't contain a `null` value and initialization with `null` value will throw an `IllegalArgumentException`

### Tuple / Triple

Immutable Two / Three values holder. Each `Tuple` / `Triple` extends `DataObject` so methods `equals`, `hashCode` and `toString` are provided by default.
```Java
    Tuple<UserId, User> tuple = tuple(
            new UserId("mtymes"),
            new User("Matej", "Tymes")
    );

    // you can access individual values
    UserId userId = tuple.a;
    User user = tuple.b;

    // or map them into something else
    String message = tuple.map(
        (userId, user) -> user.firstName + " " + user.lastName + "'s ID is: " + userId.value()
    );
```
`Tuple` / `Triple` can contain `null` values.

### ChangingValue

If you have a value that changes over time and want to have derived values that reflects these changes, you could use this hierarchy of classes. (great if you have a config that changes during runtime, e.g.: db connection, memcached location)
```Java
    // mutable wrapper of connection details
    MutableValue<ConnectionDetails> connectionDetails = mutableValue(
        originalConnectionDetails()
    );


    // database connection derived from connection details
    // each time connection details change this wrapper will create/refer to a new connection
    ChangingValue<DbConnection> dbConnection = connectionDetails.mapValueBuilder(
            details -> connectTo(details)
        )
        // and use this to dispose old connection if new one will be created
        .withDisposeFunction(
            connection -> releaseConnection(connection)
        )
        // and call this when value changes
        .withEachPotentialValueHandler(
            handleUsedValue(value -> System.out.println("we have a new connection"))
        )
        .build();


    // simple mapping - fetching database table using current connection
    ChangingValue<DbTable> usersTable = dbConnection.mapValue(
        connection -> connection.getDbTable("users")
    );


    // this will be executed on initial database connection 
    int recordCount = usersTable.mapCurrentValue(
        table -> table.getRecordsCount()
    );
    usersTable.forCurrentValue(
        table -> table.deleteRecord("agent Smith")
    );


    // update of connection details
    connectionDetails.updateValue(
        newConnectionDetails()
    );


    // table in NEW/updated connection is used to get the records count instead
    int recordCount2 = usersTable.mapCurrentValue(
        table -> table.getRecordsCount()
    );
```

## Collections

### LinkedArrayQueue

In all the cases where multiple items have to be collected just so they can be iterated over in some later stage. More performant or memory efficient than ArrayList or LinkedList while having constant (O(n)) performance on inserting last item as well as removing the first one.

### ByteQueue

Memory efficient and performant Queue for collecting and retrieving bytes. Great if you want to process upfront undefined amount or big batch of bytes.  

```Java
    ByteQueue queue = new ByteQueue(); // or new ByteQueue(pageSize); - default page size is 4kb (each time a page is filled a new one is created)

    // storing data

    byte singleByte = ... ;
    queue.addNext(singleByte);

    byte[] byteArray = ... ;
    queue.addNext(byteArray);

    byte[] byteArray = ... ;
    int offset = ... ;
    int length = ... ;
    queue.addNext(byteArray, offset, length);


    // info about data

    int size = queue.size();
    boolean isEmpty = queue.isEmpty();
    boolean hasNext = queue.hasNext();

    // polling data - removes from collection

    // streams through all bytes and removes them from Queue as they are being read
    ByteIterator pollingIterator = queue.pollingIterator();
    while(pollingIterator.hasNext()) {
        byte singleByte = pollingIterator.readNext();
    
        // or
        byte[] byteArray =  ... ;
        int removedByteCount = pollingIterator.readNext(byteArray);
    
        // or
        byte[] byteArray =  ... ;
        int offset = ... ;
        int length = ... ;
        int removedByteCount = pollingIterator.readNext(byteArray);
    }

    byte readByte = queue.pollNext();

    byte[] byteArray = ... ;
    int removedByteCount = queue.pollNext(byteArray);

    byte[] byteArray = ... ;
    int offset = ... ;
    int length = ... ;
    int removedByteCount = queue.pollNext(byteArray, offset, length);


    // peeking at data - doesn't remove from collection

    // allows you to stream through all bytes without removing them from queue
    ByteIterator peekingIterator = queue.peekingIterator(); // or queue.iterator() is peeking as well
    while(peekingIterator.hasNext()) {
        byte singleByte = peekingIterator.readNext();
    
        // or
        byte[] byteArray =  ... ;
        int readBytesCount = peekingIterator.readNext(byteArray);
    
        // or
        byte[] byteArray =  ... ;
        int offset = ... ;
        int length = ... ;
        int readBytesCount = peekingIterator.readNext(byteArray);
    }

    // these operations always look at the first byte/s - even on subsequent calls

    byte firstByteInQueue = queue.peekAtNext();

    byte[] firstBytesInQueue = ... ;
    int readBytesCount = queue.peekAtNext(firstBytesInQueue);

    byte[] firstBytesInQueue = ... ;
    int offset = ... ;
    int length = ... ;
    int readBytesCount = queue.peekAtNext(firstBytesInQueue, offset, length);

    // getting back a byte array (use only when streaming trough data is not possible as it creates additional byte array)

    byte[] allReadBytes = queue.peekAtAllBytes();

    byte[] allRemovedBytes = queue.pollAllBytes();
```

## IO

### ByteQueueOutputStream

There are currently 2 issues with `ByteArrayOutputStream`.
* collected data can't be directly transformed into InputStream
* each time the wrapped byte buffer has to be increased it creates another byte array and has to copy whole data again - which can be costly (from memory and performance point of view for big files)

`ByteQueueOutputStream` doesn't have to copy data when byte buffer has to be expanded. Instead, it just adds one additional small byte buffer to a linked list (by default size of buffer is 4kb).
Also it can be transformed into an `InputStream` (without need to copy the bytes) or you can get the underlying `ByteQueue` and access the underlying bytes directly.

```Java
    ByteQueueOutputStream bqoStream = new ByteQueueOutputStream(); // you can pass in your preferred ByteQueue as well
    
    // fill with bytes, e.g.:
    bqoStream.write(text.getBytes(charsetName));
    bqoStream.write(singleByte);
    ...
    stream.close();

    
    InputStream collectedBytesStream = bqoStream.toInputStream(removeReadBytesFromQueueBoolean); // no additional memory is allocated for collected bytes

    ByteQueue byteQueue = bqoStream.getByteQueue(); // get the underlying ByteQueue
    byte[] allBytesButStillKeptInTheQueue = byteQueue.peekAtAllBytes();
    byte[] allBytesButRemovedFromTheQueue = byteQueue.pollAllBytes();
```

just for comparison, when collecting data (in jdk15) we need:
| data size | ByteArrayOutputStream       | ByteQueueOutputStream       |
|-----------|-----------------------------|-----------------------------|
|  1024 MB  | 2.00 seconds / 2192 MB heap | 1.18 seconds / 1067 MB heap |
|   512 MB  | 1.10 seconds / 1155 MB heap | 0.66 seconds /  535 MB heap |
|   128 MB  | 0.25 seconds /  264 MB heap | 0.24 seconds /  135 MB heap |

## Math

### Decimal

Introducing new class `Decimal`, that should fix the troubles we're currently facing when dealing with `BigDecimal`. The new class was created as `BigDecimal`'s behavior can't be changed/patched because of backwards compatibility.

The advantages it provides are:

* `equals` reflects the `compareTo` behavior (plus `hashCode` is fixed respectively)

```Java
    assertThat(decimal("-1.2").equals(decimal("-1.200")), is(true));
    assertThat(decimal("-1.2").hashCode(), equalTo(decimal("-1.200").hashCode()));
```

* sensible defaults - in case rounding is needed (division, number de-scaling and de-precisioninig) `Decimal` uses rounding `HALF_UP` (the one we used in school) as default (but you can pass in your own rounding mode in case you want different result). Also the division produces by default results with precision of maximum 34 significant digits (once again you can pass your own limit, or define limit in number of decimal digits) 

* readable - able to use underscores during creation to improve readability (as in Java 7). Also can use short creation method `d(...)`:

```Java
    Decimal value = decimal("29_013_903_171.22");

    Decimal sum = d("0.456").plus(value);
```

* groovy and kotlin operators:

```Groovy
    def monthlyInterest = d("129_550.00") * d("0.03") / d("12");

    def totalDebt = d("129_550.00") + monthlyInterest * d("36");
```

```Kotlin
    val monthlyInterest = d("129_550.00") * d("0.03") / d("12");

    val totalDebt = d("129_550.00") + monthlyInterest * d("36");
```

* handy constants - one of the confusing thing about `BigDecimal` for newcomers is what is the difference between scale and precision. To ease the understanding `Decimal` provides readable constants on the `Scale` and `Precision` classes:

```Java
    d("123.4698").descaleTo(_2_DECIMAL_PLACES);  // = 123.47

    d("29_013_943_171.22").deprecisionTo(_7_SIGNIFICANT_DIGITS);  // = 29_013_940_000

    d("125_455_315").descaleTo(SCALE_OF_THOUSANDS);  // = 125_455_000
    d("125_455_315").descaleTo(SCALE_OF_MILLIONS);  // = 125_000_000
```

* non-confusing creation - `Decimal` always uses factory methods for creation, while `BigDecimal` sometimes uses constructor and sometimes factory method:
 
```Java
    // Decimal creation

    decimal(intValue);
    decimal(longValue);
    decimal(longValue, scale);
    decimal(bigIntegerValue);
    decimal(bigIntegerValue, scale);
    decimal(stringValue);

    // Decimal creation - short syntax

    d(intValue);
    d(longValue);
    d(longValue, scale);
    d(bigIntegerValue);
    d(bigIntegerValue, scale);
    d(stringValue);

    // BigDecimal creation

    new BigDecimal(intValue);
    new BigDecimal(longValue);
    BigDecimal.valueOf(longValue, scale);
    new BigDecimal(bigIntegerValue);
    new BigDecimal(bigIntegerValue, scale);
    new BigDecimal(stringValue);
```

* can evolve without affecting you - creation using only static factory methods doesn't expose defined types (you always refer to them as `Decimal`), so that the library can evolve without any changes needed on the users/client side.

* extendible (although not by you :D ) - `Decimal` is an abstract class, and currently supports two subtypes `LongDecimal` (for number with precision up to 19 digits - backed by `long`) and `HugeDecimal` for everything else (backed by `BigInteger`). The library handles the transitions between them seamlessly when doing math operation and always uses the least memory consuming type. There are plans the introduce additional types `InfinityDecimal` and `NANDecimal` (that will be disabled by default)

It is possible that you'll miss some math functions. To implement your own you can use these Decimal methods:
* `decimal.signum()` - will return -1 for negative value, 0 for zero and 1 for positive value
* `decimal.scale()` - will return you the number of decimal digits
* `decimal.unscaledValue()` - will return the unscaled value which can be of these two types: Long or BigInteger
* `decimal.precision()` - will return the number of significant digits

Also any improvements in forms of patches will be welcomed.