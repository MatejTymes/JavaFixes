package javafixes.beta.io;

import javafixes.beta.collection.ByteQueue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static javafixes.common.Asserts.assertGreaterThanZero;

// todo: mtymes - test this
// todo: mtymes - add javadoc
public class ConvertedInputStream extends InputStream {

    @FunctionalInterface
    public interface ConverterProvider {
        OutputStream provideConverter(OutputStream outputByteBuffer) throws IOException;
    }

    private final InputStream sourceInputStream;
    private final OutputStream convertingOutputStream;
    private final ByteQueue byteQueue;
    private final int fetchBytesBatchSize;

    private boolean isInputStreamFullyRead = false;
    private boolean isClosed = false;

    public ConvertedInputStream(
            InputStream sourceInputStream,
            ConverterProvider converterProvider,
            int fetchBytesBatchSize,
            int internalQueuePageSize
    ) throws IOException {
        assertGreaterThanZero(fetchBytesBatchSize, "fetchBytesBatchSize");

        this.sourceInputStream = sourceInputStream;
        this.byteQueue = new ByteQueue(internalQueuePageSize);
        this.convertingOutputStream = converterProvider.provideConverter(new ByteQueueOutputStream(byteQueue));
        this.fetchBytesBatchSize = fetchBytesBatchSize;
    }

    public ConvertedInputStream(
            InputStream sourceInputStream,
            ConverterProvider converterProvider,
            int fetchBytesBatchSize
    ) throws IOException {
        this(sourceInputStream, converterProvider, fetchBytesBatchSize, 4 * 1024);
    }

    public ConvertedInputStream(
            InputStream sourceInputStream,
            ConverterProvider converterProvider
    ) throws IOException {
        this(sourceInputStream, converterProvider, 4 * 1024);
    }

    @Override
    public int read() throws IOException {
        ensureIsNotClosed();

        while (byteQueue.isEmpty()) {
            if (isInputStreamFullyRead) {
                return -1;
            }

            processMoreBytes();
        }

        return byteQueue.pollNextByte() & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        ensureIsNotClosed();

        if (len == 0) {
            while (byteQueue.isEmpty()) {
                if (isInputStreamFullyRead) {
                    return -1;
                }
                processMoreBytes();
            }
            return 0;
        }

        int allReadBytesCount = 0;

        outer:
        while (allReadBytesCount < len) {
            while (byteQueue.isEmpty()) {
                if (isInputStreamFullyRead) {
                    break outer;
                }

                processMoreBytes();
            }

            int readBytesCount = byteQueue.pollNextBytes(b, off + allReadBytesCount, len - allReadBytesCount);
            if (readBytesCount == -1) {
                break outer;
            } else {
                allReadBytesCount += readBytesCount;
            }
        }

        return (allReadBytesCount == 0) ? -1 : allReadBytesCount;
    }

    @Override
    public void close() throws IOException {
        if (!this.isClosed) {
            try {
                super.close();
            } finally {
                try {
                    try {
                        sourceInputStream.close();
                    } finally {
                        convertingOutputStream.close();
                    }
                } finally {
                    this.isClosed = true;
                }
            }
        }
    }

    private void processMoreBytes() throws IOException {
        byte[] tempBytes = new byte[fetchBytesBatchSize];
        int bytesCount = sourceInputStream.read(tempBytes);
        if (bytesCount == -1) {
            isInputStreamFullyRead = true;
            convertingOutputStream.flush();
            convertingOutputStream.close();
        } else {
            convertingOutputStream.write(tempBytes);
            convertingOutputStream.flush();
        }
    }

    private void ensureIsNotClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException(this.getClass().getSimpleName() + " is closed");
        }
    }
}
