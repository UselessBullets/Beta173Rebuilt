// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;

abstract class FuncResidue
{
    public static FuncResidue[] residue_P;
    
    abstract void pack(final Object object, final Buffer buffer);
    
    abstract Object unpack(final Info info, final Buffer buffer);
    
    abstract Object look(final DspState dspState, final InfoMode infoMode, final Object object);
    
    abstract void free_info(final Object object);
    
    abstract void free_look(final Object object);
    
    abstract int inverse(final Block block, final Object object, final float[][] arr, final int[] arr, final int integer);
    
    static {
        FuncResidue.residue_P = new FuncResidue[] { new Residue0(), new Residue1(), new Residue2() };
    }
}
