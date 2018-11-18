package javafixes.json5;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Stack;

import static javafixes.common.CollectionUtil.newList;

// todo: test
public class Json5ToJsonReader extends Reader {

    private final Reader json5Reader;

    private List<Character> buffer = newList();
    private boolean finished = false;

    public Json5ToJsonReader(Reader json5Reader) {
        this.json5Reader = json5Reader;
    }

    // todo: implement
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        ensureCharsInBuffer(len);

        int writtenLength = 0;
        for (int i = 0; i < len; i++) {
            if (buffer.size() > 0) {
                cbuf[off + i] = buffer.remove(0);
                writtenLength++;
            } else {
                break;
            }
        }
        return (writtenLength == 0) ? -1 : writtenLength;
    }

    @Override
    public void close() throws IOException {
        json5Reader.close();
    }

    private enum In {
        array,
        //        afterArrayValue,
        object,
//        afterKey,
//        afterKeyValueSeparator,
//        afterObjectValue
    }

    private Stack<In> inStack = new Stack<>();

    private void parseNextChars() throws IOException {
        int nextChar = json5Reader.read();
        if (nextChar == -1) {
            markEndOfStream();
            return;
        }

        char currChar = (char) nextChar;

        if (currChar == '/') {
            handlePossibleComment();
        } else if (isJSON5WhiteSpace(currChar)) {
            // do nothing

//        } else if (currChar == '{') {
//            // todo: validate symbol can occur here
//            inStack.push(In.object);
//            write(currChar);
//        } else if (currChar == '[') {
//            // todo: validate symbol can occur here
//            inStack.push(In.array);
//            write(currChar);
        } else {
            write(currChar);
        }
    }

    private boolean isJSON5WhiteSpace(char c) {
        return c == 0x0009 || c == 0x000A || c == 0x000B || c == 0x000C || c == 0x000D ||
                c == 0x2028 || c == 0x2029 || c == 0xFEFF || isUnicodeSpaceSeparator(c);
    }

    private boolean isUnicodeSpaceSeparator(char c) {
        return c == 0x0020 || c == 0x00A0 || c == 0x1680 || c == 0x2000 || c == 0x2001 ||
                c == 0x2002 || c == 0x2003 || c == 0x2004 || c == 0x2005 || c == 0x2006 ||
                c == 0x2007 || c == 0x2008 || c == 0x2009 || c == 0x200A || c == 0x202F ||
                c == 0x205F || c == 0x3000;
    }

    private void handlePossibleComment() throws IOException {
        int nextChar;
        nextChar = json5Reader.read();
        if (nextChar == -1) {
            throw new IOException("Unexpected String occurred: '/'");
        }
        char currChar = (char) nextChar;
        if (currChar == '/') {
            handleSingleLineComment();
        } else if (currChar == '*') {
            handleMultiLineComment();
        } else {
            throw new IOException("Unexpected String occurred: '/" + currChar + "'");
        }
    }

    private void handleSingleLineComment() throws IOException {
        int nextChar = json5Reader.read();
        if (nextChar == -1) {
            markEndOfStream();
            return;
        }
        char currChar = (char) nextChar;

        boolean finishedComment = false;
        while (!finishedComment) {
            if (currChar == '\n') {
                finishedComment = true;
            } else {
                nextChar = json5Reader.read();
                if (nextChar == -1) {
                    markEndOfStream();
                    return;
                }
                currChar = (char) nextChar;
            }
        }
    }

    private void handleMultiLineComment() throws IOException {
        int nextChar = json5Reader.read();
        if (nextChar == -1) {
            markEndOfStream();
            return;
        }
        char currChar = (char) nextChar;

        boolean finishedComment = false;
        while (!finishedComment) {
            if (currChar == '*') {
                nextChar = json5Reader.read();
                if (nextChar == -1) {
                    markEndOfStream();
                    return;
                }
                currChar = (char) nextChar;
                if (currChar == '/') {
                    finishedComment = true;
                }
            } else {
                nextChar = json5Reader.read();
                if (nextChar == -1) {
                    throw new IOException("Multiline comment never finished");
                }
                currChar = (char) nextChar;
            }
        }
    }


    private void ensureCharsInBuffer(int n) throws IOException {
        while (!finished && buffer.size() < n) {
            parseNextChars();
        }
    }

    private void markEndOfStream() {
        // todo: blow up if the inStack is not empty - with the exception of singleLineComment
        finished = true;
    }

    private void write(char c) {
        buffer.add(c);
    }
}
