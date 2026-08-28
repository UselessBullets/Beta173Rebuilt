package net.minecraft.world.level;

// Useless - Class exists in LCE, thinking since it just stored static constants it got compiled away, and level events did not exist in b1.2 so wouldn't be in that jar either
public class LevelEvent {
    public static final int SOUND_CLICK = 1000;
    public static final int SOUND_CLICK_FAIL = 1001;
    public static final int SOUND_LAUNCH = 1002;
    public static final int SOUND_OPEN_DOOR = 1003;
    public static final int SOUND_FIZZ = 1004;

    public static final int SOUND_PLAY_RECORDING = 1005;

    public static final int PARTICLES_SHOOT = 2000;
    public static final int PARTICLES_DESTROY_BLOCK = 2001;
}
