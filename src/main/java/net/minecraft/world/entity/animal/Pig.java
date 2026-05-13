// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.animal;

import net.minecraft.stats.Stat;
import net.minecraft.stats.Achievements;
import net.minecraft.world.entity.monster.PigZombie;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class Pig extends Animal
{
    public Pig(final Level level) {
        super(level);
        this.textureName = "/mob/pig.png";
        this.setSize(0.9f, 0.9f);
    }
    
    @Override
    protected void definedSynchedData() {
        this.entityData.define(16, (byte) 0);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Saddle", this.hasSaddle());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setSaddle(compoundTag.getBoolean("Saddle"));
    }
    
    @Override
    protected String getAmbientSound() {
        return "mob.pig";
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.pig";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.pigdeath";
    }
    
    @Override
    public boolean interact(final Player player) {
        if (this.hasSaddle() && !this.level.isClientSide && (this.rider == null || this.rider == player)) {
            player.ride(this);
            return true;
        }
        return false;
    }
    
    @Override
    protected int getDeathLoot() {
        if (this.onFire > 0) {
            return Item.porkChop_cooked.id;
        }
        return Item.porkChop_raw.id;
    }
    
    public boolean hasSaddle() {
        return (this.entityData.getByte(16) & 0x1) != 0x0;
    }
    
    public void setSaddle(final boolean value) {
        if (value) {
            this.entityData.set(16, 1);
        }
        else {
            this.entityData.set(16, 0);
        }
    }
    
    @Override
    public void thunderHit(final LightningBolt lightningBolt) {
        if (this.level.isClientSide) {
            return;
        }
        final PigZombie e = new PigZombie(this.level);
        e.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
        this.level.addEntity(e);
        this.remove();
    }
    
    @Override
    protected void causeFallDamage(final float distance) {
        super.causeFallDamage(distance);
        if (distance > 5.0f && this.rider instanceof Player) {
            ((Player)this.rider).awardStat(Achievements.flyPig);
        }
    }
}
