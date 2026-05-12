// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

class Residue1 extends Residue0
{
    @Override
    int inverse(final Block block, final Object object, final float[][] arr, final int[] arr, final int integer) {
        int integer2 = 0;
        for (int i = 0; i < integer; ++i) {
            if (arr[i] != 0) {
                arr[integer2++] = arr[i];
            }
        }
        if (integer2 != 0) {
            return Residue0._01inverse(block, object, arr, integer2, 1);
        }
        return 0;
    }
}
