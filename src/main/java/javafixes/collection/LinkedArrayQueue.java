package javafixes.collection;

import java.util.*;

import static java.lang.Math.min;
import static javafixes.common.util.AssertUtil.assertGreaterThanZero;

// todo: mtymes - put into README.md
// todo: mtymes - test this
// todo: mtymes - add javadoc
public class LinkedArrayQueue<T> extends AbstractQueue<T> {

    private transient Node first;
    private transient Node last;

    private transient int size = 0;

    public LinkedArrayQueue(
            int pageSize
    ) {
        assertGreaterThanZero(pageSize, "pageSize");

        this.first = new Node(pageSize);
        this.last = this.first;
    }

    public LinkedArrayQueue() {
        this(128);
    }

    @Override
    public Iterator<T> iterator() {
        return peekingIterator();
    }

    // todo: pollNIterable - partition

    public Iterator<T> peekingIterator() {
        return new PeekingIterator();
    }

    public Iterator<T> pollingIterator() {
        return new PollingIterator();
    }

    public Iterable<T> asPeekingIterable() {
        return this::peekingIterator;
    }

    public Iterable<T> asPollingIterable() {
        return this::pollingIterator;
    }

    @Override
    public boolean isEmpty() {
        return !first.hasNext();
    }

    @Override
    public int size() {
        return size;
    }

    public int pageSize() {
        return first.values.length;
    }

    @Override
    public boolean offer(T t) {
        last.add(t);
        return true;
    }

    @Override
    public T poll() {
        if (first.hasNext()) {
            return first.remove();
        } else {
            return null;
        }
    }

    public List<T> pollN(int n) {
        List<T> values = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (first.hasNext()) {
                values.add(first.remove());
            } else {
                break;
            }
        }
        return values;
    }

    @Override
    public T peek() {
        if (first.hasNext()) {
            return first.peek();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LinkedArrayQueue)) {
            return false;
        }

        Iterator<T> thisIter = this.peekingIterator();
        Iterator<?> otherIter = ((LinkedArrayQueue<?>) other).peekingIterator();
        while (thisIter.hasNext() && otherIter.hasNext()) {
            T thisValue = thisIter.next();
            Object otherValue = otherIter.next();
            if (!Objects.equals(thisValue, otherValue)) {
                return false;
            }
        }

        return !(thisIter.hasNext() || otherIter.hasNext());
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        Iterator<T> iter = this.peekingIterator();
        while (iter.hasNext()) {
            T value = iter.next();
            hashCode = 31 * hashCode + (value == null ? 0 : value.hashCode());
        }

        return hashCode;
    }

    private class Node {

        int writeIndex = -1;
        int readIndex = -1;
        final Object[] values;

        Node next = null;

        private Node(int size) {
            values = new Object[size];
        }

        int size() {
            return min(writeIndex, values.length - 1) - min(readIndex, values.length - 1);
        }

        void add(T value) {
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

        T remove() {
            if (readIndex >= writeIndex) {
                throw new IndexOutOfBoundsException("No additional data");
            }
            int index = ++readIndex;
            if (index >= values.length) {
                if (next != null) {
                    first = next;
                    return next.remove();
                } else {
                    throw new IndexOutOfBoundsException("No additional data");
                }
            }
            T value = (T) values[index];
            values[index] = null;
            size--;
            return value;
        }

        T peek() {
            if (readIndex >= writeIndex) {
                throw new IndexOutOfBoundsException("No additional data");
            }
            int index = readIndex + 1;
            if (index >= values.length) {
                if (next != null) {
                    first = next;
                    return next.peek();
                } else {
                    throw new IndexOutOfBoundsException("No additional data");
                }
            }

            return (T) values[index];
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

    private class PeekingIterator implements Iterator<T> {

        private Node node;
        private int readIndex;

        private PeekingIterator() {
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
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Has no more values");
            }
            if (readIndex < node.readIndex) {
                throw new ConcurrentModificationException();
            }
            return (T) node.values[++readIndex];
        }
    }

    private class PollingIterator implements Iterator<T> {

        @Override
        public boolean hasNext() {
            return first.hasNext();
        }

        @Override
        public T next() {
            if (first.hasNext()) {
                return first.remove();
            } else {
                throw new NoSuchElementException("Has no more values");
            }
        }
    }

    // todo: mtymes - remove
//    public static void main(String[] args) {
//        LinkedArrayQueue<String> values1 = new LinkedArrayQueue<>();
//        values1.add("Hello");
//        values1.add("World");
//        values1.add("!");
//
//        LinkedArrayQueue<String> values2 = new LinkedArrayQueue<>();
//        values2.add("Hello");
//        values2.add("World");
//        values2.add("!");
//
//        System.out.println("values1.equals(values2) = " + values1.equals(values2));
//        System.out.println("values1.hashCode() = " + values1.hashCode());
//        System.out.println("values2.hashCode() = " + values2.hashCode());
//
//        System.out.println("values = " + values1);
//        Iterator<String> pollingIterator = values1.pollingIterator();
//        while (pollingIterator.hasNext()) {
//            System.out.println("- " + pollingIterator.next());
//        }
//        System.out.println("values = " + values1);
//    }

    // todo: mtymes - remove
//    public static void main(String[] args) {
//
//        long startTime = System.currentTimeMillis();
//
//        for (int i = 0; i < 10; i++) {
//            List<Integer> values = new ArrayList<>();
////            List<Integer> values = newLinkedList();
////            Queue<Integer> values = new LinkedArrayQueue<>(1024);
////            Queue<Integer> values = new LinkedBlockingQueue<>();
//
//            for (int j = 0; j < 102_000_000; j++) {
//                values.add(j);
////                values.offer(j);
//            }
//
//            while (!values.isEmpty()) {
//                values.remove(0);
////                values.poll();
//            }
//        }
//
//        long duration = System.currentTimeMillis() - startTime;
//
//        System.out.println(Duration.ofMillis(duration));
//    }
}
