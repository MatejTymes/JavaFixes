package javafixes.beta.collection;

import java.util.AbstractQueue;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.lang.Math.min;

// todo: mtymes - add ByteQueueCursor class
// todo: mtymes - rename to ResizableByteQueue/LinkedArrayByteQueue
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
    public Iterator<Byte> iterator() {
        return new NodeIterator();
    }

    public ByteQueueCursor pollingIterator() {
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
            return first.remove();
        } else {
            return null;
        }
    }

    public byte removeNextByte() {
        return first.remove();
    }

    public int removeNextBytes(byte[] bytes, int offset, int length) {
        if (length == 0) {
            return hasNext() ? 0 : -1;
        }

        int bytesAdded = 0;
        int currentOffset = offset;
        int remainingLength = length;
        // todo: mtymes - maybe add faster implementation
        while (remainingLength > 0 && hasNext()) {
            bytes[currentOffset] = remove();

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

        byte remove() {
            if (readIndex >= writeIndex) {
                throw new NoSuchElementException("No additional data");
            }
            int index = ++readIndex;
            if (index >= values.length) {
                if (next != null) {
                    first = next;
                    return next.remove();
                } else {
                    throw new NoSuchElementException("No additional data");
                }
            }
            byte value = values[index];
            // todo: mtymes - should we clear out the stored value ???
//            values[index] = 0;
            size--;
            return value;
        }

        byte peek() {
            if (readIndex >= writeIndex) {
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

    // todo: mtymes - merge with ByteQueueReader
    private class NodeIterator implements Iterator<Byte> {

        private Node node;
        private int readIndex;

        private NodeIterator() {
            this.node = first;
            this.readIndex = this.node.readIndex;
        }

        @Override
        public boolean hasNext() {
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
        public Byte next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Has no more values");
            }
            if (readIndex < node.readIndex) {
                throw new ConcurrentModificationException();
            }
            return node.values[++readIndex];
        }
    }


    public class ByteQueueReader implements ByteQueueCursor {

        private Node currentNode = ByteQueue.this.first;
        private int currentReadIndex = currentNode.readIndex;

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
        public Byte next() {
            return readNext();
        }

        @Override
        public byte readNext() {
            if (!hasNext()) {
                throw new NoSuchElementException("Has no more values");
            }

            do {
                if (currentReadIndex < currentNode.values.length - 1) {
                    return currentNode.values[++currentReadIndex];
                } else {
                    currentNode = currentNode.next;
                    currentReadIndex = -1;
                }
            } while (true);
        }

        @Override
        public int readNext(byte[] bytes, int offset, int length) {
            if (length == 0) {
                return hasNext() ? 0 : -1;
            }

            int bytesAdded = 0;
            int currentOffset = offset;
            int remainingLength = length;
            // todo: mtymes - maybe add faster implementation
            while (remainingLength > 0 && hasNext()) {
                bytes[currentOffset] = readNext();

                bytesAdded++;
                currentOffset++;
                remainingLength--;
            }

            return (bytesAdded == 0) ? -1 : bytesAdded;
        }
    }

    public class ByteQueuePoller implements ByteQueueCursor {

        @Override
        public boolean hasNext() {
            return ByteQueue.this.hasNext();
        }

        @Override
        public Byte next() {
            return null;
        }

        @Override
        public byte readNext() {
            return ByteQueue.this.removeNextByte();
        }

        @Override
        public int readNext(byte[] bytes, int offset, int length) {
            return ByteQueue.this.removeNextBytes(bytes, offset, length);
        }
    }

    public static interface ByteQueueCursor extends Iterator<Byte> {
        boolean hasNext();

        byte readNext();

        int readNext(byte[] bytes, int offset, int length);
    }
}
