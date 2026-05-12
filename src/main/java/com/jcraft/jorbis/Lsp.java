// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

class Lsp
{
    static final float M_PI = 3.1415927f;
    
    static void lsp_to_curve(final float[] arr1, final int[] arr, final int integer3, final int integer4, final float[] arr5, final int integer6, final float float7, final float float8) {
        final float n = 3.1415927f / integer4;
        for (int i = 0; i < integer6; ++i) {
            arr5[i] = Lookup.coslook(arr5[i]);
        }
        final int n2 = integer6 / 2 * 2;
        int j = 0;
        while (j < integer3) {
            final int n3 = arr[j];
            float n4 = 0.70710677f;
            float n5 = 0.70710677f;
            final float coslook = Lookup.coslook(n * n3);
            for (int k = 0; k < n2; k += 2) {
                n5 *= arr5[k] - coslook;
                n4 *= arr5[k + 1] - coslook;
            }
            float n7;
            float n8;
            if ((integer6 & 0x1) != 0x0) {
                final float n6 = n5 * (arr5[integer6 - 1] - coslook);
                n7 = n6 * n6;
                n8 = n4 * (n4 * (1.0f - coslook * coslook));
            }
            else {
                n7 = n5 * (n5 * (1.0f + coslook));
                n8 = n4 * (n4 * (1.0f - coslook));
            }
            float intBitsToFloat = n8 + n7;
            int n9 = Float.floatToIntBits(intBitsToFloat);
            int n10 = Integer.MAX_VALUE & n9;
            int n11 = 0;
            if (n10 < 2139095040) {
                if (n10 != 0) {
                    if (n10 < 8388608) {
                        n9 = Float.floatToIntBits((float)(intBitsToFloat * 3.3554432E7));
                        n10 = (Integer.MAX_VALUE & n9);
                        n11 = -25;
                    }
                    n11 += (n10 >>> 23) - 126;
                    intBitsToFloat = Float.intBitsToFloat((n9 & 0x807FFFFF) | 0x3F000000);
                }
            }
            final float fromdBlook = Lookup.fromdBlook(float7 * Lookup.invsqlook(intBitsToFloat) * Lookup.invsq2explook(n11 + integer6) - float8);
            do {
                final int n12 = j++;
                arr1[n12] *= fromdBlook;
            } while (j < integer3 && arr[j] == n3);
        }
    }
}
