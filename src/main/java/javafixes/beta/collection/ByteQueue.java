package javafixes.beta.collection;

import java.util.AbstractQueue;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static java.lang.Math.min;
import static javafixes.common.Asserts.assertGreaterThanZero;

// todo: mtymes - test this
// todo: mtymes - add javadoc
public class ByteQueue extends AbstractQueue<Byte> {

    private transient Node first; // todo: mtymes - change into AtomicReference
    private transient Node last; // todo: mtymes - change into AtomicReference

    private transient int size = 0; // todo: mtymes - change into AtomicReference

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
        if (first.readIndex < first.values.length - 1) {
            return first.readIndex < first.writeIndex;
        } else if (first.next != null) {
            first = first.next;
            return first.next.hasNext();
        } else {
            return false;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean offer(Byte value) {
        add(value);
        return true;
    }

    public void add(byte value) {
        Node lastNode = last;
        do {
            int nextWriteIndex = lastNode.writeIndex + 1;
            if (nextWriteIndex < lastNode.values.length) {
                lastNode.values[nextWriteIndex] = value;
                lastNode.writeIndex++;
                size++;
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

    public void add(byte[] bytes, int offset, int length) {
        Node lastNode = last;
        while (length > 0) {
            int writeIndex = lastNode.writeIndex;
            int arrayLength = lastNode.values.length;

            if (writeIndex < arrayLength - 1) {
                int writeNBytes = min(length, arrayLength - 1 - writeIndex);

                System.arraycopy(bytes, offset, lastNode.values, writeIndex + 1, writeNBytes);

                lastNode.writeIndex += writeNBytes;
                size += writeNBytes;

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

        Node next = null; // todo: mtymes - change into AtomicReference

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

        byte poll() {
            if (readIndex >= writeIndex && readIndex < values.length - 1) {
                throw new NoSuchElementException("No additional data");
            }
            int index = ++readIndex;
            if (index >= values.length) {
                readIndex = values.length;
                if (next == null) {
                    throw new NoSuchElementException("No additional data");
                } else {
                    if (this == first) {
                        first = next;
                    }
                    return next.poll();
                }
            } else {
                byte value = values[index];
                values[index] = 0;
                size--;
                return value;
            }
        }

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

        boolean hasNext() {
            if (readIndex < values.length - 1) {
                return readIndex < writeIndex;
            } else if (next != null) {
                if (this == first) {
                    first = next;
                }
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
