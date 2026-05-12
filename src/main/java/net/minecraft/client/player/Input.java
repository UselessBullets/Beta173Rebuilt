// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.player;

import net.minecraft.world.entity.player.Player;

public class Input
{
    public float xa;
    public float ya;
    public boolean wasJumping;
    public boolean jumping;
    public boolean sneaking;
    
    public Input() {
        this.xa = 0.0f;
        this.ya = 0.0f;
        this.wasJumping = false;
        this.jumping = false;
        this.sneaking = false;
    }
    
    public void tick(final Player player) {
    }
    
    public void releaseAllKeys() {
    }
    
    public void setKey(final int eventKey, final boolean eventKeyState) {
    }
}
