// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

class Residue2 extends Residue0
{
    @Override
    int inverse(final Block block, final Object object, final float[][] arr, final int[] arr, final int integer) {
        int n;
        for (n = 0; n < integer && arr[n] == 0; ++n) {}
        if (n == integer) {
            return 0;
        }
        return Residue0._2inverse(block, object, arr, integer);
    }
}
