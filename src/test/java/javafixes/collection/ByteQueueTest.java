package javafixes.collection;

import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.copyOf;
import static java.util.Collections.shuffle;
import static java.util.UUID.randomUUID;
import static javafixes.collection.util.CollectionUtil.newList;
import static javafixes.test.Random.*;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ByteQueueTest {

    private interface QueueOperation {
        void run(ByteQueue queue, List<Byte> expectedBytes);
    }

    private final List<QueueOperation> operations = newList(
            (queue, expectedBytes) -> {
                System.out.println("queue.offer(b)");
                byte b = randomByte();
                queue.offer(b);
                expectedBytes.add(b);
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.addNext(b)");
                byte b = randomByte();
                queue.addNext(b);
                expectedBytes.add(b);
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.addNext(noBytes)");
                byte[] noBytes = {};
                queue.addNext(noBytes);
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.addNext(bytes)");
                byte[] bytes = randomByteArray();
                queue.addNext(bytes);
                for (byte b : bytes) {
                    expectedBytes.add(b);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.addNext(bytes, 0, bytes.length)");
                byte[] bytes = randomByteArray();
                queue.addNext(bytes, 0, bytes.length);
                for (byte b : bytes) {
                    expectedBytes.add(b);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.addNext(noBytes, 0, 0)");
                byte[] noBytes = {};
                queue.addNext(noBytes, 0, 0);
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.addNext(bytes, offset, length)");
                byte[] bytes = randomByteArray();
                int offset = randomOffset(bytes);
                int length = randomLength(bytes, offset);
                queue.addNext(bytes, offset, length);
                for (int i = 0; i < length; i++) {
                    expectedBytes.add(bytes[offset + i]);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.poll()");
                if (expectedBytes.isEmpty()) {
                    assertThat(queue.poll(), is(nullValue()));
                } else {
                    assertThat(queue.poll(), equalTo(expectedBytes.remove(0)));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollNext()");
                if (expectedBytes.isEmpty()) {
                    try {
                        queue.pollNext();
                        fail("expected NoSuchElementException");
                    } catch (NoSuchElementException expectedException) {
                        assertThat(expectedException.getMessage(), equalTo("No additional data"));
                    }
                } else {
                    assertThat(queue.poll(), equalTo(expectedBytes.remove(0)));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollNext(noBytes)");
                byte[] noBytes = {};
                if (expectedBytes.isEmpty()) {
                    assertThat(queue.pollNext(noBytes), equalTo(-1));
                } else {
                    assertThat(queue.pollNext(noBytes), equalTo(0));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollNext(bytes)");
                byte[] bytes = randomByteArray();

                if (expectedBytes.isEmpty()) {
                    assertThat(queue.pollNext(bytes), equalTo(-1));
                } else {
                    int copiedBytesCount = min(bytes.length, expectedBytes.size());

                    byte[] expectedByteArray = copy(bytes);
                    assertThat(queue.pollNext(bytes), equalTo(copiedBytesCount));
                    for (int i = 0; i < copiedBytesCount; i++) {
                        expectedByteArray[i] = expectedBytes.remove(0);
                    }
                    assertAreEqual(bytes, expectedByteArray);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollNext(noBytes, 0, 0)");
                byte[] noBytes = {};
                if (expectedBytes.isEmpty()) {
                    assertThat(queue.pollNext(noBytes, 0, 0), equalTo(-1));
                } else {
                    assertThat(queue.pollNext(noBytes, 0, 0), equalTo(0));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollNext(bytes, 0, bytes.length)");
                byte[] bytes = randomByteArray();

                if (expectedBytes.isEmpty()) {
                    assertThat(queue.pollNext(bytes, 0, bytes.length), equalTo(-1));
                } else {
                    int copiedBytesCount = min(bytes.length, expectedBytes.size());

                    byte[] expectedByteArray = copy(bytes);
                    assertThat(queue.pollNext(bytes, 0, bytes.length), equalTo(copiedBytesCount));
                    for (int i = 0; i < copiedBytesCount; i++) {
                        expectedByteArray[i] = expectedBytes.remove(0);
                    }
                    assertAreEqual(bytes, expectedByteArray);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollNext(bytes, offset, length)");
                byte[] bytes = randomByteArray();
                int offset = randomOffset(bytes);
                int length = randomLength(bytes, offset);

                if (expectedBytes.isEmpty()) {
                    assertThat(queue.pollNext(bytes, offset, length), equalTo(-1));
                } else {
                    int copiedBytesCount = min(length, expectedBytes.size());

                    byte[] expectedByteArray = copy(bytes);
                    assertThat(queue.pollNext(bytes, offset, length), equalTo(copiedBytesCount));
                    for (int i = 0; i < copiedBytesCount; i++) {
                        expectedByteArray[i + offset] = expectedBytes.remove(0);
                    }
                    assertAreEqual(bytes, expectedByteArray);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.peek()");
                if (expectedBytes.isEmpty()) {
                    assertThat(queue.peek(), is(nullValue()));
                } else {
                    assertThat(queue.peek(), equalTo(expectedBytes.get(0)));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.peekAtNext()");
                if (expectedBytes.isEmpty()) {
                    try {
                        queue.peekAtNext();
                        fail("expected NoSuchElementException");
                    } catch (NoSuchElementException expectedException) {
                        assertThat(expectedException.getMessage(), equalTo("No additional data"));
                    }
                } else {
                    assertThat(queue.peekAtNext(), equalTo(expectedBytes.get(0)));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.peekAtNext(noBytes)");
                byte[] noBytes = {};
                if (expectedBytes.isEmpty()) {
                    assertThat(queue.peekAtNext(noBytes), equalTo(-1));
                } else {
                    assertThat(queue.peekAtNext(noBytes), equalTo(0));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.peekAtNext(bytes)");
                byte[] bytes = randomByteArray();

                if (expectedBytes.isEmpty()) {
                    assertThat(queue.peekAtNext(bytes), equalTo(-1));
                } else {
                    int copiedBytesCount = min(bytes.length, expectedBytes.size());

                    byte[] expectedByteArray = copy(bytes);
                    assertThat(queue.peekAtNext(bytes), equalTo(copiedBytesCount));
                    for (int i = 0; i < copiedBytesCount; i++) {
                        expectedByteArray[i] = expectedBytes.get(i);
                    }
                    assertAreEqual(bytes, expectedByteArray);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.peekAtNext(noBytes, 0, 0)");
                byte[] noBytes = {};
                if (expectedBytes.isEmpty()) {
                    assertThat(queue.peekAtNext(noBytes, 0, 0), equalTo(-1));
                } else {
                    assertThat(queue.peekAtNext(noBytes, 0, 0), equalTo(0));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.peekAtNext(bytes, 0, bytes.length)");
                byte[] bytes = randomByteArray();

                if (expectedBytes.isEmpty()) {
                    assertThat(queue.peekAtNext(bytes, 0, bytes.length), equalTo(-1));
                } else {
                    int copiedBytesCount = min(bytes.length, expectedBytes.size());

                    byte[] expectedByteArray = copy(bytes);
                    assertThat(queue.peekAtNext(bytes, 0, bytes.length), equalTo(copiedBytesCount));
                    for (int i = 0; i < copiedBytesCount; i++) {
                        expectedByteArray[i] = expectedBytes.get(i);
                    }
                    assertAreEqual(bytes, expectedByteArray);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.peekAtNext(bytes, offset, length)");
                byte[] bytes = randomByteArray();
                int offset = randomOffset(bytes);
                int length = randomLength(bytes, offset);

                if (expectedBytes.isEmpty()) {
                    assertThat(queue.peekAtNext(bytes, offset, length), equalTo(-1));
                } else {
                    int copiedBytesCount = min(length, expectedBytes.size());

                    byte[] expectedByteArray = copy(bytes);
                    assertThat(queue.peekAtNext(bytes, offset, length), equalTo(copiedBytesCount));
                    for (int i = 0; i < copiedBytesCount; i++) {
                        expectedByteArray[i + offset] = expectedBytes.get(i);
                    }
                    assertAreEqual(bytes, expectedByteArray);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollingIterator().readNext()");
                if (expectedBytes.isEmpty()) {
                    try {
                        queue.pollingIterator().readNext();
                        fail("expected NoSuchElementException");
                    } catch (NoSuchElementException expectedException) {
                        assertThat(expectedException.getMessage(), equalTo("No additional data"));
                    }
                } else {
                    assertThat(queue.pollingIterator().readNext(), equalTo(expectedBytes.remove(0)));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollingIterator().readNext(noBytes)");
                byte[] noBytes = {};
                if (expectedBytes.isEmpty()) {
                    assertThat(queue.pollingIterator().readNext(noBytes), equalTo(-1));
                } else {
                    assertThat(queue.pollingIterator().readNext(noBytes), equalTo(0));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollingIterator().readNext(bytes)");
                byte[] bytes = randomByteArray();

                if (expectedBytes.isEmpty()) {
                    assertThat(queue.pollingIterator().readNext(bytes), equalTo(-1));
                } else {
                    int copiedBytesCount = min(bytes.length, expectedBytes.size());

                    byte[] expectedByteArray = copy(bytes);
                    assertThat(queue.pollingIterator().readNext(bytes), equalTo(copiedBytesCount));
                    for (int i = 0; i < copiedBytesCount; i++) {
                        expectedByteArray[i] = expectedBytes.remove(0);
                    }
                    assertAreEqual(bytes, expectedByteArray);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollingIterator().readNext(noBytes, 0, 0)");
                byte[] noBytes = {};
                if (expectedBytes.isEmpty()) {
                    assertThat(queue.pollingIterator().readNext(noBytes, 0, 0), equalTo(-1));
                } else {
                    assertThat(queue.pollingIterator().readNext(noBytes, 0, 0), equalTo(0));
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollingIterator().readNext(bytes, 0, bytes.length)");
                byte[] bytes = randomByteArray();

                if (expectedBytes.isEmpty()) {
                    assertThat(queue.pollingIterator().readNext(bytes, 0, bytes.length), equalTo(-1));
                } else {
                    int copiedBytesCount = min(bytes.length, expectedBytes.size());

                    byte[] expectedByteArray = copy(bytes);
                    assertThat(queue.pollingIterator().readNext(bytes, 0, bytes.length), equalTo(copiedBytesCount));
                    for (int i = 0; i < copiedBytesCount; i++) {
                        expectedByteArray[i] = expectedBytes.remove(0);
                    }
                    assertAreEqual(bytes, expectedByteArray);
                }
            },
            (queue, expectedBytes) -> {
                System.out.println("queue.pollingIterator().readNext(bytes, offset, length)");
                byte[] bytes = randomByteArray();
                int offset = randomOffset(bytes);
                int length = randomLength(bytes, offset);

                if (expectedBytes.isEmpty()) {
                    assertThat(queue.pollingIterator().readNext(bytes, offset, length), equalTo(-1));
                } else {
                    int copiedBytesCount = min(length, expectedBytes.size());

                    byte[] expectedByteArray = copy(bytes);
                    assertThat(queue.pollingIterator().readNext(bytes, offset, length), equalTo(copiedBytesCount));
                    for (int i = 0; i < copiedBytesCount; i++) {
                        expectedByteArray[i + offset] = expectedBytes.remove(0);
                    }
                    assertAreEqual(bytes, expectedByteArray);
                }
            }
    );

    @Test
    public void shouldWorkCorrectlyAgainstEmptyQueue() {
        for (QueueOperation operation : operations) {
            ByteQueue queue = new ByteQueue(randomInt(1, 20));
            List<Byte> expectedBytes = newList();

            operation.run(queue, expectedBytes);

            assertQueueContent(queue, expectedBytes);
        }
    }

    @Test
    public void shouldWorkCorrectlyIfAppliedInSequence() {
        ByteQueue queue = new ByteQueue(randomInt(1, 20));
        List<Byte> expectedBytes = newList();

        for (QueueOperation operation : operations) {
            operation.run(queue, expectedBytes);

            assertQueueContent(queue, expectedBytes);
        }
    }

    @Test
    public void shouldWorkCorrectlyWhenAppliedRandomly() {
        List<QueueOperation> shuffledOperations = newList(operations);
        for (int i = 0; i < 18 * 1024; i++) {
            shuffledOperations.add(pickRandomValue(operations));
        }

        shuffle(shuffledOperations);

        ByteQueue queue = new ByteQueue(randomInt(1, 20));
        List<Byte> expectedBytes = newList();

        for (QueueOperation operation : shuffledOperations) {
            operation.run(queue, expectedBytes);

            assertQueueContent(queue, expectedBytes);
        }
    }

//    @Test
//    public void shouldCollectAndRetrieveBytes() throws Exception {
//        ByteQueue queue = new ByteQueue(randomInt(1, 20));
//
//        assertQueueSize(queue, 0);
//
//        String originalText = "This is a random string " + randomUUID() + " with some random " + randomUUID() + " values in it";
//        String charsetName = "UTF-8";
//
//        byte[] originalBytes = originalText.getBytes(charsetName);
//        queue.addNext(originalBytes, 0, originalBytes.length);
//
//        assertQueueSize(queue, originalBytes.length);
//
//        byte[] newBytes = new byte[originalBytes.length];
//
//        int bytesRead = queue.pollNext(newBytes, 0, originalBytes.length);
//        assertThat(bytesRead, equalTo(originalBytes.length));
//        assertQueueSize(queue, 0);
//
//        assertThat(new String(newBytes, charsetName), equalTo(originalText));
//    }

    private void assertQueueSize(ByteQueue queue, int expectedSize) {
        assertThat(queue.isEmpty(), is(expectedSize <= 0));
        assertThat(queue.hasNext(), is(expectedSize > 0));
        assertThat(queue.size(), equalTo(expectedSize));

        assertThat(queue.iterator().hasNext(), is(expectedSize > 0));
        assertThat(queue.pollingIterator().hasNext(), is(expectedSize > 0));
        assertThat(queue.peekingIterator().hasNext(), is(expectedSize > 0));
    }

    private void assertQueueContent(ByteQueue queue, List<Byte> expectedBytes) {
        byte[] actualBytes = queue.toByteArray();
        assertAreEqual(actualBytes, expectedBytes);

        actualBytes = new byte[queue.size()];
        queue.peekAtNext(actualBytes);
        assertAreEqual(actualBytes, expectedBytes);

        actualBytes = new byte[queue.size()];
        queue.peekAtNext(actualBytes, 0, actualBytes.length);
        assertAreEqual(actualBytes, expectedBytes);

        actualBytes = new byte[queue.size()];
        ByteIterator peekingIterator = queue.iterator();
        for(int i = 0; i < actualBytes.length; i++) {
            actualBytes[i] = peekingIterator.readNext();
        }
        assertAreEqual(actualBytes, expectedBytes);

        actualBytes = new byte[queue.size()];
        peekingIterator = queue.iterator();
        peekingIterator.readNext(actualBytes);
        assertAreEqual(actualBytes, expectedBytes);

        actualBytes = new byte[queue.size()];
        peekingIterator = queue.iterator();
        peekingIterator.readNext(actualBytes, 0, actualBytes.length);
        assertAreEqual(actualBytes, expectedBytes);

        actualBytes = new byte[queue.size()];
        peekingIterator = queue.peekingIterator();
        for(int i = 0; i < actualBytes.length; i++) {
            actualBytes[i] = peekingIterator.readNext();
        }
        assertAreEqual(actualBytes, expectedBytes);

        actualBytes = new byte[queue.size()];
        peekingIterator = queue.peekingIterator();
        peekingIterator.readNext(actualBytes);
        assertAreEqual(actualBytes, expectedBytes);

        actualBytes = new byte[queue.size()];
        peekingIterator = queue.peekingIterator();
        int readBytesCount = 0;
        while (readBytesCount < actualBytes.length) {
            byte[] tempBytes = new byte[randomInt(1, actualBytes.length - readBytesCount)];
            int acquiredNBytes = peekingIterator.readNext(tempBytes);
            if (acquiredNBytes < 1) {
                break;
            }
            System.arraycopy(tempBytes, 0, actualBytes, readBytesCount, acquiredNBytes);

            readBytesCount += acquiredNBytes;
        }
        assertAreEqual(actualBytes, expectedBytes);

        actualBytes = new byte[queue.size()];
        peekingIterator = queue.peekingIterator();
        peekingIterator.readNext(actualBytes, 0, actualBytes.length);
        assertAreEqual(actualBytes, expectedBytes);

        assertQueueSize(queue, expectedBytes.size());
    }

    private static void assertAreEqual(byte[] actualBytes, byte[] expectedBytes) {
        boolean isAMatch = true;
        if (actualBytes.length != expectedBytes.length) {
            isAMatch = false;
        } else {
            for (int i = 0; i < actualBytes.length; i++) {
                byte actualByte = actualBytes[i];
                byte expectedByte = expectedBytes[i];
                if (actualByte != expectedByte) {
                    isAMatch = false;
                    break;
                }
            }
        }
        if (!isAMatch) {
            String actualBytesString = "[ ";
            String expectedBytesString = "[ ";
            int maxLength = max(actualBytes.length, expectedBytes.length);
            for (int i = 0; i < maxLength; i++) {
                if (i >= actualBytes.length) {
                    if (i > 0) {
                        expectedBytesString += ", ";
                    }
                    expectedBytesString += expectedBytes[i] + " *";
                } else if (i >= expectedBytes.length) {
                    if (i > 0) {
                        actualBytesString += ", ";
                    }
                    actualBytesString += actualBytes[i] + " *";
                } else {
                    if (i > 0) {
                        actualBytesString += ", ";
                        expectedBytesString += ", ";
                    }

                    byte actualByte = actualBytes[i];
                    byte expectedByte = expectedBytes[i];
                    if (actualByte == expectedByte) {
                        actualBytesString += actualByte;
                        expectedBytesString += expectedByte;
                    } else {
                        actualBytesString += actualByte + " *";
                        expectedBytesString += expectedByte + " *";
                    }
                }
            }
            actualBytesString += " ]";
            expectedBytesString += " ]";

            assertThat(actualBytesString, equalTo(expectedBytesString));
        }
    }

    private static void assertAreEqual(byte[] actualBytes, List<Byte> expectedBytes) {
        assertAreEqual(actualBytes, toByteArray(expectedBytes));
    }

    public static int randomOffset(byte[] values) {
        int lastArrayIndex = values.length - 1;
        return randomInt(0, lastArrayIndex);
    }

    private static int randomLength(byte[] values, int startIndex) {
        int lastArrayIndex = values.length - 1;
        return randomInt(0, lastArrayIndex - startIndex);
    }

    private static byte[] toByteArray(List<Byte> values) {
        byte[] expectedByteArray = new byte[values.size()];
        for (int i = 0; i < values.size(); i++) {
            expectedByteArray[i] = values.get(i);
        }
        return expectedByteArray;
    }

    private static byte[] copy(byte[] values) {
        return copyOf(values, values.length);
    }
}