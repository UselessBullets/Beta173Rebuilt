package net.minecraft.world.entity;

// Useless - Class existed in LCE and b1.2 leak
public class EntityEvent {
    public static final byte JUMP = 1;
    public static final byte HURT = 2;
    public static final byte DEATH = 3;
    public static final byte START_ATTACKING = 4;
    public static final byte STOP_ATTACKING = 5;

    public static final byte TAMING_FAILED = 6;
    public static final byte TAMING_SUCCEEDED = 7;
    public static final byte SHAKE_WETNESS = 8;
}
