// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntTag extends Tag
{
    public int data;
    
    public IntTag() {
    }
    
    public IntTag(final int data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput dos) throws IOException {
        dos.writeInt(this.data);
    }
    
    @Override
    void load(final DataInput dis) throws IOException {
        this.data = dis.readInt();
    }
    
    @Override
    public byte getId() {
        return TAG_Int;
    }
    
    @Override
    public String toString() {
        return "" + this.data;
    }
}
