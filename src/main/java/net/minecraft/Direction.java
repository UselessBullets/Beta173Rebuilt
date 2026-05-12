// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft;

public class Direction
{
    public static final int[] DIRECTION_FACING;
    public static final int[] DIRECTION_OPPOSITE;
    public static final int[][] RELATIVE_DIRECTION_FACING;
    
    static {
        DIRECTION_FACING = new int[] { 3, 4, 2, 5 };
        DIRECTION_OPPOSITE = new int[] { 2, 3, 0, 1 };
        RELATIVE_DIRECTION_FACING = new int[][] { { 1, 0, 3, 2, 5, 4 }, { 1, 0, 5, 4, 2, 3 }, { 1, 0, 2, 3, 4, 5 }, { 1, 0, 4, 5, 3, 2 } };
    }
}
