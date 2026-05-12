// 
// Decompiled by Procyon v0.6.0
// 

package util;

public class Mth
{
    private static float[] sin;
    
    public static final float sin(final float i) {
        return Mth.sin[(int)(i * 10430.378f) & 0xFFFF];
    }
    
    public static final float cos(final float i) {
        return Mth.sin[(int)(i * 10430.378f + 16384.0f) & 0xFFFF];
    }
    
    public static final float sqrt(final float x) {
        return (float)Math.sqrt(x);
    }
    
    public static final float sqrt(final double x) {
        return (float)Math.sqrt(x);
    }
    
    public static int floor(final float v) {
        final int n = (int)v;
        return (v < n) ? (n - 1) : n;
    }
    
    public static int floor(final double v) {
        final int n = (int)v;
        return (v < n) ? (n - 1) : n;
    }
    
    public static float abs(final float v) {
        return (v >= 0.0f) ? v : (-v);
    }
    
    public static double asbMax(double a, double b) {
        if (a < 0.0) {
            a = -a;
        }
        if (b < 0.0) {
            b = -b;
        }
        return (a > b) ? a : b;
    }
    
    static {
        Mth.sin = new float[65536];
        for (int i = 0; i < 65536; ++i) {
            Mth.sin[i] = (float)Math.sin(i * 3.141592653589793 * 2.0 / 65536.0);
        }
    }
}
