// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.world.level.Level;

public class Giant extends Monster
{
    public Giant(final Level level) {
        super(level);
        this.textureName = "/mob/zombie.png";
        this.runSpeed = 0.5f;
        this.attackDamage = 50;
        this.health *= 10;
        this.heightOffset *= 6.0f;
        this.setSize(this.bbWidth * 6.0f, this.bbHeight * 6.0f);
    }
    
    @Override
    protected float getWalkTargetValue(final int x, final int y, final int z) {
        return this.level.getBrightness(x, y, z) - 0.5f;
    }
}
