package javafixes.experimental.collect;

import java.util.*;

import static java.lang.Math.min;

// todo: mtymes - remove from experimental package
// todo: mtymes - test this
public class LinkedArrayQueue<T> extends AbstractQueue<T> {

    private transient Node first;
    private transient Node last;

    private transient int size = 0;

    public LinkedArrayQueue(int arraySize) {
        this.first = new Node(arraySize);
        this.last = this.first;
    }

    public LinkedArrayQueue() {
        this(128);
    }

    @Override
    public Iterator<T> iterator() {
        return new NodeIterator();
    }

    @Override
    public boolean isEmpty() {
        return !first.hasNext();
    }

    @Override
    public int size() {
        return size;
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

    private class NodeIterator implements Iterator<T> {

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
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Has no value");
            }
            if (readIndex < node.readIndex) {
                throw new ConcurrentModificationException();
            }
            return (T) node.values[++readIndex];
        }
    }

    // todo: mtymes - remove
//    public static void main(String[] args) {
//
//        long startTime = System.currentTimeMillis();
//
//        for (int i = 0; i < 10; i++) {
////            List<Integer> values = new ArrayList<>();
////            List<Integer> values = newLinkedList();
//            Queue<Integer> values = new LinkedArrayQueue<>(1024);
////            Queue<Integer> values = new LinkedBlockingQueue<>();
//
//            for (int j = 0; j < 102_000_000; j++) {
//                values.add(j);
////                values.offer(j);
//            }
//
////            while (!values.isEmpty()) {
////                values.remove(0);
//////                values.poll();
////            }
//        }
//
//        long duration = System.currentTimeMillis() - startTime;
//
//        System.out.println(Duration.ofMillis(duration));
//    }
}
