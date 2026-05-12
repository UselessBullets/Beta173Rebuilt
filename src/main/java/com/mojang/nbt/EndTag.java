// 
// Decompiled by Procyon v0.6.0
// 

package com.mojang.nbt;

import java.io.DataOutput;
import java.io.DataInput;

public class EndTag extends Tag
{
    @Override
    void load(final DataInput dis) {
    }
    
    @Override
    void write(final DataOutput dos) {
    }
    
    @Override
    public byte getId() {
        return 0;
    }
    
    @Override
    public String toString() {
        return "END";
    }
}
