// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.monster;

import net.minecraft.world.Difficulty;
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
    private static final int ID_SIZE = 16;
    public float squish;
    public float oSquish;
    private int jumpDelay = 0;
    
    public Slime(final Level level) {
        super(level);
        this.textureName = "/mob/slime.png";
        final int size = 1 << this.random.nextInt(3);
        this.heightOffset = 0.0f;
        this.jumpDelay = this.random.nextInt(20) + 10;
        this.setSize(size);
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(ID_SIZE, (byte) 1);
    }
    
    public void setSize(final int size) {
        this.entityData.set(ID_SIZE, (byte) size);
        this.setSize(0.6f * size, 0.6f * size);
        this.health = size * size;
        this.setPos(this.x, this.y, this.z);
    }
    
    public int getSize() {
        return this.entityData.getByte(ID_SIZE);
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

        final boolean wasOnGround = this.onGround;
        super.tick();
        if (this.onGround && !wasOnGround) {
            final int size = this.getSize();
            for (int i = 0; i < size * 8; ++i) {
                final float dir = this.random.nextFloat() * Mth.PI * 2.0f;
                final float d = this.random.nextFloat() * 0.5f + 0.5f;
                float xd = Mth.sin(dir) * size * 0.5f * d;
                float zd = Mth.cos(dir) * size * 0.5f * d;
                this.level.addParticle("slime", this.x + xd, this.bb.y0, this.z + zd, 0.0, 0.0, 0.0);
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
        final Player player = this.level.getNearestPlayer(this, 16.0);
        if (player != null) {
            this.lookAt(player, 10.0f, 20.0f);
        }

        if (this.onGround && this.jumpDelay-- <= 0) {
            this.jumpDelay = this.random.nextInt(20) + 10;
            if (player != null) {
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
                this.xxa = this.yya = 0;
            }
        }
    }
    
    @Override
    public void remove() {
        final int size = this.getSize();
        if (!this.level.isClientSide && size > 1 && this.health == 0) {
            for (int i = 0; i < 4; ++i) {
                final float xd = (i % 2 - 0.5f) * size / 4.0f;
                final float zd = (i / 2 - 0.5f) * size / 4.0f;
                final Slime slime = new Slime(this.level);
                slime.setSize(size / 2);
                slime.moveTo(this.x + xd, this.y + 0.5, this.z + zd, this.random.nextFloat() * 360.0f, 0.0f);
                this.level.addEntity(slime);
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
        if (this.getSize() == 1) return Item.slimeBall.id;
        return 0;
    }
    
    @Override
    public boolean canSpawn() {
        final LevelChunk lc = this.level.getChunkAt(Mth.floor(this.x), Mth.floor(this.z));
        return (this.getSize() == 1 || this.level.difficulty > Difficulty.PEACEFUL) && this.random.nextInt(10) == 0 && lc.getRandom(987234911L).nextInt(10) == 0 && this.y < 16.0;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.6f;
    }
}
