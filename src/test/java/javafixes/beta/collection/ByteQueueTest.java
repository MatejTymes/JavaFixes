package javafixes.beta.collection;

import org.junit.Test;

import static java.util.UUID.randomUUID;
import static javafixes.test.Random.randomInt;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ByteQueueTest {

    @Test
    public void shouldCollectAndRetrieveBytes() throws Exception {
        ByteQueue queue = new ByteQueue(randomInt(8, 128));

        assertThat(queue.isEmpty(), is(true));
        assertThat(queue.hasNext(), is(false));
        assertThat(queue.size(), equalTo(0));

        String originalText = "This is a random string " + randomUUID() + " with some random " + randomUUID() + " values in it";
        String charsetName = "UTF-8";

        byte[] originalBytes = originalText.getBytes(charsetName);
        queue.add(originalBytes, 0, originalBytes.length);

        assertThat(queue.isEmpty(), is(false));
        assertThat(queue.hasNext(), is(true));
        assertThat(queue.size(), equalTo(originalBytes.length));

        byte[] newBytes = new byte[originalBytes.length];

        int bytesRead = queue.pollNext(newBytes, 0, originalBytes.length);
        assertThat(bytesRead, equalTo(originalBytes.length));
        assertThat(queue.isEmpty(), is(true));
        assertThat(queue.hasNext(), is(false));
        assertThat(queue.size(), equalTo(0));

        assertThat(new String(newBytes, charsetName), equalTo(originalText));
    }
}