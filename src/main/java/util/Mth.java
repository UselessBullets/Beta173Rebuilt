// 
// Decompiled by Procyon v0.6.0
// 

package util;

public class Mth
{
    public static final float PI = (float) Math.PI; // Useless - Added for the floating point precision PIs in the codebase, afaik one of these *should've* existed but I couldn't find anything other than an LCE def in a non Java represented header file
    public static final float DEGRAD = (float) (Math.PI / 180.0);
    public static final float RADDEG = (float) (180.0 / Math.PI);
    private static float[] sin;
    private static final float sinScale = (float) (0xFFFF / (Math.PI * 2));
    
    public static final float sin(final float i) {
        return Mth.sin[(int)(i * sinScale) & 0xFFFF];
    }
    
    public static final float cos(final float i) {
        return Mth.sin[(int)(i * sinScale + 65536.0f / 4f) & 0xFFFF];
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
    
    public static int intFloorDiv(final int a, final int b) {
        if (a < 0) {
            return -((-a - 1) / b) - 1;
        }
        return a / b;
    }
    
    public static boolean isNullOrEmpty(final String str) {
        return str == null || str.length() == 0;
    }
    
    static {
        Mth.sin = new float[65536];
        for (int i = 0; i < 65536; ++i) {
            Mth.sin[i] = (float)Math.sin(i * Math.PI * 2.0 / 65536.0);
        }
    }
}
