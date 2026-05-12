// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class IntTag extends Tag
{
    public int data;
    
    public IntTag() {
    }
    
    public IntTag(final int data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput dos) {
        dos.writeInt(this.data);
    }
    
    @Override
    void load(final DataInput dis) {
        this.data = dis.readInt();
    }
    
    @Override
    public byte getId() {
        return 3;
    }
    
    @Override
    public String toString() {
        return "" + this.data;
    }
}
