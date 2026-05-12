// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import util.Mth;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;

public class Slime extends Mob implements Enemy
{
    public float squish;
    public float oSquish;
    private int jumpDelay;
    
    public Slime(final Level level) {
        super(level);
        this.jumpDelay = 0;
        this.textureName = "/mob/slime.png";
        final int size = 1 << this.random.nextInt(3);
        this.heightOffset = 0.0f;
        this.jumpDelay = this.random.nextInt(20) + 10;
        this.setSize(size);
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(16, new Byte((byte)1));
    }
    
    public void setSize(final int size) {
        this.entityData.set(16, new Byte((byte)size));
        this.setSize(0.6f * size, 0.6f * size);
        this.health = size * size;
        this.setPos(this.x, this.y, this.z);
    }
    
    public int getSize() {
        return this.entityData.getByte(16);
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Size", this.getSize() - 1);
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setSize(compoundTag.getInt("Size") + 1);
    }
    
    @Override
    public void tick() {
        this.oSquish = this.squish;
        final boolean onGround = this.onGround;
        super.tick();
        if (this.onGround && !onGround) {
            final int size = this.getSize();
            for (int i = 0; i < size * 8; ++i) {
                final float n = this.random.nextFloat() * 3.1415927f * 2.0f;
                final float n2 = this.random.nextFloat() * 0.5f + 0.5f;
                this.level.addParticle("slime", this.x + Mth.sin(n) * size * 0.5f * n2, this.bb.y0, this.z + Mth.cos(n) * size * 0.5f * n2, 0.0, 0.0, 0.0);
            }
            if (size > 2) {
                this.level.playSound(this, "mob.slime", this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) / 0.8f);
            }
            this.squish = -0.5f;
        }
        this.squish *= 0.6f;
    }
    
    @Override
    protected void updateAi() {
        this.checkDespawn();
        final Player nearestPlayer = this.level.getNearestPlayer(this, 16.0);
        if (nearestPlayer != null) {
            this.lookAt(nearestPlayer, 10.0f, 20.0f);
        }
        if (this.onGround && this.jumpDelay-- <= 0) {
            this.jumpDelay = this.random.nextInt(20) + 10;
            if (nearestPlayer != null) {
                this.jumpDelay /= 3;
            }
            this.jumping = true;
            if (this.getSize() > 1) {
                this.level.playSound(this, "mob.slime", this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 0.8f);
            }
            this.squish = 1.0f;
            this.xxa = 1.0f - this.random.nextFloat() * 2.0f;
            this.yya = (float)(1 * this.getSize());
        }
        else {
            this.jumping = false;
            if (this.onGround) {
                final float n = 0.0f;
                this.yya = n;
                this.xxa = n;
            }
        }
    }
    
    @Override
    public void remove() {
        final int size = this.getSize();
        if (!this.level.isClientSide && size > 1 && this.health == 0) {
            for (int i = 0; i < 4; ++i) {
                final float n = (i % 2 - 0.5f) * size / 4.0f;
                final float n2 = (i / 2 - 0.5f) * size / 4.0f;
                final Slime e = new Slime(this.level);
                e.setSize(size / 2);
                e.moveTo(this.x + n, this.y + 0.5, this.z + n2, this.random.nextFloat() * 360.0f, 0.0f);
                this.level.addEntity(e);
            }
        }
        super.remove();
    }
    
    @Override
    public void playerTouch(final Player player) {
        final int size = this.getSize();
        if (size > 1 && this.canSee(player) && this.distanceTo(player) < 0.6 * size && player.hurt(this, size)) {
            this.level.playSound(this, "mob.slimeattack", 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
        }
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.slime";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.slime";
    }
    
    @Override
    protected int getDeathLoot() {
        if (this.getSize() == 1) {
            return Item.slimeBall.id;
        }
        return 0;
    }
    
    @Override
    public boolean canSpawn() {
        final LevelChunk chunk = this.level.getChunkAt(Mth.floor(this.x), Mth.floor(this.z));
        return (this.getSize() == 1 || this.level.difficulty > 0) && this.random.nextInt(10) == 0 && chunk.getRandom(987234911L).nextInt(10) == 0 && this.y < 16.0;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.6f;
    }
}
