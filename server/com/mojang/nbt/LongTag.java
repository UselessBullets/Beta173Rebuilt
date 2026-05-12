// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class LongTag extends Tag
{
    public long data;
    
    public LongTag() {
    }
    
    public LongTag(final long data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput dos) {
        dos.writeLong(this.data);
    }
    
    @Override
    void load(final DataInput dis) {
        this.data = dis.readLong();
    }
    
    @Override
    public byte getId() {
        return 4;
    }
    
    @Override
    public String toString() {
        return "" + this.data;
    }
}
