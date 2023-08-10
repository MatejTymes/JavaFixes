package javafixes.collection;

import java.util.AbstractQueue;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.min;
import static javafixes.common.util.AssertUtil.assertGreaterThanZero;

// todo: mtymes - add javadoc
public class ByteQueue extends AbstractQueue<Byte> {

    private transient final Object writeLock = new Object();
    private transient final Object pollLock = new Object();

    private transient Node first;
    private transient Node last;

    private transient final AtomicInteger size = new AtomicInteger(0);

    public ByteQueue(
            int pageSize
    ) {
        assertGreaterThanZero(pageSize, "pageSize");

        this.first = new Node(pageSize);
        this.last = this.first;
    }

    public ByteQueue() {
        this(4 * 1024);
    }

    @Override
    public ByteIterator iterator() {
        return peekingIterator();
    }

    public ByteIterator peekingIterator() {
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
        addNext(value);
        return true;
    }

    public void addNext(byte value) {
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

    public void addNext(byte[] bytes) {
        addNext(bytes, 0, bytes.length);
    }

    public void addNext(byte[] bytes, int offset, int length) {
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
                return pollNext();
            } else {
                return null;
            }
        }
    }

    public byte pollNext() {
        synchronized (pollLock) {
            Node firstNode = first;
            do {
                int prevReadIndex = firstNode.readIndex;
                int lastArrayIndex = firstNode.values.length - 1;
                int writeIndex = firstNode.writeIndex;

                if (prevReadIndex >= writeIndex && prevReadIndex < lastArrayIndex) {
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

    public int pollNext(byte[] bytes) {
        return pollNext(bytes, 0, bytes.length);
    }

    public int pollNext(byte[] bytes, int offset, int length) {
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
            return peekAtNext();
        } else {
            return null;
        }
    }

    public byte peekAtNext() {
        Node firstNode = first;
        do {
            int prevReadIndex = firstNode.readIndex;
            int lastArrayIndex = firstNode.values.length - 1;
            int writeIndex = firstNode.writeIndex;

            if (prevReadIndex >= writeIndex && prevReadIndex < lastArrayIndex) {
                throw new NoSuchElementException("No additional data");
            }

            if (prevReadIndex >= lastArrayIndex) {
                if (firstNode.next == null) {
                    throw new NoSuchElementException("No additional data");
                } else {
                    firstNode = firstNode.next;
                }
            } else {
                return firstNode.values[prevReadIndex + 1];
            }
        } while (true);
    }

    public int peekAtNext(byte[] bytes) {
        return peekAtNext(bytes, 0, bytes.length);
    }

    public int peekAtNext(byte[] bytes, int offset, int length) {
        ByteQueueReader reader = new ByteQueueReader();
        return reader.readNext(bytes, offset, length);
    }

    public byte[] toByteArray() {
        int arrayLength;
        ByteQueueReader reader;

        synchronized (pollLock) {
            synchronized (writeLock) {
                reader = new ByteQueueReader();
                arrayLength = size();
            }
        }

        byte[] bytes = new byte[arrayLength];
        reader.readNext(bytes, 0, arrayLength);

        return bytes;
    }

    public byte[] pollAllBytes() {
        synchronized (pollLock) {
            synchronized (writeLock) {
                int arrayLength = size();

                byte[] bytes = new byte[arrayLength];
                pollNext(bytes, 0, arrayLength);

                return bytes;
            }
        }
    }

    static class Node {

        volatile int writeIndex = -1;
        volatile int readIndex = -1;
        final byte[] values;

        Node next = null;

        private Node(int size) {
            values = new byte[size];
        }
    }


    public class ByteQueueReader implements ByteIterator {

        private Node node;
        private int prevReadIndex;

        private ByteQueueReader() {
            this.node = first;
            this.prevReadIndex = node.readIndex;
        }

        @Override
        public boolean hasNext() {
            do {
                if (prevReadIndex < node.values.length - 1) {
                    return prevReadIndex < node.writeIndex;
                } else if (node.next == null) {
                    return false;
                }

                node = node.next;
                prevReadIndex = -1;
            } while (true);
        }

        @Override
        public byte readNext() {
            do {
                if (prevReadIndex < node.values.length - 1) {
                    if (prevReadIndex >= node.writeIndex) {
                        throw new NoSuchElementException("No additional data");
                    }
                    return node.values[++prevReadIndex];
                } else if (node.next == null) {
                    throw new NoSuchElementException("No additional data");
                } else {
                    node = node.next;
                    prevReadIndex = -1;
                }
            } while (true);
        }

        @Override
        public int readNext(byte[] bytes, int offset, int length) {
            if (length == 0) {
                return hasNext() ? 0 : -1;
            }

            int bytesAdded = 0;

            while (length > 0) {
                int lastArrayIndex = node.values.length - 1;
                int writeIndex = node.writeIndex;

                if (prevReadIndex >= lastArrayIndex) {
                    if (node.next != null) {
                        node = node.next;
                        prevReadIndex = -1;
                        continue;
                    } else {
                        break;
                    }
                } else if (prevReadIndex >= writeIndex) {
                    break;
                } else {
                    int readNBytes = min(length, min(lastArrayIndex, writeIndex) - prevReadIndex);

                    System.arraycopy(node.values, prevReadIndex + 1, bytes, offset, readNBytes);

                    prevReadIndex += readNBytes;

                    bytesAdded += readNBytes;

                    offset += readNBytes;
                    length -= readNBytes;
                }
            }

            return (bytesAdded == 0) ? -1 : bytesAdded;
        }
    }

    public class ByteQueuePoller implements ByteIterator {

        @Override
        public boolean hasNext() {
            return ByteQueue.this.hasNext();
        }

        @Override
        public byte readNext() {
            return ByteQueue.this.pollNext();
        }

        @Override
        public int readNext(byte[] bytes, int offset, int length) {
            return ByteQueue.this.pollNext(bytes, offset, length);
        }
    }
}
