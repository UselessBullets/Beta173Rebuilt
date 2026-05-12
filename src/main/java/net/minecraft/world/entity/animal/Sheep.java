// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.animal;

import java.util.Random;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class Sheep extends Animal
{
    public static final float[][] COLOR;
    
    public Sheep(final Level level) {
        super(level);
        this.textureName = "/mob/sheep.png";
        this.setSize(0.9f, 1.3f);
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();
        this.entityData.define(16, new Byte((byte)0));
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        return super.hurt(source, dmg);
    }
    
    @Override
    protected void dropDeathLoot() {
        if (!this.isSheared()) {
            this.spawnAtLocation(new ItemInstance(Tile.cloth.id, 1, this.getColor()), 0.0f);
        }
    }
    
    @Override
    protected int getDeathLoot() {
        return Tile.cloth.id;
    }
    
    @Override
    public boolean interact(final Player player) {
        final ItemInstance selected = player.inventory.getSelected();
        if (selected != null && selected.id == Item.shears.id && !this.isSheared()) {
            if (!this.level.isClientSide) {
                this.setSheared(true);
                for (int n = 2 + this.random.nextInt(3), i = 0; i < n; ++i) {
                    final ItemEntity ie = this.spawnAtLocation(new ItemInstance(Tile.cloth.id, 1, this.getColor()), 1.0f);
                    ie.yd += this.random.nextFloat() * 0.05f;
                    ie.xd += (this.random.nextFloat() - this.random.nextFloat()) * 0.1f;
                    ie.zd += (this.random.nextFloat() - this.random.nextFloat()) * 0.1f;
                }
            }
            selected.hurt(1, player);
        }
        return false;
    }
    
    @Override
    public void addAdditionalSaveData(final CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Sheared", this.isSheared());
        compoundTag.putByte("Color", (byte)this.getColor());
    }
    
    @Override
    public void readAdditionalSaveData(final CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setSheared(compoundTag.getBoolean("Sheared"));
        this.setColor(compoundTag.getByte("Color"));
    }
    
    @Override
    protected String getAmbientSound() {
        return "mob.sheep";
    }
    
    @Override
    protected String getHurtSound() {
        return "mob.sheep";
    }
    
    @Override
    protected String getDeathSound() {
        return "mob.sheep";
    }
    
    public int getColor() {
        return this.entityData.getByte(16) & 0xF;
    }
    
    public void setColor(final int color) {
        this.entityData.set(16, (byte)((this.entityData.getByte(16) & 0xF0) | (color & 0xF)));
    }
    
    public boolean isSheared() {
        return (this.entityData.getByte(16) & 0x10) != 0x0;
    }
    
    public void setSheared(final boolean value) {
        final byte byte1 = this.entityData.getByte(16);
        if (value) {
            this.entityData.set(16, (byte)(byte1 | 0x10));
        }
        else {
            this.entityData.set(16, (byte)(byte1 & 0xFFFFFFEF));
        }
    }
    
    public static int getSheepColor(final Random random) {
        final int nextInt = random.nextInt(100);
        if (nextInt < 5) {
            return 15;
        }
        if (nextInt < 10) {
            return 7;
        }
        if (nextInt < 15) {
            return 8;
        }
        if (nextInt < 18) {
            return 12;
        }
        if (random.nextInt(500) == 0) {
            return 6;
        }
        return 0;
    }
    
    static {
        COLOR = new float[][] { { 1.0f, 1.0f, 1.0f }, { 0.95f, 0.7f, 0.2f }, { 0.9f, 0.5f, 0.85f }, { 0.6f, 0.7f, 0.95f }, { 0.9f, 0.9f, 0.2f }, { 0.5f, 0.8f, 0.1f }, { 0.95f, 0.7f, 0.8f }, { 0.3f, 0.3f, 0.3f }, { 0.6f, 0.6f, 0.6f }, { 0.3f, 0.6f, 0.7f }, { 0.7f, 0.4f, 0.9f }, { 0.2f, 0.4f, 0.8f }, { 0.5f, 0.4f, 0.3f }, { 0.4f, 0.5f, 0.2f }, { 0.8f, 0.3f, 0.3f }, { 0.1f, 0.1f, 0.1f } };
    }
}
