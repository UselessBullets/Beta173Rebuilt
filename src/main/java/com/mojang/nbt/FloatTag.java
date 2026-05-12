// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatTag extends Tag
{
    public float data;
    
    public FloatTag() {
    }
    
    public FloatTag(final float data) {
        this.data = data;
    }
    
    @Override
    void write(final DataOutput dos) throws IOException {
        dos.writeFloat(this.data);
    }
    
    @Override
    void load(final DataInput dis) throws IOException {
        this.data = dis.readFloat();
    }
    
    @Override
    public byte getId() {
        return TAG_Float;
    }
    
    @Override
    public String toString() {
        return "" + this.data;
    }
}
