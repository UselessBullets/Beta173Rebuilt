// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.world.item.Item;
import com.mojang.nbt.CompoundTag;
import util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class Spider extends Monster
{
    public Spider(final Level level) {
        super(level);
        this.textureName = "/mob/spider.png";
        this.setSize(1.4f, 0.9f);
        this.runSpeed = 0.8f;
    }
    
    @Override
    public double getRideHeight() {
        return this.bbHeight * 0.75 - 0.5;
    }
    
    @Override
    protected boolean makeStepSound() {
        return false;
    }
    
    @Override
    protected Entity findAttackTarget() {
        float br = this.getBrightness(1.0f);
        if (br < 0.5f) {
            double maxDist = 16;
            return this.level.getNearestPlayer(this, maxDist);
        }
        return null;
    }
    
    @Override
    protected String getAmbientSound() {
        return "mob.spider";
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.spider";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.spiderdeath";
    }
    
    @Override
    protected void checkHurtTarget(final Entity target, final float d) {
        float br = this.getBrightness(1.0f);
        if (br > 0.5f && this.random.nextInt(100) == 0) {
            this.attackTarget = null;
            return;
        }

        if (d > 2.0f && d < 6.0f && this.random.nextInt(10) == 0) {
            if (this.onGround) {
                final double xdd = target.x - this.x;
                final double zdd = target.z - this.z;
                final float dd = Mth.sqrt(xdd * xdd + zdd * zdd);
                this.xd = xdd / dd * 0.5 * 0.8f + this.xd * 0.2f;
                this.zd = zdd / dd * 0.5 * 0.8f + this.zd * 0.2f;
                this.yd = 0.4f;
            }
        }
        else {
            super.checkHurtTarget(target, d);
        }
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }
    
    @Override
    protected int getDeathLoot() {
        return Item.string.id;
    }
    
    @Override
    public boolean onLadder() {
        return this.horizontalCollision;
    }
}
