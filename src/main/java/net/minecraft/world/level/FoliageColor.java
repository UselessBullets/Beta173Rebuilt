// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public class FoliageColor
{
    private static int[] pixels;
    
    public static void init(final int[] pixels) {
        FoliageColor.pixels = pixels;
    }
    
    public static int get(final double temp, double rain) {
        rain *= temp;
        return FoliageColor.pixels[(int)((1.0 - rain) * 255.0) << 8 | (int)((1.0 - temp) * 255.0)];
    }
    
    public static int getEvergreenColor() {
        return 6396257;
    }
    
    public static int getBirchColor() {
        return 8431445;
    }
    
    public static int getDefaultColor() {
        return 4764952;
    }
    
    static {
        FoliageColor.pixels = new int[65536];
    }
}
