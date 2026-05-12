// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public class GrassColor
{
    private static int[] pixels;
    
    public static void init(final int[] pixels) {
        GrassColor.pixels = pixels;
    }
    
    public static int get(final double temp, double rain) {
        rain *= temp;
        return GrassColor.pixels[(int)((1.0 - rain) * 255.0) << 8 | (int)((1.0 - temp) * 255.0)];
    }
    
    static {
        GrassColor.pixels = new int[65536];
    }
}
