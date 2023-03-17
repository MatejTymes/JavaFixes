package javafixes.beta.collection;

import java.util.AbstractQueue;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static java.lang.Math.min;

// todo: mtymes - test this
// todo: mtymes - add javadoc
// todo: mtymes - maybe rename to ResizableByteQueue/LinkedArrayByteQueue
public class ByteQueue extends AbstractQueue<Byte> {

    private transient Node first;
    private transient Node last;

    private transient int size = 0;

    public ByteQueue(int arraySize) {
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
        return !first.hasNext();
    }

    public boolean hasNext() {
        return first.hasNext();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean offer(Byte value) {
        last.add(value);
        return true;
    }

    public void add(byte value) {
        last.add(value);
    }

    public void add(byte[] bytes, int offset, int length) {
        for (int i = 0; i < length; i++) {
            last.add(bytes[offset]);
            offset++;
        }
    }

    @Override
    public Byte poll() {
        if (first.hasNext()) {
            return first.poll();
        } else {
            return null;
        }
    }

    public byte pollNextByte() {
        return first.poll();
    }

    public int pollNextBytes(byte[] bytes, int offset, int length) {
        if (length == 0) {
            return hasNext() ? 0 : -1;
        }

        int bytesAdded = 0;
        int currentOffset = offset;
        int remainingLength = length;
        // todo: mtymes - maybe add faster implementation
        while (remainingLength > 0 && hasNext()) {
            bytes[currentOffset] = pollNextByte();

            bytesAdded++;
            currentOffset++;
            remainingLength--;
        }

        return (bytesAdded == 0) ? -1 : bytesAdded;
    }

    @Override
    public Byte peek() {
        if (first.hasNext()) {
            return first.peek();
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

        int size() {
            return min(writeIndex, values.length - 1) - min(readIndex, values.length - 1);
        }

        void add(byte value) {
            int index = ++writeIndex;
            if (index >= values.length) {
                if (next == null) {
                    next = new Node(values.length);
                    last = next;
                }
                next.add(value);
            } else {
                values[index] = value;
                size++;
            }
        }

        byte poll() {
            if (readIndex >= writeIndex && readIndex < values.length - 1) {
                throw new NoSuchElementException("No additional data");
            }
            int index = ++readIndex;
            if (index >= values.length) {
                if (next != null) {
                    first = next;
                    return next.poll();
                } else {
                    throw new NoSuchElementException("No additional data");
                }
            }
            byte value = values[index];
            values[index] = 0;
            size--;
            return value;
        }

        byte peek() {
            if (readIndex >= writeIndex && readIndex < values.length - 1) {
                throw new NoSuchElementException("No additional data");
            }
            int index = readIndex + 1;
            if (index >= values.length) {
                if (next != null) {
                    first = next;
                    return next.peek();
                } else {
                    throw new NoSuchElementException("No additional data");
                }
            }

            return values[index];
        }

        boolean hasNext() {
            if (readIndex < values.length - 1) {
                return readIndex < writeIndex;
            } else if (next != null) {
                return next.hasNext();
            } else {
                return false;
            }
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
            Node node = currentNode;
            int readIndex = currentReadIndex;
            do {
                if (readIndex < node.values.length - 1) {
                    return readIndex < node.writeIndex;
                } else if (node.next == null) {
                    return false;
                }

                node = node.next;
                readIndex = -1;
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

        // todo: mtymes - maybe add faster implementation for: int readNext(byte[] bytes, int offset, int length)
    }
}
