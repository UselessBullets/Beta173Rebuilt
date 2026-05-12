// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

abstract class FuncMapping
{
    public static FuncMapping[] mapping_P;
    
    abstract void pack(final Info info, final Object object, final Buffer buffer);
    
    abstract Object unpack(final Info info, final Buffer buffer);
    
    abstract Object look(final DspState dspState, final InfoMode infoMode, final Object object);
    
    abstract void free_info(final Object object);
    
    abstract void free_look(final Object object);
    
    abstract int inverse(final Block block, final Object object);
    
    static {
        FuncMapping.mapping_P = new FuncMapping[] { new Mapping0() };
    }
}
