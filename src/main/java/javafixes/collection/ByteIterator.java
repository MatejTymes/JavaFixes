package javafixes.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A byte related enhancement of Iterator interface. In addition to the common functionality
 * it adds ability to retrieve the bytes without the need for boxing/conversion into Byte class.
 * as well as provides functionality for batch retrieval of bytes.
 *
 * @author mtymes
 */
public interface ByteIterator extends Iterator<Byte> {

    @Override
    default Byte next() {
        return readNext();
    }

    /**
     * Returns next {@code byte} in the iteration
     *
     * @return next {@code byte} in the iteration
     * @throws NoSuchElementException if the iteration has no more bytes
     */
    byte readNext() throws NoSuchElementException;

    /**
     * Stores available iteration bytes into the input byte array parameter (buffer).
     * Then the method returns the number of bytes stored into this buffer.
     * If iteration has no more bytes available it returns -1 instead.
     * The bytes are stored into the buffer sequentially with the first index being 0
     * (e.g. they are stored into buffer[0], buffer[1], buffer[2], ...)
     * and the process stops if the last index of the buffer is reached or there are no more bytes in the iteration.
     *
     * @param buffer byte array into which the iteration bytes will be stored
     * @return -1 if iteration has no more bytes available or number of bytes stored into the buffer
     */
    default int readNext(byte[] buffer) {
        return readNext(buffer, 0, buffer.length);
    }

    /**
     * Stores available iteration bytes into the input byte array parameter (buffer).
     * Then the method returns the number of bytes stored into this buffer.
     * If iteration has no more bytes available it returns -1 instead.
     * The bytes are stored into the buffer sequentially with the first index being the offset parameter
     * (e.g. they are stored into buffer[offset], buffer[offset + 1], buffer[offset + 2], ...)
     * and the process stops if the last index of the buffer is reached or there are no more bytes in the iteration
     * or the number of stored bytes reached the value of length parameter.
     *
     * @param buffer byte array into which the iteration bytes will be stored
     * @param offset initial index within the buffer where the bytes storage will start
     * @param length maximum amount of bytes that can be stored into the buffer byte array
     * @return -1 if iteration has no more bytes available or number of bytes stored into the buffer
     */
    default int readNext(byte[] buffer, int offset, int length) {
        if (length == 0) {
            return hasNext() ? 0 : -1;
        }

        int bytesAdded = 0;
        int currentOffset = offset;
        int remainingLength = length;
        while (remainingLength > 0 && hasNext()) {
            buffer[currentOffset] = readNext();

            bytesAdded++;
            currentOffset++;
            remainingLength--;
        }

        return (bytesAdded == 0) ? -1 : bytesAdded;
    }
}
