// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft;

public class Direction {
    // Useless - Extra constants and constant arrays were added here, they exist in LCE, unsure if they were simply unused in b1.7.3 or added in future updates
    public static final int UNDEFINED = -1;
    public static final int SOUTH = 0;
    public static final int WEST = 1;
    public static final int NORTH = 2;
    public static final int EAST = 3;

    public static final int[] STEP_X = new int[] {0, -1, 0, 1}; // Useless - LCE inclusion

    public static final int[] STEP_Z = new int[] {1, 0, -1, }; // Useless - LCE inclusion

    // for [direction] it gives [tile-face]
    public static final int[] DIRECTION_FACING = new int[]{Facing.SOUTH, Facing.WEST, Facing.NORTH, Facing.EAST};

    // for [facing] it gives [direction]
    public static final int[] FACING_DIRECTION = new int[] {UNDEFINED, UNDEFINED, NORTH, SOUTH, WEST, EAST}; // Useless - LCE inclusion

    // for [direction] it gives [opposite direction]
    public static final int[] DIRECTION_OPPOSITE = new int[]{NORTH, EAST, SOUTH, WEST};

    // for [direction] it gives [90 degrees clockwise direction]
    public static final int[] DIRECTION_CLOCKWISE = new int[] {WEST, NORTH, EAST, SOUTH}; // Useless - LCE inclusion

    // for [direction] it gives [90 degrees counter-clockwise direction]
    public static final int[] DIRECTION_COUNTER_CLOCKWISE = new int[] {EAST, SOUTH, WEST, NORTH}; // Useless - LCE inclusion

    // for [direction][world-facing] it gives [tile-facing]
    public static final int[][] RELATIVE_DIRECTION_FACING = new int[][]{
            {Facing.UP, Facing.DOWN, Facing.SOUTH, Facing.NORTH, Facing.EAST, Facing.WEST}, // south
            {Facing.UP, Facing.DOWN, Facing.EAST, Facing.WEST, Facing.NORTH, Facing.SOUTH}, // west
            {Facing.UP, Facing.DOWN, Facing.NORTH, Facing.SOUTH, Facing.WEST, Facing.EAST}, // north
            {Facing.UP, Facing.DOWN, Facing.WEST, Facing.EAST, Facing.SOUTH, Facing.NORTH} // east
    };

}
