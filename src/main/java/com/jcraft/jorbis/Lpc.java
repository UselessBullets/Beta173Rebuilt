// 
// Decompiled by Procyon v0.6.0
// 

package com.jcraft.jorbis;

class Lpc
{
    Drft fft;
    int ln;
    int m;
    
    Lpc() {
        this.fft = new Drft();
    }
    
    static float lpc_from_data(final float[] arr1, final float[] arr2, final int integer3, final int integer4) {
        final float[] array = new float[integer4 + 1];
        int n = integer4 + 1;
        while (n-- != 0) {
            float n2 = 0.0f;
            for (int i = n; i < integer3; ++i) {
                n2 += arr1[i] * arr1[i - n];
            }
            array[n] = n2;
        }
        float n3 = array[0];
        for (int j = 0; j < integer4; ++j) {
            float n4 = -array[j + 1];
            if (n3 == 0.0f) {
                for (int k = 0; k < integer4; ++k) {
                    arr2[k] = 0.0f;
                }
                return 0.0f;
            }
            for (int l = 0; l < j; ++l) {
                n4 -= arr2[l] * array[j - l];
            }
            final float n5 = n4 / n3;
            arr2[j] = n5;
            int n6;
            for (n6 = 0; n6 < j / 2; ++n6) {
                final float n7 = arr2[n6];
                final int n8 = n6;
                arr2[n8] += n5 * arr2[j - 1 - n6];
                final int n9 = j - 1 - n6;
                arr2[n9] += n5 * n7;
            }
            if (j % 2 != 0) {
                final int n10 = n6;
                arr2[n10] += arr2[n6] * n5;
            }
            n3 *= (float)(1.0 - n5 * n5);
        }
        return n3;
    }
    
    float lpc_from_curve(final float[] arr1, final float[] arr2) {
        final int ln = this.ln;
        final float[] array = new float[ln + ln];
        final float n = (float)(0.5 / ln);
        for (int i = 0; i < ln; ++i) {
            array[i * 2] = arr1[i] * n;
            array[i * 2 + 1] = 0.0f;
        }
        array[ln * 2 - 1] = arr1[ln - 1] * n;
        final int integer3 = ln * 2;
        this.fft.backward(array);
        float n3;
        for (int j = 0, n2 = integer3 / 2; j < integer3 / 2; array[j++] = array[n2], array[n2++] = n3) {
            n3 = array[j];
        }
        return lpc_from_data(array, arr2, integer3, this.m);
    }
    
    void init(final int integer1, final int integer2) {
        this.ln = integer1;
        this.m = integer2;
        this.fft.init(integer1 * 2);
    }
    
    void clear() {
        this.fft.clear();
    }
    
    static float FAST_HYPOT(final float float1, final float float2) {
        return (float)Math.sqrt(float1 * float1 + float2 * float2);
    }
    
    void lpc_to_curve(final float[] arr1, final float[] arr2, final float float3) {
        for (int i = 0; i < this.ln * 2; ++i) {
            arr1[i] = 0.0f;
        }
        if (float3 == 0.0f) {
            return;
        }
        for (int j = 0; j < this.m; ++j) {
            arr1[j * 2 + 1] = arr2[j] / (4.0f * float3);
            arr1[j * 2 + 2] = -arr2[j] / (4.0f * float3);
        }
        this.fft.backward(arr1);
        final int n = this.ln * 2;
        final float n2 = (float)(1.0 / float3);
        arr1[0] = (float)(1.0 / (arr1[0] * 2.0f + n2));
        for (int k = 1; k < this.ln; ++k) {
            arr1[k] = (float)(1.0 / FAST_HYPOT(arr1[k] + arr1[n - k] + n2, arr1[k] - arr1[n - k]));
        }
    }
}
