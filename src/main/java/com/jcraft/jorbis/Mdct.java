// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

class Mdct
{
    int n;
    int log2n;
    float[] trig;
    int[] bitrev;
    float scale;
    float[] _x;
    float[] _w;
    
    Mdct() {
        this._x = new float[1024];
        this._w = new float[1024];
    }
    
    void init(final int integer) {
        this.bitrev = new int[integer / 4];
        this.trig = new float[integer + integer / 4];
        this.log2n = (int)Math.rint(Math.log(integer) / Math.log(2.0));
        this.n = integer;
        final int n = 0;
        final int n2 = 1;
        final int n3 = n + integer / 2;
        final int n4 = n3 + 1;
        final int n5 = n3 + integer / 2;
        final int n6 = n5 + 1;
        for (int i = 0; i < integer / 4; ++i) {
            this.trig[n + i * 2] = (float)Math.cos(3.141592653589793 / integer * (4 * i));
            this.trig[n2 + i * 2] = (float)(-Math.sin(3.141592653589793 / integer * (4 * i)));
            this.trig[n3 + i * 2] = (float)Math.cos(3.141592653589793 / (2 * integer) * (2 * i + 1));
            this.trig[n4 + i * 2] = (float)Math.sin(3.141592653589793 / (2 * integer) * (2 * i + 1));
        }
        for (int j = 0; j < integer / 8; ++j) {
            this.trig[n5 + j * 2] = (float)Math.cos(3.141592653589793 / integer * (4 * j + 2));
            this.trig[n6 + j * 2] = (float)(-Math.sin(3.141592653589793 / integer * (4 * j + 2)));
        }
        final int n7 = (1 << this.log2n - 1) - 1;
        final int n8 = 1 << this.log2n - 2;
        for (int k = 0; k < integer / 8; ++k) {
            int n9 = 0;
            for (int n10 = 0; n8 >>> n10 != 0; ++n10) {
                if ((n8 >>> n10 & k) != 0x0) {
                    n9 |= 1 << n10;
                }
            }
            this.bitrev[k * 2] = (~n9 & n7);
            this.bitrev[k * 2 + 1] = n9;
        }
        this.scale = 4.0f / integer;
    }
    
    void clear() {
    }
    
    void forward(final float[] arr1, final float[] arr2) {
    }
    
    synchronized void backward(final float[] arr1, final float[] arr2) {
        if (this._x.length < this.n / 2) {
            this._x = new float[this.n / 2];
        }
        if (this._w.length < this.n / 2) {
            this._w = new float[this.n / 2];
        }
        final float[] x = this._x;
        final float[] w = this._w;
        final int integer4 = this.n >>> 1;
        final int integer5 = this.n >>> 2;
        final int integer6 = this.n >>> 3;
        int n = 1;
        int n2 = 0;
        int n3 = integer4;
        for (int i = 0; i < integer6; ++i) {
            n3 -= 2;
            x[n2++] = -arr1[n + 2] * this.trig[n3 + 1] - arr1[n] * this.trig[n3];
            x[n2++] = arr1[n] * this.trig[n3 + 1] - arr1[n + 2] * this.trig[n3];
            n += 4;
        }
        int n4 = integer4 - 4;
        for (int j = 0; j < integer6; ++j) {
            n3 -= 2;
            x[n2++] = arr1[n4] * this.trig[n3 + 1] + arr1[n4 + 2] * this.trig[n3];
            x[n2++] = arr1[n4] * this.trig[n3] - arr1[n4 + 2] * this.trig[n3 + 1];
            n4 -= 4;
        }
        final float[] mdct_kernel = this.mdct_kernel(x, w, this.n, integer4, integer5, integer6);
        int n5 = 0;
        int n6 = integer4;
        int n7 = integer5;
        int n8 = n7 - 1;
        int n9 = integer5 + integer4;
        int n10 = n9 - 1;
        for (int k = 0; k < integer5; ++k) {
            final float n11 = mdct_kernel[n5] * this.trig[n6 + 1] - mdct_kernel[n5 + 1] * this.trig[n6];
            final float n12 = -(mdct_kernel[n5] * this.trig[n6] + mdct_kernel[n5 + 1] * this.trig[n6 + 1]);
            arr2[n7] = -n11;
            arr2[n8] = n11;
            arr2[n10] = (arr2[n9] = n12);
            ++n7;
            --n8;
            ++n9;
            --n10;
            n5 += 2;
            n6 += 2;
        }
    }
    
    private float[] mdct_kernel(float[] arr1, float[] arr2, final int integer3, final int integer4, final int integer5, final int integer6) {
        float n4;
        float n5;
        for (int n = integer5, n2 = 0, n3 = integer4, i = 0; i < integer5; arr2[i++] = n4 * this.trig[n3] + n5 * this.trig[n3 + 1], arr2[i] = n5 * this.trig[n3] - n4 * this.trig[n3 + 1], arr2[integer5 + i] = arr1[n++] + arr1[n2++], ++i) {
            n4 = arr1[n] - arr1[n2];
            arr2[integer5 + i] = arr1[n++] + arr1[n2++];
            n5 = arr1[n] - arr1[n2];
            n3 -= 4;
        }
        for (int j = 0; j < this.log2n - 3; ++j) {
            int n6 = integer3 >>> j + 2;
            final int n7 = 1 << j + 3;
            int n8 = integer4 - 2;
            for (int n9 = 0, k = 0; k < n6 >>> 2; --n6, n9 += n7, ++k) {
                int n10 = n8;
                int n11 = n10 - (n6 >> 1);
                final float n12 = this.trig[n9];
                final float n13 = this.trig[n9 + 1];
                n8 -= 2;
                ++n6;
                for (int l = 0; l < 2 << j; ++l) {
                    final float n14 = arr2[n10] - arr2[n11];
                    arr1[n10] = arr2[n10] + arr2[n11];
                    final float n15 = arr2[++n10] - arr2[++n11];
                    arr1[n10] = arr2[n10] + arr2[n11];
                    arr1[n11] = n15 * n12 - n14 * n13;
                    arr1[n11 - 1] = n14 * n12 + n15 * n13;
                    n10 -= n6;
                    n11 -= n6;
                }
            }
            final float[] array = arr2;
            arr2 = arr1;
            arr1 = array;
        }
        int n16 = integer3;
        int n17 = 0;
        int n18 = 0;
        int n19 = integer4 - 1;
        for (int n20 = 0; n20 < integer6; ++n20) {
            final int n21 = this.bitrev[n17++];
            final int n22 = this.bitrev[n17++];
            final float n23 = arr2[n21] - arr2[n22 + 1];
            final float n24 = arr2[n21 - 1] + arr2[n22];
            final float n25 = arr2[n21] + arr2[n22 + 1];
            final float n26 = arr2[n21 - 1] - arr2[n22];
            final float n27 = n23 * this.trig[n16];
            final float n28 = n24 * this.trig[n16++];
            final float n29 = n23 * this.trig[n16];
            final float n30 = n24 * this.trig[n16++];
            arr1[n18++] = (n25 + n29 + n28) * 0.5f;
            arr1[n19--] = (-n26 + n30 - n27) * 0.5f;
            arr1[n18++] = (n26 + n30 - n27) * 0.5f;
            arr1[n19--] = (n25 - n29 - n28) * 0.5f;
        }
        return arr1;
    }
}
