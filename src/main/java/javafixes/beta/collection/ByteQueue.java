package javafixes.beta.collection;

import java.util.AbstractQueue;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.min;
import static javafixes.common.Asserts.assertGreaterThanZero;

// todo: mtymes - test this
// todo: mtymes - add javadoc
public class ByteQueue extends AbstractQueue<Byte> {

    // todo: mtymes - currently peeking is not greatly guarded by concurrency - do we want it like that
    // todo: mtymes - add methods: toByteArray(): byte[] & fillByteArray(byte[] bytes, int offset, int length): int

    private transient final Object writeLock = new Object();
    private transient final Object pollLock = new Object();

    private transient Node first;
    private transient Node last;

    private transient final AtomicInteger size = new AtomicInteger(0);

    public ByteQueue(
            int arraySize
    ) {
        assertGreaterThanZero(arraySize, "arraySize");

        this.first = new Node(arraySize);
        this.last = this.first;
    }

    public ByteQueue() {
        this(4 * 1024);
    }

    @Override
    public ByteIterator iterator() {
        return new ByteQueueReader();
    }

    public ByteIterator pollingIterator() {
        return new ByteQueuePoller();
    }

    @Override
    public boolean isEmpty() {
        return !hasNext();
    }

    public boolean hasNext() {
        do {
            if (first.readIndex < first.values.length - 1) {
                return first.readIndex < first.writeIndex;
            } else if (first.next != null) {
                first = first.next;
            } else {
                return false;
            }
        } while (true);
    }

    @Override
    public int size() {
        return size.get();
    }

    @Override
    public boolean offer(Byte value) {
        add(value);
        return true;
    }

    public void add(byte value) {
        synchronized (writeLock) {
            Node lastNode = last;
            do {
                int nextWriteIndex = lastNode.writeIndex + 1;
                if (nextWriteIndex < lastNode.values.length) {
                    lastNode.values[nextWriteIndex] = value;
                    lastNode.writeIndex++;
                    size.incrementAndGet();
                    return;
                } else {
                    if (lastNode.next == null) {
                        lastNode.next = new Node(lastNode.values.length);
                        last = lastNode.next;
                    }
                    lastNode = lastNode.next;
                }
            } while (true);
        }
    }

    public void add(byte[] bytes, int offset, int length) {
        synchronized (writeLock) {
            Node lastNode = last;
            while (length > 0) {
                int writeIndex = lastNode.writeIndex;
                int arrayLength = lastNode.values.length;

                if (writeIndex < arrayLength - 1) {
                    int writeNBytes = min(length, arrayLength - 1 - writeIndex);

                    System.arraycopy(bytes, offset, lastNode.values, writeIndex + 1, writeNBytes);

                    lastNode.writeIndex += writeNBytes;
                    size.addAndGet(writeNBytes);

                    offset += writeNBytes;
                    length -= writeNBytes;

                    if (length > 0) {
                        if (lastNode.next == null) {
                            lastNode.next = new Node(last.values.length);
                            last = lastNode.next;
                        }
                        lastNode = lastNode.next;
                    }
                } else {
                    if (lastNode.next == null) {
                        lastNode.next = new Node(last.values.length);
                        last = lastNode.next;
                    }
                    lastNode = lastNode.next;
                }
            }
        }
    }

    @Override
    public Byte poll() {
        synchronized (pollLock) {
            if (hasNext()) {
                return pollNextByte();
            } else {
                return null;
            }
        }
    }

    public byte pollNextByte() {
        synchronized (pollLock) {
            Node firstNode = first;
            do {
                if (firstNode.readIndex >= firstNode.writeIndex && firstNode.readIndex < firstNode.values.length - 1) {
                    throw new NoSuchElementException("No additional data");
                }
                int index = ++firstNode.readIndex;
                if (index >= firstNode.values.length) {
                    firstNode.readIndex = firstNode.values.length;
                    if (firstNode.next == null) {
                        throw new NoSuchElementException("No additional data");
                    } else {
                        first = firstNode.next;
                        firstNode = firstNode.next;
                    }
                } else {
                    byte value = firstNode.values[index];
                    size.decrementAndGet();
                    return value;
                }
            } while(true);
        }
    }

    public int pollNextBytes(byte[] bytes, int offset, int length) {
        synchronized (pollLock) {
            if (length == 0) {
                return hasNext() ? 0 : -1;
            }

            int bytesAdded = 0;
            int currentOffset = offset;
            int remainingLength = length;
            while (remainingLength > 0 && hasNext()) {
                // todo: mtymes - currently way too slow - speedup
                bytes[currentOffset] = pollNextByte();

                bytesAdded++;
                currentOffset++;
                remainingLength--;
            }

            return (bytesAdded == 0) ? -1 : bytesAdded;
        }
    }

    @Override
    public Byte peek() {
        if (hasNext()) {
            return peekAtNextByte();
        } else {
            return null;
        }
    }

    public byte peekAtNextByte() {
        return first.peek();
    }


    class Node {

        int writeIndex = -1;
        int readIndex = -1;
        final byte[] values;

        Node next = null;

        private Node(int size) {
            values = new byte[size];
        }

//        int size() {
//            return min(writeIndex, values.length - 1) - min(readIndex, values.length - 1);
//        }

//        void add(byte value) {
//            int nextWriteIndex = writeIndex + 1;
//            if (nextWriteIndex < values.length) {
//                values[nextWriteIndex] = value;
//                writeIndex++;
//                size++;
//            } else {
//                if (next == null) {
//                    next = new Node(values.length);
//                    last = next;
//                }
//                next.add(value);
//            }
//        }

//        void add(byte[] bytes, int offset, int length) {
//            Node lastNode = this;
//            while (length > 0) {
//                int writeIndex = lastNode.writeIndex;
//                int arrayLength = lastNode.values.length;
//
//                if (writeIndex < arrayLength - 1) {
//                    int writeNBytes = min(length, arrayLength - 1 - writeIndex);
//
//                    System.arraycopy(bytes, offset, lastNode.values, writeIndex + 1, writeNBytes);
//
//                    lastNode.writeIndex += writeNBytes;
//                    size += writeNBytes;
//
//                    offset += writeNBytes;
//                    length -= writeNBytes;
//
//                    if (length > 0) {
//                        if (lastNode.next == null) {
//                            lastNode.next = new Node(values.length);
//                            last = lastNode.next;
//                        }
//                        lastNode = lastNode.next;
//                    }
//                } else {
//                    if (lastNode.next == null) {
//                        lastNode.next = new Node(values.length);
//                        last = lastNode.next;
//                    }
//                    lastNode = lastNode.next;
//                }
//            }
//        }

//        byte poll() {
//            if (readIndex >= writeIndex && readIndex < values.length - 1) {
//                throw new NoSuchElementException("No additional data");
//            }
//            int index = ++readIndex;
//            if (index >= values.length) {
//                readIndex = values.length;
//                if (next == null) {
//                    throw new NoSuchElementException("No additional data");
//                } else {
//                    if (this == first) {
//                        first = next;
//                    }
//                    return next.poll();
//                }
//            } else {
//                byte value = values[index];
//                values[index] = 0;
//                size.decrementAndGet();
//                return value;
//            }
//        }

        byte peek() {
            if (readIndex >= writeIndex && readIndex < values.length - 1) {
                throw new NoSuchElementException("No additional data");
            }
            int index = readIndex + 1;
            if (index >= values.length) {
                readIndex = values.length;
                if (next == null) {
                    throw new NoSuchElementException("No additional data");
                } else {
                    if (this == first) {
                        first = next;
                    }
                    return next.peek();
                }
            } else {
                return values[index];
            }
        }

//        boolean hasNext() {
//            if (readIndex < values.length - 1) {
//                return readIndex < writeIndex;
//            } else if (next != null) {
//                if (this == first) {
//                    first = next;
//                }
//                return next.hasNext();
//            } else {
//                return false;
//            }
//        }
    }


    public class ByteQueueReader implements ByteIterator {

        private Node currentNode;
        private int currentReadIndex;

        private ByteQueueReader() {
            this.currentNode = first;
            this.currentReadIndex = currentNode.readIndex;
        }

        @Override
        public boolean hasNext() {
            do {
                if (currentReadIndex < currentNode.values.length - 1) {
                    return currentReadIndex < currentNode.writeIndex;
                } else if (currentNode.next == null) {
                    return false;
                }

                currentNode = currentNode.next;
                currentReadIndex = -1;
            } while (true);
        }

        @Override
        public byte readNext() {
            do {
                if (currentReadIndex < currentNode.values.length - 1) {
                    if (currentReadIndex >= currentNode.writeIndex) {
                        throw new NoSuchElementException("No additional data");
                    } else if (currentReadIndex < currentNode.readIndex) {
                        throw new ConcurrentModificationException("This data has been removed and is no longer available");
                    }
                    return currentNode.values[++currentReadIndex];
                } else if (currentNode.next == null) {
                    throw new NoSuchElementException("No additional data");
                } else {
                    currentNode = currentNode.next;
                    currentReadIndex = -1;
                }
            } while (true);
        }

        // todo: mtymes - maybe add faster implementation for: int readNext(byte[] bytes, int offset, int length)
    }

    public class ByteQueuePoller implements ByteIterator {

        @Override
        public boolean hasNext() {
            return ByteQueue.this.hasNext();
        }

        @Override
        public byte readNext() {
            return ByteQueue.this.pollNextByte();
        }

        @Override
        public int readNext(byte[] bytes, int offset, int length) {
            return ByteQueue.this.pollNextBytes(bytes, offset, length);
        }
    }
}
