// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteTag extends Tag
{
    public byte data;
    
    public ByteTag() {
    }
    
    public ByteTag(final byte data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput dos) throws IOException {
        dos.writeByte(this.data);
    }
    
    @Override
    void load(final DataInput dis) throws IOException {
        this.data = dis.readByte();
    }
    
    @Override
    public byte getId() {
        return TAG_Byte;
    }
    
    @Override
    public String toString() {
        return "" + this.data;
    }
}
