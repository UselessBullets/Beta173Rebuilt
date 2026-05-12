// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Options;

public class KeyboardInput extends Input
{
    private boolean[] keys;
    private Options options;
    
    public KeyboardInput(final Options options) {
        this.keys = new boolean[10];
        this.options = options;
    }
    
    @Override
    public void setKey(final int eventKey, final boolean eventKeyState) {
        int n = -1;
        if (eventKey == this.options.keyUp.key) {
            n = 0;
        }
        if (eventKey == this.options.keyDown.key) {
            n = 1;
        }
        if (eventKey == this.options.keyLeft.key) {
            n = 2;
        }
        if (eventKey == this.options.keyRight.key) {
            n = 3;
        }
        if (eventKey == this.options.keyJump.key) {
            n = 4;
        }
        if (eventKey == this.options.keySneak.key) {
            n = 5;
        }
        if (n >= 0) {
            this.keys[n] = eventKeyState;
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
        if (this.keys[0]) {
            ++this.ya;
        }
        if (this.keys[1]) {
            --this.ya;
        }
        if (this.keys[2]) {
            ++this.xa;
        }
        if (this.keys[3]) {
            --this.xa;
        }
        this.jumping = this.keys[4];
        this.sneaking = this.keys[5];
        if (this.sneaking) {
            this.xa *= (float)0.3;
            this.ya *= (float)0.3;
        }
    }
}
