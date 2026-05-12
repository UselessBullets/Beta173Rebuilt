// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class Creeper extends Monster
{
    int swell;
    int oldSwell;
    
    public Creeper(final Level level) {
        super(level);
        this.textureName = "/mob/creeper.png";
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(16, -1);
        this.entityData.define(17, 0);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.entityData.getByte(17) == 1) {
            compoundTag.putBoolean("powered", true);
        }
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(17, (byte)(byte)(compoundTag.getBoolean("powered") ? 1 : 0));
    }
    
    @Override
    protected void cantSeeTarget(final Entity target, final float distance) {
        if (this.level.isClientSide) {
            return;
        }
        if (this.swell > 0) {
            this.setSwellDir(-1);
            --this.swell;
            if (this.swell < 0) {
                this.swell = 0;
            }
        }
    }
    
    @Override
    public void tick() {
        this.oldSwell = this.swell;
        if (this.level.isClientSide) {
            final int swellDir = this.getSwellDir();
            if (swellDir > 0 && this.swell == 0) {
                this.level.playSound(this, "random.fuse", 1.0f, 0.5f);
            }
            this.swell += swellDir;
            if (this.swell < 0) {
                this.swell = 0;
            }
            if (this.swell >= 30) {
                this.swell = 30;
            }
        }
        super.tick();
        if (this.attackTarget == null && this.swell > 0) {
            this.setSwellDir(-1);
            --this.swell;
            if (this.swell < 0) {
                this.swell = 0;
            }
        }
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.creeper";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.creeperdeath";
    }
    
    @Override
    public void die(final Entity source) {
        super.die(source);
        if (source instanceof Skeleton) {
            this.spawnAtLocation(Item.record_01.id + this.random.nextInt(2), 1);
        }
    }
    
    @Override
    protected void checkHurtTarget(final Entity target, final float distance) {
        if (this.level.isClientSide) {
            return;
        }
        final int swellDir = this.getSwellDir();
        if ((swellDir <= 0 && distance < 3.0f) || (swellDir > 0 && distance < 7.0f)) {
            if (this.swell == 0) {
                this.level.playSound(this, "random.fuse", 1.0f, 0.5f);
            }
            this.setSwellDir(1);
            ++this.swell;
            if (this.swell >= 30) {
                if (this.isPowered()) {
                    this.level.explode(this, this.x, this.y, this.z, 6.0f);
                }
                else {
                    this.level.explode(this, this.x, this.y, this.z, 3.0f);
                }
                this.remove();
            }
            this.holdGround = true;
        }
        else {
            this.setSwellDir(-1);
            --this.swell;
            if (this.swell < 0) {
                this.swell = 0;
            }
        }
    }
    
    public boolean isPowered() {
        return this.entityData.getByte(17) == 1;
    }
    
    public float getSwelling(final float partialTick) {
        return (this.oldSwell + (this.swell - this.oldSwell) * partialTick) / 28.0f;
    }
    
    @Override
    protected int getDeathLoot() {
        return Item.sulphur.id;
    }
    
    private int getSwellDir() {
        return this.entityData.getByte(16);
    }
    
    private void setSwellDir(final int dir) {
        this.entityData.set(16, (byte)dir);
    }
    
    @Override
    public void thunderHit(final LightningBolt lightningBolt) {
        super.thunderHit(lightningBolt);
        this.entityData.set(17, 1);
    }
}
