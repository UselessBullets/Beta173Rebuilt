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
import java.io.PushbackReader;
import java.io.Reader;

final class PositionTrackingPushbackReader implements ThingWithPosition {
    private static final int NEWLINE = '\n';
    private static final int CARRIAGE_RETURN = '\r';

    private final PushbackReader pushbackReader;
    private int characterCount = 0;
    private int lineCount = 1;
    private boolean lastCharacterWasCarriageReturn = false;

    public PositionTrackingPushbackReader(final Reader in) {
        this.pushbackReader = new PushbackReader(in);
    }

    public void unread(final char c) throws IOException {
        characterCount--;
        if (characterCount < 0) characterCount = 0;
        pushbackReader.unread(c);
    }

    public void uncount(final char[] resultCharArray) throws IOException {
        characterCount = characterCount - resultCharArray.length;
        if (characterCount < 0) characterCount = 0;
    }

    public int read() throws IOException {
        final int result = pushbackReader.read();
        updateCharacterAndLineCounts(result);
        return result;
    }

    public int read(final char[] buffer) throws IOException {
        final int result = pushbackReader.read(buffer);
        for (char character : buffer) {
            updateCharacterAndLineCounts(character);
        }
        return result;
    }

    private void updateCharacterAndLineCounts(final int result) {
        if (CARRIAGE_RETURN == result) {
            characterCount = 0;
            lineCount++;
            lastCharacterWasCarriageReturn = true;
        } else {
            if (NEWLINE == result && !lastCharacterWasCarriageReturn) {
                characterCount = 0;
                lineCount++;
            } else {
                characterCount++;
            }
            lastCharacterWasCarriageReturn = false;
        }
    }

    public int getColumn() {
        return characterCount;
    }

    public int getRow() {
        return lineCount;
    }

    public ThingWithPosition snapshotOfPosition() {
        return new ThingWithPosition() {
            private final int localCharacterCount = characterCount;
            private final int localLineCount = lineCount;

            public int getColumn() {
                return localCharacterCount;
            }

            public int getRow() {
                return localLineCount;
            }
        };
    }
}