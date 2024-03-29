package javafixes.io;

import javafixes.collection.ByteQueue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// todo: mtymes - test this
// todo: mtymes - add javadoc
// todo: mtymes - add method ByteCollectingOutputStream.writeTo(OutputStream os) ???
// todo: mtymes - should we generate InputStream only when the stream is closed ???
public class ByteQueueOutputStream extends OutputStream {

    private final ByteQueue byteQueue;
    private boolean isClosed;

    public ByteQueueOutputStream(
            ByteQueue byteQueue
    ) {
        this.byteQueue = byteQueue;
        this.isClosed = false;
    }

    public ByteQueueOutputStream() {
        this(new ByteQueue());
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

    public ByteQueue getByteQueue() {
        return byteQueue;
    }

    @Override
    public void write(int b) throws IOException {
        ensureIsNotClosed();

        byteQueue.addNext((byte) b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        ensureIsNotClosed();

        byteQueue.addNext(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        ensureIsNotClosed();

        byteQueue.addNext(b, off, len);
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
