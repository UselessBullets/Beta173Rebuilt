// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends Tag
{
    public short data;
    
    public ShortTag() {
    }
    
    public ShortTag(final short data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput dos) throws IOException {
        dos.writeShort(this.data);
    }
    
    @Override
    void load(final DataInput dis) throws IOException {
        this.data = dis.readShort();
    }
    
    @Override
    public byte getId() {
        return TAG_Short;
    }
    
    @Override
    public String toString() {
        return "" + this.data;
    }
}
