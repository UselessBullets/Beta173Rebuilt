/*
 * Copyright 2011 Mark Slater
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package argo.saj;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 * Converts a character stream into calls to a <code>JsonListener</code>.
 * <p/>
 * Instances of <code>SajParser</code> are threadsafe in that concurrent calls to <code>parse</code> are safe, provided
 * each call is made with a different <code>Reader</code> and a different <code>JsonListener</code>.
 *
 * @see JsonListener
 */
public final class SajParser {

    private static final char DOUBLE_QUOTE = '"';
    private static final char BACK_SLASH = '\\';
    private static final char BACKSPACE = '\b';
    private static final char TAB = '\t';
    private static final char NEWLINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';
    private static final char FORM_FEED = '\f';

    public SajParser() {
    }

    /**
     * Parses the given character stream into calls to the given JsonListener.
     *
     * @param in           the character stream to parse
     * @param jsonListener the JsonListener to notify of parsing events
     * @throws IOException            bubbled up from exceptions thrown reading from <code>in</code>
     * @throws InvalidSyntaxException thrown to indicate the characters read from <code>in</code> did not constitute valid JSON.
     */
    public void parse(final Reader in, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
        final PositionTrackingPushbackReader pushbackReader = new PositionTrackingPushbackReader(in);
        final char nextChar = (char) pushbackReader.read();
        switch (nextChar) {
            case '{':
                pushbackReader.unread(nextChar);
                jsonListener.startDocument();
                objectString(pushbackReader, jsonListener);
                break;
            case '[':
                pushbackReader.unread(nextChar);
                jsonListener.startDocument();
                arrayString(pushbackReader, jsonListener);
                break;
            default:
                throw new InvalidSyntaxException("Expected either [ or { but got [" + nextChar + "].", pushbackReader);
        }
        final int trailingCharacter = readNextNonWhitespaceChar(pushbackReader);
        if (trailingCharacter != -1) {
            throw new InvalidSyntaxException("Got unexpected trailing character [" + (char) trailingCharacter + "].", pushbackReader);
        }
        jsonListener.endDocument();
    }

    private void arrayString(final PositionTrackingPushbackReader pushbackReader, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
        final char firstChar = (char) readNextNonWhitespaceChar(pushbackReader);
        if (firstChar != '[') {
            throw new InvalidSyntaxException("Expected object to start with [ but got [" + firstChar + "].", pushbackReader);
        }
        jsonListener.startArray();
        final char secondChar = (char) readNextNonWhitespaceChar(pushbackReader);
        pushbackReader.unread(secondChar);
        if (secondChar != ']') {
            aJsonValue(pushbackReader, jsonListener);
        }
        boolean gotEndOfArray = false;
        while (!gotEndOfArray) {
            final char nextChar = (char) readNextNonWhitespaceChar(pushbackReader);
            switch (nextChar) {
                case ',':
                    aJsonValue(pushbackReader, jsonListener);
                    break;
                case ']':
                    gotEndOfArray = true;
                    break;
                default:
                    throw new InvalidSyntaxException("Expected either , or ] but got [" + nextChar + "].", pushbackReader);
            }
        }
        jsonListener.endArray();
    }

    private void objectString(final PositionTrackingPushbackReader pushbackReader, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
        final char firstChar = (char) readNextNonWhitespaceChar(pushbackReader);
        if (firstChar != '{') {
            throw new InvalidSyntaxException("Expected object to start with { but got [" + firstChar + "].", pushbackReader);
        }
        jsonListener.startObject();
        final char secondChar = (char) readNextNonWhitespaceChar(pushbackReader);
        pushbackReader.unread(secondChar);
        if (secondChar != '}') {
            aFieldToken(pushbackReader, jsonListener);
        }
        boolean gotEndOfObject = false;
        while (!gotEndOfObject) {
            final char nextChar = (char) readNextNonWhitespaceChar(pushbackReader);
            switch (nextChar) {
                case ',':
                    aFieldToken(pushbackReader, jsonListener);
                    break;
                case '}':
                    gotEndOfObject = true;
                    break;
                default:
                    throw new InvalidSyntaxException("Expected either , or } but got [" + nextChar + "].", pushbackReader);
            }
        }
        jsonListener.endObject();
    }

    private void aFieldToken(final PositionTrackingPushbackReader pushbackReader, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
        final char nextChar = (char) readNextNonWhitespaceChar(pushbackReader);
        if (DOUBLE_QUOTE != nextChar) {
            throw new InvalidSyntaxException("Expected object identifier to begin with [\"] but got [" + nextChar + "].", pushbackReader);
        }
        pushbackReader.unread(nextChar);
        jsonListener.startField(stringToken(pushbackReader));
        final char separatorChar = (char) readNextNonWhitespaceChar(pushbackReader);
        if (separatorChar != ':') {
            throw new InvalidSyntaxException("Expected object identifier to be followed by : but got [" + separatorChar + "].", pushbackReader);
        }
        aJsonValue(pushbackReader, jsonListener);
        jsonListener.endField();
    }

    private void aJsonValue(final PositionTrackingPushbackReader pushbackReader, final JsonListener jsonListener) throws IOException, InvalidSyntaxException {
        final char nextChar = (char) readNextNonWhitespaceChar(pushbackReader);
        switch (nextChar) {
            case '"':
                pushbackReader.unread(nextChar);
                jsonListener.stringValue(stringToken(pushbackReader));
                break;
            case 't':
                final char[] remainingTrueTokenCharacters = new char[3];
                final int trueTokenCharactersRead = pushbackReader.read(remainingTrueTokenCharacters);
                if (trueTokenCharactersRead != 3 || remainingTrueTokenCharacters[0] != 'r' || remainingTrueTokenCharacters[1] != 'u' || remainingTrueTokenCharacters[2] != 'e') {
                    pushbackReader.uncount(remainingTrueTokenCharacters);
                    throw new InvalidSyntaxException("Expected 't' to be followed by [[r, u, e]], but got [" + Arrays.toString(remainingTrueTokenCharacters) + "].", pushbackReader);
                } else {
                    jsonListener.trueValue();
                }
                break;
            case 'f':
                final char[] remainingFalseTokenCharacters = new char[4];
                final int falseTokenCharactersRead = pushbackReader.read(remainingFalseTokenCharacters);
                if (falseTokenCharactersRead != 4 || remainingFalseTokenCharacters[0] != 'a' || remainingFalseTokenCharacters[1] != 'l' || remainingFalseTokenCharacters[2] != 's' || remainingFalseTokenCharacters[3] != 'e') {
                    pushbackReader.uncount(remainingFalseTokenCharacters);
                    throw new InvalidSyntaxException("Expected 'f' to be followed by [[a, l, s, e]], but got [" + Arrays.toString(remainingFalseTokenCharacters) + "].", pushbackReader);
                } else {
                    jsonListener.falseValue();
                }
                break;
            case 'n':
                final char[] remainingNullTokenCharacters = new char[3];
                final int nullTokenCharactersRead = pushbackReader.read(remainingNullTokenCharacters);
                if (nullTokenCharactersRead != 3 || remainingNullTokenCharacters[0] != 'u' || remainingNullTokenCharacters[1] != 'l' || remainingNullTokenCharacters[2] != 'l') {
                    pushbackReader.uncount(remainingNullTokenCharacters);
                    throw new InvalidSyntaxException("Expected 'n' to be followed by [[u, l, l]], but got [" + Arrays.toString(remainingNullTokenCharacters) + "].", pushbackReader);
                } else {
                    jsonListener.nullValue();
                }
                break;
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                pushbackReader.unread(nextChar);
                jsonListener.numberValue(numberToken(pushbackReader));
                break;
            case '{':
                pushbackReader.unread(nextChar);
                objectString(pushbackReader, jsonListener);
                break;
            case '[':
                pushbackReader.unread(nextChar);
                arrayString(pushbackReader, jsonListener);
                break;
            default:
                throw new InvalidSyntaxException("Invalid character at start of value [" + nextChar + "].", pushbackReader);
        }
    }

    private String numberToken(final PositionTrackingPushbackReader in) throws IOException, InvalidSyntaxException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) in.read();
        if ('-' == firstChar) {
            result.append('-');
        } else {
            in.unread(firstChar);
        }
        result.append(nonNegativeNumberToken(in));
        return result.toString();
    }

    private String nonNegativeNumberToken(final PositionTrackingPushbackReader in) throws IOException, InvalidSyntaxException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) in.read();
        if ('0' == firstChar) {
            result.append('0');
            result.append(possibleFractionalComponent(in));
            result.append(possibleExponent(in));
        } else {
            in.unread(firstChar);
            result.append(nonZeroDigitToken(in));
            result.append(digitString(in));
            result.append(possibleFractionalComponent(in));
            result.append(possibleExponent(in));
        }
        return result.toString();
    }

    private char nonZeroDigitToken(final PositionTrackingPushbackReader in) throws IOException, InvalidSyntaxException {
        final char result;
        final char nextChar = (char) in.read();
        switch (nextChar) {
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                result = nextChar;
                break;
            default:
                throw new InvalidSyntaxException("Expected a digit 1 - 9 but got [" + nextChar + "].", in);
        }
        return result;
    }

    private char digitToken(final PositionTrackingPushbackReader in) throws IOException, InvalidSyntaxException {
        final char result;
        final char nextChar = (char) in.read();
        switch (nextChar) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                result = nextChar;
                break;
            default:
                throw new InvalidSyntaxException("Expected a digit 1 - 9 but got [" + nextChar + "].", in);
        }
        return result;
    }

    private String digitString(final PositionTrackingPushbackReader in) throws IOException {
        final StringBuilder result = new StringBuilder();
        boolean gotANonDigit = false;
        while (!gotANonDigit) {
            final char nextChar = (char) in.read();
            switch (nextChar) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    result.append(nextChar);
                    break;
                default:
                    gotANonDigit = true;
                    in.unread(nextChar);
            }
        }
        return result.toString();
    }

    private String possibleFractionalComponent(final PositionTrackingPushbackReader pushbackReader) throws IOException, InvalidSyntaxException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) pushbackReader.read();
        if (firstChar == '.') {
            result.append('.');
            result.append(digitToken(pushbackReader));
            result.append(digitString(pushbackReader));
        } else {
            pushbackReader.unread(firstChar);
        }
        return result.toString();
    }

    private String possibleExponent(final PositionTrackingPushbackReader pushbackReader) throws IOException, InvalidSyntaxException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) pushbackReader.read();
        switch (firstChar) {
            case '.':
            case 'E':
                result.append('E');
                result.append(possibleSign(pushbackReader));
                result.append(digitToken(pushbackReader));
                result.append(digitString(pushbackReader));
                break;
            case 'e':
                result.append('e');
                result.append(possibleSign(pushbackReader));
                result.append(digitToken(pushbackReader));
                result.append(digitString(pushbackReader));
                break;
            default:
                pushbackReader.unread(firstChar);
                break;
        }
        return result.toString();
    }

    private String possibleSign(final PositionTrackingPushbackReader pushbackReader) throws IOException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) pushbackReader.read();
        if (firstChar == '+' || firstChar == '-') {
            result.append(firstChar);
        } else {
            pushbackReader.unread(firstChar);
        }
        return result.toString();
    }


    private String stringToken(final PositionTrackingPushbackReader in) throws IOException, InvalidSyntaxException {
        final StringBuilder result = new StringBuilder();
        final char firstChar = (char) in.read();
        if (DOUBLE_QUOTE != firstChar) {
            throw new InvalidSyntaxException("Expected [" + DOUBLE_QUOTE + "] but got [" + firstChar + "].", in);
        }
        final ThingWithPosition openDoubleQuotesPosition = in.snapshotOfPosition();
        boolean stringClosed = false;
        while (!stringClosed) {
            final char nextChar = (char) in.read();
            switch (nextChar) {
                case (char) -1:
                    throw new InvalidSyntaxException("Got opening [" + DOUBLE_QUOTE + "] without matching closing [" + DOUBLE_QUOTE + "]", openDoubleQuotesPosition);
                case DOUBLE_QUOTE:
                    stringClosed = true;
                    break;
                case BACK_SLASH:
                    final char escapedChar = escapedStringChar(in);
                    result.append(escapedChar);
                    break;
                default:
                    result.append(nextChar);
            }
        }
        return result.toString();
    }

    private char escapedStringChar(final PositionTrackingPushbackReader in) throws IOException, InvalidSyntaxException {
        final char result;
        final char firstChar = (char) in.read();
        switch (firstChar) {
            case DOUBLE_QUOTE:
                result = DOUBLE_QUOTE;
                break;
            case BACK_SLASH:
                result = BACK_SLASH;
                break;
            case '/':
                result = '/';
                break;
            case 'b':
                result = BACKSPACE;
                break;
            case 'f':
                result = FORM_FEED;
                break;
            case 'n':
                result = NEWLINE;
                break;
            case 'r':
                result = CARRIAGE_RETURN;
                break;
            case 't':
                result = TAB;
                break;
            case 'u':
                result = (char) hexadecimalNumber(in);
                break;
            default:
                throw new InvalidSyntaxException("Unrecognised escape character [" + firstChar + "].", in);
        }
        return result;
    }

    private int hexadecimalNumber(final PositionTrackingPushbackReader in) throws IOException, InvalidSyntaxException {
        final char[] resultCharArray = new char[4];
        final int readSize = in.read(resultCharArray);
        if (readSize != 4) {
            throw new InvalidSyntaxException("Expected a 4 digit hexadecimal number but got only [" + readSize + "], namely [" + String.valueOf(resultCharArray, 0, readSize) + "].", in);
        }
        int result;
        try {
            result = Integer.parseInt(String.valueOf(resultCharArray), 16);
        } catch (final NumberFormatException e) {
            in.uncount(resultCharArray);
            throw new InvalidSyntaxException("Unable to parse [" + String.valueOf(resultCharArray) + "] as a hexadecimal number.", e, in);
        }
        return result;
    }

    private int readNextNonWhitespaceChar(final PositionTrackingPushbackReader in) throws IOException {
        int nextChar;
        boolean gotNonWhitespace = false;
        do {
            nextChar = in.read();
            switch (nextChar) {
                case ' ':
                case TAB:
                case NEWLINE:
                case CARRIAGE_RETURN:
                    break;
                default:
                    gotNonWhitespace = true;
            }
        } while (!gotNonWhitespace);
        return nextChar;
    }

}
