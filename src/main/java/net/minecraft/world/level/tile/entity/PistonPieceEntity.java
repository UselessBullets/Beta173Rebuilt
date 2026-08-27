// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import java.util.ArrayList;
import com.mojang.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.Facing;
import java.util.List;

public class PistonPieceEntity extends TileEntity
{
    private int id;
    private int data;
    private int facing;
    private boolean extending;
    private boolean isSourcePiston;
    private float progress, progressO;
    private static List<Entity> collisionHolder = new ArrayList<>();
    
    public PistonPieceEntity() {
    }
    
    public PistonPieceEntity(final int id, final int data, final int facing, final boolean extending, final boolean isSourcePiston) {
        this.id = id;
        this.data = data;
        this.facing = facing;
        this.extending = extending;
        this.isSourcePiston = isSourcePiston;
    }
    
    public int getId() {
        return this.id;
    }
    
    @Override
    public int getData() {
        return this.data;
    }
    
    public boolean isExtending() {
        return this.extending;
    }
    
    public int getFacing() {
        return this.facing;
    }
    
    public boolean isSourcePiston() {
        return this.isSourcePiston;
    }
    
    public float getProgress(float a) {
        if (a > 1.0f) a = 1.0f;
        return this.progressO + (this.progress - this.progressO) * a;
    }
    
    public float getXOff(final float a) {
        if (this.extending) {
            return (this.getProgress(a) - 1.0f) * Facing.STEP_X[this.facing];
        } else {
            return (1.0f - this.getProgress(a)) * Facing.STEP_X[this.facing];
        }
    }
    
    public float getYOff(final float a) {
        if (this.extending) {
            return (this.getProgress(a) - 1.0f) * Facing.STEP_Y[this.facing];
        } else {
            return (1.0f - this.getProgress(a)) * Facing.STEP_Y[this.facing];
        }
    }
    
    public float getZOff(final float a) {
        if (this.extending) {
            return (this.getProgress(a) - 1.0f) * Facing.STEP_Z[this.facing];
        } else {
            return (1.0f - this.getProgress(a)) * Facing.STEP_Z[this.facing];
        }
    }
    
    private void moveCollidedEntities(float progress, final float amount) {
        if (!this.extending) {
            progress--;
        }
        else {
            progress = 1.0f - progress;
        }

        final AABB aabb = Tile.pistonMovingPiece.getAABB(this.level, this.x, this.y, this.z, this.id, progress, this.facing);
        if (aabb != null) {
            final List<Entity> entities = this.level.getEntities(null, aabb);
            if (!entities.isEmpty()) {
                PistonPieceEntity.collisionHolder.addAll(entities);
                for (Entity entity : PistonPieceEntity.collisionHolder) {
                    entity.move(amount * Facing.STEP_X[this.facing],
                                amount * Facing.STEP_Y[this.facing],
                                amount * Facing.STEP_Z[this.facing]);
                }
                PistonPieceEntity.collisionHolder.clear();
            }
        }
    }
    
    public void finalTick() {
        if (this.progressO < 1.0f) {
            this.progressO = this.progress = 1.0f;
            this.level.removeTileEntity(this.x, this.y, this.z);
            this.setRemoved();
            if (this.level.getTile(this.x, this.y, this.z) == Tile.pistonMovingPiece.id)
                this.level.setTileAndData(this.x, this.y, this.z, this.id, this.data);
        }
    }
    
    @Override
    public void tick() {
        this.progressO = this.progress;

        if (this.progressO >= 1.0f) {
            this.moveCollidedEntities(1.0f, 4 / 16f);
            this.level.removeTileEntity(this.x, this.y, this.z);
            this.setRemoved();
            if (this.level.getTile(this.x, this.y, this.z) == Tile.pistonMovingPiece.id)
                this.level.setTileAndData(this.x, this.y, this.z, this.id, this.data);
            return;
        }

        this.progress += 0.5f;
        if (this.progress >= 1.0f) {
            this.progress = 1.0f;
        }

        if (this.extending) {
            this.moveCollidedEntities(this.progress, this.progress - this.progressO + 1 / 16f);
        }
    }
    
    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);

        this.id = tag.getInt("blockId");
        this.data = tag.getInt("blockData");
        this.facing = tag.getInt("facing");
        this.progressO = this.progress = tag.getFloat("progress");
        this.extending = tag.getBoolean("extending");
    }
    
    @Override
    public void save(final CompoundTag compoundTag) {
        super.save(compoundTag);

        compoundTag.putInt("blockId", this.id);
        compoundTag.putInt("blockData", this.data);
        compoundTag.putInt("facing", this.facing);
        compoundTag.putFloat("progress", this.progressO);
        compoundTag.putBoolean("extending", this.extending);
    }

}
