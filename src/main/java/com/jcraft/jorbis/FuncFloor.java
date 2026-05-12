// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

abstract class FuncFloor
{
    public static FuncFloor[] floor_P;
    
    abstract void pack(final Object object, final Buffer buffer);
    
    abstract Object unpack(final Info info, final Buffer buffer);
    
    abstract Object look(final DspState dspState, final InfoMode infoMode, final Object object);
    
    abstract void free_info(final Object object);
    
    abstract void free_look(final Object object);
    
    abstract void free_state(final Object object);
    
    abstract int forward(final Block block, final Object object2, final float[] arr3, final float[] arr4, final Object object5);
    
    abstract Object inverse1(final Block block, final Object object2, final Object object3);
    
    abstract int inverse2(final Block block, final Object object2, final Object object3, final float[] arr);
    
    static {
        FuncFloor.floor_P = new FuncFloor[] { new Floor0(), new Floor1() };
    }
}
