// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleTag extends Tag
{
    public double data;
    
    public DoubleTag() {
    }
    
    public DoubleTag(final double data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput dos) throws IOException {
        dos.writeDouble(this.data);
    }
    
    @Override
    void load(final DataInput dis) throws IOException {
        this.data = dis.readDouble();
    }
    
    @Override
    public byte getId() {
        return TAG_Double;
    }
    
    @Override
    public String toString() {
        return "" + this.data;
    }
}
