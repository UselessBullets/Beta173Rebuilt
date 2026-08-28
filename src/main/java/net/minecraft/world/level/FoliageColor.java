// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public class FoliageColor
{
    private static int[] pixels = new int[256 * 256];
    
    public static void init(final int[] pixels) {
        FoliageColor.pixels = pixels;
    }
    
    public static int get(final double temp, double rain) {
        rain *= temp;
        int x = (int) ((1.0 - temp) * 255.0);
        int y = (int) ((1.0 - rain) * 255.0);
        int returnVal = FoliageColor.pixels[y << 8 | x];

        return returnVal;
    }
    
    public static int getEvergreenColor() {
        return 0x619961;
    }
    
    public static int getBirchColor() {
        return 0x80a755;
    }
    
    public static int getDefaultColor() {
        return 0x48b518;
    }

}
