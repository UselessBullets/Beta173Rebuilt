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
    public static final int DATA_SWELL_DIR = 16;
    public static final int DATA_IS_POWERED = 17;
    int swell;
    int oldSwell;
    public static final int MAX_SWELL = 30;
    
    public Creeper(final Level level) {
        super(level);
        this.textureName = "/mob/creeper.png";
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(DATA_SWELL_DIR, (byte)-1);
        this.entityData.define(DATA_IS_POWERED, (byte)0);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.entityData.getByte(DATA_IS_POWERED) == 1) compoundTag.putBoolean("powered", true);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(DATA_IS_POWERED, (byte)(compoundTag.getBoolean("powered") ? 1 : 0));
    }
    
    @Override
    protected void cantSeeTarget(final Entity target, final float distance) {
        if (this.level.isClientSide) return;

        if (this.swell > 0) {
            this.setSwellDir(-1);
            this.swell--;
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
            if (this.swell < 0) this.swell = 0;
            if (this.swell >= MAX_SWELL) this.swell = MAX_SWELL;
        }

        super.tick();

        if (this.attackTarget == null && this.swell > 0) {
            this.setSwellDir(-1);
            this.swell--;
            if (this.swell < 0) this.swell = 0;
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
        if (this.level.isClientSide) return;

        final int swellDir = this.getSwellDir();
        if (swellDir <= 0 && distance < 3.0f || swellDir > 0 && distance < 7.0f) {
            if (this.swell == 0) {
                this.level.playSound(this, "random.fuse", 1.0f, 0.5f);
            }
            this.setSwellDir(1);
            this.swell++;
            if (this.swell >= MAX_SWELL) {
                if (this.isPowered()) this.level.explode(this, this.x, this.y, this.z, 6.0f);
                else this.level.explode(this, this.x, this.y, this.z, 3.0f);
                this.remove();
            }
            this.holdGround = true;
        } else {
            this.setSwellDir(-1);
            this.swell--;
            if (this.swell < 0) {
                this.swell = 0;
            }
        }
    }
    
    public boolean isPowered() {
        return this.entityData.getByte(DATA_IS_POWERED) == 1;
    }
    
    public float getSwelling(final float a) {
        return (this.oldSwell + (this.swell - this.oldSwell) * a) / (MAX_SWELL - 2);
    }
    
    @Override
    protected int getDeathLoot() {
        return Item.sulphur.id;
    }
    
    private int getSwellDir() {
        return this.entityData.getByte(DATA_SWELL_DIR);
    }
    
    private void setSwellDir(final int dir) {
        this.entityData.set(DATA_SWELL_DIR, (byte)dir);
    }
    
    @Override
    public void thunderHit(final LightningBolt lightningBolt) {
        super.thunderHit(lightningBolt);
        this.entityData.set(DATA_IS_POWERED, 1);
    }
}
