package javafixes.experimental.json5;

import com.google.common.io.CharStreams;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class Json5ToJsonReaderTest {

    @Test
    public void shouldReturnJsonUnchanged() throws IOException {
        // todo: add every single json feature in here
        String jsonWithoutCommentary = "{ \"hello\": \"world\" }";
        StringReader jsonReader = new StringReader(jsonWithoutCommentary);

        Reader json5ToJsonReader = new Json5ToJsonReader(jsonReader);

        String convertedJson = CharStreams.toString(json5ToJsonReader);
        assertThat(convertedJson, equalTo("{\"hello\":\"world\"}"));
    }

    // todo: add commentary inside of string
    @Test
    public void shouldFilterMultiLineComment() throws IOException {
        String jsonWithMultiLineCommentary = "{ \"hello\"/*\n: // \"not\" \n */: \"world\" }";
        StringReader jsonReader = new StringReader(jsonWithMultiLineCommentary);

        Reader json5ToJsonReader = new Json5ToJsonReader(jsonReader);

        String convertedJson = CharStreams.toString(json5ToJsonReader);
        assertThat(convertedJson, equalTo("{\"hello\":\"world\"}"));
    }

    // todo: add commentary inside of string
    @Test
    public void shouldFilterSingleLineComment() throws IOException {
        String jsonWithSingleLineCommentary = "{ \"hello\"//: /* \"not\" \n: \"world\" }";
        StringReader jsonReader = new StringReader(jsonWithSingleLineCommentary);

        Reader json5ToJsonReader = new Json5ToJsonReader(jsonReader);

        String convertedJson = CharStreams.toString(json5ToJsonReader);
        assertThat(convertedJson, equalTo("{\"hello\":\"world\"}"));
    }
}