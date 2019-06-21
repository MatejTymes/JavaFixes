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
    public void shouldBeAbleToParseValidJson() throws IOException {
        StringReader jsonReader = new StringReader("{\n" +
                "  \"stringField\" : \"this \\\" is \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a string \",\n" +
                "  \"intField1\" : 0,\n" +
                "  \"intField2\" : 1.953e+5,\n" +
                "  \"objectField\" : {\n" +
                "    \"booleanField\" : true,\n" +
                "    \"booleanField2\" : false,\n" +
                "    \"nullField\" : null,\n" +
                "    \"arrayField\" : [\n" +
                "      \"this \\\" is \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a string \",\n" +
                "      { \"field\" : \"value\" },\n" +
                "      [ 0, 1.953e+5, \"string\", false, null, true ],\n" +
                "      -59,\n" +
                "      489E-31,\n" +
                "      false,\n" +
                "      null,\n" +
                "      true\n" +
                "    ]\n" +
                "  },\n" +
                "  \"intField3\" : -59,\n" +
                "  \"intField4\" : 489E-31,\n" +
                "  \"intField5\" : -0E1,\n" +
                "  \"nullField\" : null\n" +
                "}");

        Reader json5ToJsonReader = new Json5ToJsonReader(jsonReader);

        String convertedJson = CharStreams.toString(json5ToJsonReader);
        assertThat(convertedJson, equalTo("{\"stringField\":\"this \\\" is \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a string \",\"intField1\":0,\"intField2\":1.953e+5,\"objectField\":{\"booleanField\":true,\"booleanField2\":false,\"nullField\":null,\"arrayField\":[\"this \\\" is \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a string \",{\"field\":\"value\"},[0,1.953e+5,\"string\",false,null,true],-59,489E-31,false,null,true]},\"intField3\":-59,\"intField4\":489E-31,\"intField5\":-0E1,\"nullField\":null}"));
    }

    @Test
    public void shouldBeAbleToHandleJson5Features() throws IOException {
        StringReader jsonReader = new StringReader("\n" +
                "// single line comment\n" +
                "/* multi\n" +
                "\n" +
                "// line\n" +
                "comment */\n" +
                "\n" +
                "{ // comment\n" +
                "  \"stringField\":\"this \\\"is\\\" \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a 'string' \",\n" +
                "  'singleQuotedStringField'\n" +
                "  : 'this \"is\" \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a \\'string\\' ',\n" +
                "\n" +
                "  \"intField1\" : 0, /*\n" +
                "\n" +
                "    yet more multiline\n" +
                "    comments\n" +
                "\n" +
                "  */\n" +
                "\n" +
                "  \"intField2\" : 1.953e+5,\n" +
                "//  \"intFieldX\" : bla,\n" +
                "/*  \"intFieldY\" : bla,\n" +
                "    commented out\n" +
                "*/\n" +
                "  \"leadingDecimalPoint\": .8675309,\n" +
                "  \"trailingDecimalPoint\": 8675309.,\n" +
                "  \"positiveSign\": +1,\n" +
                "\n" +
                "  \"objectField\" : {\n" +
                "    \"booleanField\" : true,\n" +
                "    \"booleanField2\" : false,\n" +
                "    \"nullField\" : null,\n" +
                "    \"arrayField\" : [\n" +
                "      \"this \\\"is\\\" \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a 'string' \",\n" +
                "      .8675309,\n" +
                "      { \"field\" : \"value\" },\n" +
                "      8675309.,\n" +
                "      +1,\n" +
                "      'this \"is\" \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a \\'string\\' ',\n" +
                "      [ 0, 1.953e+5, \"string\", false, null, true ],\n" +
                "      -59,\n" +
                "      489E-31,\n" +
                "      false,\n" +
                "      null,\n" +
                "      true\n" +
                "      , // trailing comma in array\n" +
                "    ], // another trailing comma\n" +
                "  },\n" +
                "  \"intField3\" : -59,\n" +
                "  \"intField4\" : 489E-31,\n" +
                "  \"intField5\" : -0E1,\n" +
                "  \"nullField\" : null\n" +
                "  , // trailing comma\n" +
                "}");

        Reader json5ToJsonReader = new Json5ToJsonReader(jsonReader);

        String convertedJson = CharStreams.toString(json5ToJsonReader);
        assertThat(convertedJson, equalTo("{\"stringField\":\"this \\\"is\\\" \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a 'string' \",\"singleQuotedStringField\":\"this \\\"is\\\" \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a 'string' \",\"intField1\":0,\"intField2\":1.953e+5,\"leadingDecimalPoint\":0.8675309,\"trailingDecimalPoint\":8675309,\"positiveSign\":1,\"objectField\":{\"booleanField\":true,\"booleanField2\":false,\"nullField\":null,\"arrayField\":[\"this \\\"is\\\" \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a 'string' \",0.8675309,{\"field\":\"value\"},8675309,1,\"this \\\"is\\\" \\\\ \\/ \\b \\f \\n \\r \\t \\u9210 a 'string' \",[0,1.953e+5,\"string\",false,null,true],-59,489E-31,false,null,true]},\"intField3\":-59,\"intField4\":489E-31,\"intField5\":-0E1,\"nullField\":null}"));
    }
}