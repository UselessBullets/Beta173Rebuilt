// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Options;

public class KeyboardInput extends Input
{
    public static final int KEY_UP = 0;
    public static final int KEY_DOWN = 1;
    public static final int KEY_LEFT = 2;
    public static final int KEY_RIGHT = 3;
    public static final int KEY_JUMP = 4;
    public static final int KEY_SNEAK = 5;
    private boolean[] keys = new boolean[10];
    private Options options;
    
    public KeyboardInput(final Options options) {
        this.options = options;
    }
    
    @Override
    public void setKey(final int eventKey, final boolean eventKeyState) {
        int id = -1;
        if (eventKey == this.options.keyUp.key) id = KEY_UP;
        if (eventKey == this.options.keyDown.key) id = KEY_DOWN;
        if (eventKey == this.options.keyLeft.key) id = KEY_LEFT;
        if (eventKey == this.options.keyRight.key) id = KEY_RIGHT;
        if (eventKey == this.options.keyJump.key) id = KEY_JUMP;
        if (eventKey == this.options.keySneak.key) id = KEY_SNEAK;

        if (id >= 0) {
            this.keys[id] = eventKeyState;
        }
    }
    
    @Override
    public void releaseAllKeys() {
        for (int i = 0; i < 10; ++i) {
            this.keys[i] = false;
        }
    }
    
    @Override
    public void tick(final Player player) {
        this.xa = 0.0f;
        this.ya = 0.0f;

        if (this.keys[KEY_UP]) ++this.ya;
        if (this.keys[KEY_DOWN]) --this.ya;
        if (this.keys[KEY_LEFT]) ++this.xa;
        if (this.keys[KEY_RIGHT]) --this.xa;

        this.jumping = this.keys[KEY_JUMP];
        this.sneaking = this.keys[KEY_SNEAK];
        if (this.sneaking) {
            this.xa *= (float)0.3;
            this.ya *= (float)0.3;
        }
    }
}
