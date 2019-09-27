package javafixes.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.Math.min;

// todo: add into readme
// todo: javadoc
public class LinkedArrayOutputStream extends OutputStream {

    private transient final Object lock = new Object();

    private transient final Node first;
    private transient Node last;

    private transient boolean closed = false;

    public LinkedArrayOutputStream(int arraySize) {
        this.first = new Node(arraySize);
        this.last = this.first;
    }

    public LinkedArrayOutputStream() {
        this(4 * 1024);
    }

    @Override
    public void write(int b) throws IOException {
        last.add((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        last.add(b, off, len);
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            closed = true;
        }
    }

    public void copyTo(OutputStream os) throws IOException {
        synchronized (lock) {
            if (!closed) {
                throw new IllegalStateException("Unable to copy content to OutputStream. LinkedArrayOutputStream is not closed yet");
            }

            Node node = first;
            do {
                if (node.writeToIndex != 0) {
                    os.write(node.bytes, 0, node.writeToIndex);
                }
                node = node.next;
            } while(node != null);
        }
    }

    public InputStream toInputStream() {
        synchronized (lock) {
            if (!closed) {
                throw new IllegalStateException("Unable to provide InputStream. LinkedArrayOutputStream is not closed yet");
            }

            return new InputStream() {
                int readIndex = 0;
                Node node = first;

                @Override
                public int read() throws IOException {
                    if (readIndex == node.writeToIndex) {
                        return -1;
                    }

                    int value = node.bytes[readIndex];
                    readIndex++;
                    if (readIndex == node.bytes.length && node.next != null) {
                        node = node.next;
                        readIndex = 0;
                    }
                    return value;
                }
            };
        }
    }

    private class Node {

        private final byte[] bytes;
        private int writeToIndex = 0;

        private Node next = null;

        private Node(int size) {
            this.bytes = new byte[size];
        }

        private void add(byte b) {
            synchronized (lock) {
                if (closed) {
                    throw new IllegalStateException("Unable to write any more bytes. LinkedArrayOutputStream is closed");
                }

                if (writeToIndex < bytes.length) {
                    bytes[writeToIndex] = b;
                    writeToIndex++;
                } else {
                    nextNode().add(b);
                }
            }
        }

        private void add(byte[] b, int off, int len) {
            synchronized (lock) {
                if (closed) {
                    throw new IllegalStateException("Unable to write any more bytes. LinkedArrayOutputStream is closed");
                }

                Node node = this;
                while (len > 0) {
                    int writeIndex = node.writeToIndex;
                    int arrayLength = node.bytes.length;

                    if (writeIndex < arrayLength) {
                        int writeNBytes = min(len, arrayLength - writeIndex);

                        System.arraycopy(b, off, node.bytes, writeIndex, writeNBytes);

                        node.writeToIndex = node.writeToIndex + writeNBytes;
                        off = off + writeNBytes;
                        len = len - writeNBytes;

                        if (len > 0) {
                            node = node.nextNode();
                        }
                    } else {
                        node = node.nextNode();
                    }
                }
            }
        }

        private Node nextNode() {
            if (next == null) {
                next = new Node(bytes.length);
                LinkedArrayOutputStream.this.last = next;
            }
            return next;
        }
    }
}
