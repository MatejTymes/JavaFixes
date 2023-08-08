package javafixes.collection;

import org.junit.Test;

import java.util.List;

import static java.lang.Math.max;
import static java.util.UUID.randomUUID;
import static javafixes.collection.util.CollectionUtil.newList;
import static javafixes.test.Random.randomByte;
import static javafixes.test.Random.randomInt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ByteQueueTest {

    @Test
    public void shouldWorkCorrectly() {
        ByteQueue queue = new ByteQueue(randomInt(1, 20));

        List<Byte> expectedBytes = newList();
        assertQueueContent(queue, expectedBytes);


        byte b = randomByte();
        queue.offer(b);

        expectedBytes.add(b);
        assertQueueContent(queue, expectedBytes);
    }

    @Test
    public void shouldCollectAndRetrieveBytes() throws Exception {
        ByteQueue queue = new ByteQueue(randomInt(1, 20));

        assertQueueSize(queue, 0);

        String originalText = "This is a random string " + randomUUID() + " with some random " + randomUUID() + " values in it";
        String charsetName = "UTF-8";

        byte[] originalBytes = originalText.getBytes(charsetName);
        queue.addNext(originalBytes, 0, originalBytes.length);

        assertQueueSize(queue, originalBytes.length);

        byte[] newBytes = new byte[originalBytes.length];

        int bytesRead = queue.pollNext(newBytes, 0, originalBytes.length);
        assertThat(bytesRead, equalTo(originalBytes.length));
        assertQueueSize(queue, 0);

        assertThat(new String(newBytes, charsetName), equalTo(originalText));
    }

    private void assertQueueSize(ByteQueue queue, int expectedSize) {
        assertThat(queue.isEmpty(), is(expectedSize <= 0));
        assertThat(queue.hasNext(), is(expectedSize > 0));
        assertThat(queue.size(), equalTo(expectedSize));
    }

    private void assertQueueContent(ByteQueue queue, List<Byte> expectedBytes) {
        byte[] actualBytes = queue.toByteArray();

        assertAreEqual(actualBytes, expectedBytes);

        assertQueueSize(queue, expectedBytes.size());
    }

    private static void assertAreEqual(byte[] actualBytes, List<Byte> expectedBytes) {
        boolean isAMatch = true;
        if (actualBytes.length != expectedBytes.size()) {
            isAMatch = false;
        } else {
            for (int i = 0; i < actualBytes.length; i++) {
                byte actualByte = actualBytes[i];
                byte expectedByte = expectedBytes.get(i);
                if (actualByte != expectedByte) {
                    isAMatch = false;
                    break;
                }
            }
        }
        if (!isAMatch) {
            String actualBytesString = "[ ";
            String expectedBytesString = "[ ";
            int maxLength = max(actualBytes.length, expectedBytes.size());
            for (int i = 0; i < maxLength; i++) {
                if (i >= actualBytes.length) {
                    if (i > 0) {
                        expectedBytesString += ", ";
                    }
                    expectedBytesString += expectedBytes.get(i) + " *";
                } else if (i >= expectedBytes.size()) {
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
                    byte expectedByte = expectedBytes.get(i);
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
}