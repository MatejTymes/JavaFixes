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
                int prevWriteIndex = lastNode.writeIndex;
                int lastArrayIndex = lastNode.values.length - 1;

                if (prevWriteIndex < lastArrayIndex) {
                    int writeNBytes = min(length, lastArrayIndex - prevWriteIndex);

                    System.arraycopy(bytes, offset, lastNode.values, prevWriteIndex + 1, writeNBytes);

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
                int prevReadIndex = firstNode.readIndex;
                int lastArrayIndex = firstNode.values.length - 1;
                if (prevReadIndex >= firstNode.writeIndex && prevReadIndex < lastArrayIndex) {
                    throw new NoSuchElementException("No additional data");
                }
                int nextReadIndex = ++firstNode.readIndex;
                if (nextReadIndex >= firstNode.values.length) {
                    firstNode.readIndex = lastArrayIndex;
                    if (firstNode.next == null) {
                        throw new NoSuchElementException("No additional data");
                    } else {
                        first = firstNode.next;
                        firstNode = firstNode.next;
                    }
                } else {
                    byte value = firstNode.values[nextReadIndex];
                    size.decrementAndGet();
                    return value;
                }
            } while (true);
        }
    }

    public int pollNextBytes(byte[] bytes, int offset, int length) {
        synchronized (pollLock) {
            if (length == 0) {
                return hasNext() ? 0 : -1;
            }

            int bytesAdded = 0;

            Node firstNode = first;
            while (length > 0) {
                int prevReadIndex = firstNode.readIndex;
                int lastArrayIndex = firstNode.values.length - 1;
                int writeIndex = firstNode.writeIndex;

                if (prevReadIndex >= lastArrayIndex) {
                    if (firstNode.next != null) {
                        first = firstNode.next;
                        firstNode = firstNode.next;
                        continue;
                    } else {
                        break;
                    }
                } else if (prevReadIndex >= writeIndex) {
                    break;
                } else {
                    int readNBytes = min(length, min(lastArrayIndex, writeIndex) - prevReadIndex);

                    System.arraycopy(firstNode.values, prevReadIndex + 1, bytes, offset, readNBytes);

                    firstNode.readIndex += readNBytes;
                    size.addAndGet(-readNBytes);

                    bytesAdded += readNBytes;

                    offset += readNBytes;
                    length -= readNBytes;
                }
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
        Node firstNode = first;
        do {
            int prevReadIndex = firstNode.readIndex;
            int lastArrayIndex = firstNode.values.length - 1;
            int writeIndex = firstNode.writeIndex;

            if (prevReadIndex >= writeIndex && prevReadIndex < lastArrayIndex) {
                throw new NoSuchElementException("No additional data");
            }

            if (prevReadIndex >= lastArrayIndex) {
                firstNode = firstNode.next;
            } else {
                return firstNode.values[prevReadIndex + 1];
            }
        } while (true);
    }


    class Node {

        int writeIndex = -1; // todo: mtymes - change to volatile ???
        int readIndex = -1; // todo: mtymes - change to volatile ???
        final byte[] values;

        Node next = null; // todo: mtymes - change to volatile ???

        private Node(int size) {
            values = new byte[size];
        }
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
