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
        if (this.getBrightness(1.0f) < 0.5f) {
            return this.level.getNearestPlayer(this, 16.0);
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
    protected void checkHurtTarget(final Entity target, final float distance) {
        if (this.getBrightness(1.0f) > 0.5f && this.random.nextInt(100) == 0) {
            this.attackTarget = null;
            return;
        }
        if (distance > 2.0f && distance < 6.0f && this.random.nextInt(10) == 0) {
            if (this.onGround) {
                final double n = target.x - this.x;
                final double n2 = target.z - this.z;
                final float sqrt = Mth.sqrt(n * n + n2 * n2);
                this.xd = n / sqrt * 0.5 * 0.800000011920929 + this.xd * 0.20000000298023224;
                this.zd = n2 / sqrt * 0.5 * 0.800000011920929 + this.zd * 0.20000000298023224;
                this.yd = 0.4000000059604645;
            }
        }
        else {
            super.checkHurtTarget(target, distance);
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
