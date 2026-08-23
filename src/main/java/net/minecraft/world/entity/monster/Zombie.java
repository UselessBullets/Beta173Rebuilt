// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.SharedConstants;
import net.minecraft.world.item.Item;
import util.Mth;
import net.minecraft.world.level.Level;

public class Zombie extends Monster
{
    public Zombie(final Level level) {
        super(level);
        this.textureName = "/mob/zombie.png";
        this.runSpeed = 0.5f;
        this.attackDamage = 5;
    }
    
    @Override
    public void aiStep() {
        if (this.level.isDay()) {
            final float br = this.getBrightness(1.0f);
            if (br > 0.5f && this.level.canSeeSky(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z)) && this.random.nextFloat() * 30.0f < (br - 0.4f) * 2.0f) {
                this.onFire = 8 * SharedConstants.TICKS_PER_SECOND;
            }
        }
        super.aiStep();
    }
    
    @Override
    protected String getAmbientSound() {
        return "mob.zombie";
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.zombiehurt";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.zombiedeath";
    }
    
    @Override
    protected int getDeathLoot() {
        return Item.feather.id;
    }
}
