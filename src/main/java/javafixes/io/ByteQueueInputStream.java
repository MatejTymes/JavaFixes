package javafixes.io;

import javafixes.collection.ByteIterator;
import javafixes.collection.ByteQueue;

import java.io.IOException;
import java.io.InputStream;

// todo: mtymes - test this
// todo: mtymes - add javadoc
public class ByteQueueInputStream extends InputStream {

    private final ByteIterator iterator;
    private boolean isClosed;

    public ByteQueueInputStream(
            ByteIterator iterator
    ) {
        this.iterator = iterator;
    }

    public ByteQueueInputStream(
            ByteQueue byteQueue,
            boolean removeReadBytesFromQueue
    ) {
        this(
                removeReadBytesFromQueue
                        ? byteQueue.pollingIterator()
                        : byteQueue.peekingIterator()
        );
    }

    @Override
    public int read() throws IOException {
        ensureIsNotClosed();

        if (!iterator.hasNext()) {
            return -1;
        }

        return iterator.readNext() & 0xFF;
    }

    @Override
    public int read(byte[] b) throws IOException {
        ensureIsNotClosed();

        return iterator.readNext(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        ensureIsNotClosed();

        return iterator.readNext(b, off, len);
    }

    @Override
    public void close() throws IOException {
        if (!this.isClosed) {
            try {
                super.close();
            } finally {
                this.isClosed = true;
            }
        }
    }

    private void ensureIsNotClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException(this.getClass().getSimpleName() + " is closed");
        }
    }
}
