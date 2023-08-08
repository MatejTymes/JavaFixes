package javafixes.beta.io;

import javafixes.beta.collection.ByteQueue;

import java.io.IOException;
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
