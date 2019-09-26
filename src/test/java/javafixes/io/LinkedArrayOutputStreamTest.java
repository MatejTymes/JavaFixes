package javafixes.io;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class LinkedArrayOutputStreamTest {


    @Test
    public void shouldCollectBytes() throws IOException {
        String text = "This is a random string " + randomUUID() + " with some random " + randomUUID() + " values in it";

        LinkedArrayOutputStream stream = new LinkedArrayOutputStream(5);

        stream.write(text.getBytes("UTF-8"));
        stream.close();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        stream.copyTo(os);

        assertThat(new String(os.toByteArray(), "UTF-8"), equalTo(text));
        assertThat(IOUtils.toString(stream.toInputStream(), "UTF-8"), equalTo(text));
    }
}