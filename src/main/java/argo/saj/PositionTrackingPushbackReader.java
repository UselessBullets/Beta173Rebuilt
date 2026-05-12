// 
// Decompiled by Procyon v0.6.0
// 

package argo.saj;

import java.io.Reader;
import java.io.PushbackReader;

final class PositionTrackingPushbackReader implements ThingWithPosition
{
    private final PushbackReader pushbackReader;
    private int characterCount;
    private int lineCount;
    private boolean lastCharacterWasCarriageReturn;
    
    public PositionTrackingPushbackReader(final Reader in) {
        this.characterCount = 0;
        this.lineCount = 1;
        this.lastCharacterWasCarriageReturn = false;
        this.pushbackReader = new PushbackReader(in);
    }
    
    public void unread(final char c) {
        --this.characterCount;
        if (this.characterCount < 0) {
            this.characterCount = 0;
        }
        this.pushbackReader.unread(c);
    }
    
    public void uncount(final char[] resultCharArray) {
        this.characterCount -= resultCharArray.length;
        if (this.characterCount < 0) {
            this.characterCount = 0;
        }
    }
    
    public int read() {
        final int read = this.pushbackReader.read();
        this.updateCharacterAndLineCounts(read);
        return read;
    }
    
    public int read(final char[] buffer) {
        final int read = this.pushbackReader.read(buffer);
        for (int length = buffer.length, i = 0; i < length; ++i) {
            this.updateCharacterAndLineCounts(buffer[i]);
        }
        return read;
    }
    
    private void updateCharacterAndLineCounts(final int result) {
        if (13 == result) {
            this.characterCount = 0;
            ++this.lineCount;
            this.lastCharacterWasCarriageReturn = true;
        }
        else {
            if (10 == result && !this.lastCharacterWasCarriageReturn) {
                this.characterCount = 0;
                ++this.lineCount;
            }
            else {
                ++this.characterCount;
            }
            this.lastCharacterWasCarriageReturn = false;
        }
    }
    
    public int getColumn() {
        return this.characterCount;
    }
    
    public int getRow() {
        return this.lineCount;
    }
}
