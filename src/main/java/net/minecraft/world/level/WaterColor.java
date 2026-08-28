// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public class WaterColor
{
    private static int[] pixels = new int[256 * 256];
    
    public static void init(final int[] pixels) {
        WaterColor.pixels = pixels;
    }

    // Useless - Commented out in LCE leak, almost certainly was just here but used given the existance of the class and colormap texture
    public static int get(double temp, double rain) {
        rain *= temp;
        int x = (int) ((1 - temp) * 255);
        int y = (int) ((1 - rain) * 255);
        int returnVal = pixels[y << 8 | x];

        return returnVal;
    }
}
