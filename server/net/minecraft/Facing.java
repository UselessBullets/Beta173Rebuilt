// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft;

public class Facing
{
    public static final int[] OPPOSITE_FACING;
    public static final int[] STEP_X;
    public static final int[] STEP_Y;
    public static final int[] STEP_Z;
    
    static {
        OPPOSITE_FACING = new int[] { 1, 0, 3, 2, 5, 4 };
        STEP_X = new int[] { 0, 0, 0, 0, -1, 1 };
        STEP_Y = new int[] { -1, 1, 0, 0, 0, 0 };
        STEP_Z = new int[] { 0, 0, -1, 1, 0, 0 };
    }
}
