// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

abstract class FuncTime
{
    public static FuncTime[] time_P;
    
    abstract void pack(final Object object, final Buffer buffer);
    
    abstract Object unpack(final Info info, final Buffer buffer);
    
    abstract Object look(final DspState dspState, final InfoMode infoMode, final Object object);
    
    abstract void free_info(final Object object);
    
    abstract void free_look(final Object object);
    
    abstract int inverse(final Block block, final Object object, final float[] arr3, final float[] arr4);
    
    static {
        FuncTime.time_P = new FuncTime[] { new Time0() };
    }
}
