// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public class StringTag extends Tag
{
    public String data;
    
    public StringTag() {
    }
    
    public StringTag(final String data) {
        this.data = data;
        if (data == null) {
            throw new IllegalArgumentException("Empty string not allowed");
        }
    }
    
    @Override
    void write(final DataOutput dos) {
        dos.writeUTF(this.data);
    }
    
    @Override
    void load(final DataInput dis) {
        this.data = dis.readUTF();
    }
    
    @Override
    public byte getId() {
        return 8;
    }
    
    @Override
    public String toString() {
        return "" + this.data;
    }
}
