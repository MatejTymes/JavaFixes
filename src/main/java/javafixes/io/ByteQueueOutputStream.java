package javafixes.io;

import javafixes.collection.ByteQueue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// todo: mtymes - test this
// todo: mtymes - add javadoc
public class ByteQueueOutputStream extends OutputStream {

    private final ByteQueue byteQueue;
    private boolean isClosed;

    public ByteQueueOutputStream(
            ByteQueue byteQueue
    ) {
        this.byteQueue = byteQueue;
        this.isClosed = false;
    }

    public InputStream toInputStream() {
        return toPeekingInputStream();
    }

    public InputStream toPeekingInputStream() {
        return toInputStream(false);
    }

    public InputStream toPollingInputStream() {
        return toInputStream(true);
    }

    public InputStream toInputStream(boolean removeReadBytesFromQueue) {
        return new ByteQueueInputStream(byteQueue, removeReadBytesFromQueue);
    }

    @Override
    public void write(int b) throws IOException {
        ensureIsNotClosed();

        byteQueue.add((byte) b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        ensureIsNotClosed();

        byteQueue.add(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        ensureIsNotClosed();

        byteQueue.add(b, off, len);
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
