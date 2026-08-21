// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft;

public class Facing
{
    public static final int DOWN = 0;
    public static final int UP = 1;
    public static final int NORTH = 2;
    public static final int SOUTH = 3;
    public static final int WEST = 4;
    public static final int EAST = 5;
    public static final int[] OPPOSITE_FACING = new int[] { UP, DOWN, SOUTH, NORTH, EAST, WEST };
    public static final int[] STEP_X = new int[] { 0, 0, 0, 0, -1, 1 };
    public static final int[] STEP_Y = new int[] { -1, 1, 0, 0, 0, 0 };
    public static final int[] STEP_Z = new int[] { 0, 0, -1, 1, 0, 0 };

}
