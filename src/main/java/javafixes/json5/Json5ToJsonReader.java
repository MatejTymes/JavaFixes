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
            handlePossibleComment(currChar);
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

    private void handlePossibleComment(char currChar) throws IOException {
        int nextChar;
        nextChar = json5Reader.read();
        if (nextChar == -1) {
            throw new IOException("Unexpected String occurred: '" + currChar + "'");
        }
        char prevChar = currChar;
        currChar = (char) nextChar;
        if (currChar == '/') {
            handleSingleLineComment();
        } else if (currChar == '*') {
            handleMultiLineComment();
        } else {
            throw new IOException("Unexpected String occurred: '" + prevChar + currChar + "'");
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
                write(currChar);
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
                    throw new IOException("Multiline comment never closed");
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
