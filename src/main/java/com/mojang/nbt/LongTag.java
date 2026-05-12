// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends Tag
{
    public long data;
    
    public LongTag() {
    }
    
    public LongTag(final long data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput dos) throws IOException {
        dos.writeLong(this.data);
    }
    
    @Override
    void load(final DataInput dis) throws IOException {
        this.data = dis.readLong();
    }
    
    @Override
    public byte getId() {
        return TAG_Long;
    }
    
    @Override
    public String toString() {
        return "" + this.data;
    }
}
