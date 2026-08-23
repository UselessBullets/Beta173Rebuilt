// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.entity.animal;

import java.util.Random;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyePowderItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class Sheep extends Animal
{
    private static final int DATA_WOOL_ID = 16;
    public static final float[][] COLOR = new float[][] {
            { 1.0f, 1.0f, 1.0f },  // white
            { 0.95f, 0.7f, 0.2f }, // orange
            { 0.9f, 0.5f, 0.85f }, // magenta
            { 0.6f, 0.7f, 0.95f }, // light blue
            { 0.9f, 0.9f, 0.2f }, // yellow
            { 0.5f, 0.8f, 0.1f }, // light green
            { 0.95f, 0.7f, 0.8f }, // pink
            { 0.3f, 0.3f, 0.3f }, // gray
            { 0.6f, 0.6f, 0.6f }, // silver
            { 0.3f, 0.6f, 0.7f }, // cyan
            { 0.7f, 0.4f, 0.9f }, // purple
            { 0.2f, 0.4f, 0.8f }, // blue
            { 0.5f, 0.4f, 0.3f }, // brown
            { 0.4f, 0.5f, 0.2f }, // green
            { 0.8f, 0.3f, 0.3f }, // red
            { 0.1f, 0.1f, 0.1f }, // black
    };
    
    public Sheep(final Level level) {
        super(level);
        this.textureName = "/mob/sheep.png";
        this.setSize(0.9f, 1.3f);
    }
    
    @Override
    protected void definedSynchedData() {
        super.definedSynchedData();

        // sheared and color share a byte
        this.entityData.define(DATA_WOOL_ID, (byte) 0);
    }
    
    @Override
    public boolean hurt(final Entity source, final int dmg) {
        return super.hurt(source, dmg);
    }
    
    @Override
    protected void dropDeathLoot() {
        if (!this.isSheared()) {
            // killing a non-sheared sheep will drop a single block of cloth
            this.spawnAtLocation(new ItemInstance(Tile.cloth.id, 1, this.getColor()), 0.0f);
        }
    }
    
    @Override
    protected int getDeathLoot() {
        return Tile.cloth.id;
    }
    
    @Override
    public boolean interact(final Player player) {
        final ItemInstance item = player.inventory.getSelected();
        if (item != null && item.id == Item.shears.id && !this.isSheared()) {
            if (!this.level.isClientSide) {
                this.setSheared(true);
                int count = 2 + this.random.nextInt(3);
                for (int i = 0; i < count; ++i) {
                    final ItemEntity ie = this.spawnAtLocation(new ItemInstance(Tile.cloth.id, 1, this.getColor()), 1.0f);
                    ie.yd += this.random.nextFloat() * 0.05f;
                    ie.xd += (this.random.nextFloat() - this.random.nextFloat()) * 0.1f;
                    ie.zd += (this.random.nextFloat() - this.random.nextFloat()) * 0.1f;
                }
            }
            item.hurt(1, player);
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
        return this.entityData.getByte(DATA_WOOL_ID) & 0xF;
    }
    
    public void setColor(final int color) {
        this.entityData.set(DATA_WOOL_ID, (byte)((this.entityData.getByte(DATA_WOOL_ID) & 0xF0) | (color & 0xF)));
    }
    
    public boolean isSheared() {
        return (this.entityData.getByte(DATA_WOOL_ID) & 0x10) != 0x0;
    }
    
    public void setSheared(final boolean value) {
        final byte current = this.entityData.getByte(DATA_WOOL_ID);
        if (value) {
            this.entityData.set(DATA_WOOL_ID, (byte)(current | 0x10));
        }
        else {
            this.entityData.set(DATA_WOOL_ID, (byte)(current & ~0x10));
        }
    }
    
    public static int getSheepColor(final Random random) {
        final int nextInt = random.nextInt(100);
        if (nextInt < 5) return 15 - DyePowderItem.BLACK;
        if (nextInt < 10) return 15 - DyePowderItem.GRAY;
        if (nextInt < 15) return 15 - DyePowderItem.SILVER;
        if (nextInt < 18) return 15 - DyePowderItem.BROWN;
        if (random.nextInt(500) == 0) return 15 - DyePowderItem.PINK;
        return 0;
    }

}
