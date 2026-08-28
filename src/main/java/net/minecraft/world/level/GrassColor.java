// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public class GrassColor
{
    private static int[] pixels = new int[256 * 256];
    
    public static void init(final int[] pixels) {
        GrassColor.pixels = pixels;
    }
    
    public static int get(final double temp, double rain) {
        rain *= temp;
        int x = (int) ((1.0 - temp) * 255.0);
        int y = (int) ((1.0 - rain) * 255.0);
        int returnVal = GrassColor.pixels[y << 8 | x];

        return returnVal;
    }

}
