// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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
    void write(final DataOutput dos) throws IOException {
        dos.writeUTF(this.data);
    }
    
    @Override
    void load(final DataInput dis) throws IOException {
        this.data = dis.readUTF();
    }
    
    @Override
    public byte getId() {
        return TAG_String;
    }
    
    @Override
    public String toString() {
        return "" + this.data;
    }
}
