// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteArrayTag extends Tag
{
    public byte[] data;
    
    public ByteArrayTag() {
    }
    
    public ByteArrayTag(final byte[] data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput dos) throws IOException {
        dos.writeInt(this.data.length);
        dos.write(this.data);
    }
    
    @Override
    void load(final DataInput dis) throws IOException {
        dis.readFully(this.data = new byte[dis.readInt()]);
    }
    
    @Override
    public byte getId() {
        return TAG_Byte_Array;
    }
    
    @Override
    public String toString() {
        return "[" + this.data.length + " bytes]";
    }
}
