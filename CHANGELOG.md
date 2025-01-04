### 1.4.3.0
- CollectionUtil: added .unmodifiableListCopy(...), .unmodifiableSetCopy(...), .unmodifiableLinkedSetCopy(...), .unmodifiableMapCopy(...), .unmodifiableLinkedMapCopy(...)

### 1.4.2.0 - 2024-05-18
- ByteQueue & LinkedArrayQueue: added .equals(...) and .hashCode()
- removed ByteCollectingOutputStream
- javadoc updates

### 1.4.1.0 - 2023-12-28
- Collections & IO
  - ByteQueue: renamed .toByteArray() -> .peekAtAllBytes()
  - deprecated: ByteCollectingOutputStream, InputStreamUtil
  - ByteQueueOutputStream: added: .getByteQueue()
- ChangingValue
  - ChangingValue:
    - added: .mapCurrent(...), .forCurrent(...), .map(...), .mapValue(...), .mapBuilder(...), .mapValueBuilder(...), .join(...), .joinValues(...), .joinBuilder(...), .joinValuesBuilder(...), .cacheBuilder()
    - renamed: .mapToValue(...) -> .mapCurrentValue(...)
  - added FailableValueMapper
  - DerivedValue: simplified generics & started to use FailableValueMapper
  - FailableValue
    - improved error message
  - MutableValue: added .updateValue(...) while ignoring difference check
  - ChangingValueBuilder: renamed .asCachedValue() -> .cacheBuilder()
  - DerivedValueBuilder: simplified generics & started to use FailableValueMapper
  - ChangingValueUtil: removed .mappingValue(...)
- javadoc updates

### 1.4.0.0 - 2023-11-19
- ChangingValue - implementation of new feature
- Collections & IO
  - added byte streaming and queuing functionality via new classes: ByteQueueInputStream, ByteQueueOutputStream, ConvertedInputStream, GzipCompressingInputStream, ByteIterable, ByteIterator, ByteQueue
  - LinkedArrayQueue: added ability to poll as well as peek
  - added: CollectionUtil, InitializationFailedException, TriConsumer, AssertUtil, StreamUtil
  - moved into deprecated package: ByteCollectingOutputStream, InputStreamUtil
- Concurrency
  - Runner: is now Closeable
  - WrappedException: moved into javafixes.common.exception package
- Math
  - Decimal now uses new exception UnsupportedDecimalTypeException
- Common
  - added: SmartComparable

### 1.3.7.1 - 2021-05-25

TODO: provide older history