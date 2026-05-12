// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public class WaterColor
{
    private static int[] pixels;
    
    public static void init(final int[] pixels) {
        WaterColor.pixels = pixels;
    }
    
    static {
        WaterColor.pixels = new int[65536];
    }
}
