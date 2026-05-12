// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile.entity;

import java.util.ArrayList;
import com.mojang.nbt.CompoundTag;
import java.util.Iterator;
import net.minecraft.world.phys.AABB;
import net.minecraft.Facing;
import java.util.Collection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.tile.Tile;
import java.util.List;

public class PistonPieceEntity extends TileEntity
{
    private int id;
    private int data;
    private int facing;
    private boolean extending;
    private boolean isSourcePiston;
    private float progress;
    private float progressO;
    private static List collisionHolder;
    
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
    
    public float getProgress(float partialTick) {
        if (partialTick > 1.0f) {
            partialTick = 1.0f;
        }
        return this.progressO + (this.progress - this.progressO) * partialTick;
    }
    
    private void moveCollidedEntities(float progress, final float amount) {
        if (!this.extending) {
            --progress;
        }
        else {
            progress = 1.0f - progress;
        }
        final AABB aabb = Tile.pistonMovingPiece.getAABB(this.level, this.x, this.y, this.z, this.id, progress, this.facing);
        if (aabb != null) {
            final List entities = this.level.getEntities(null, aabb);
            if (!entities.isEmpty()) {
                PistonPieceEntity.collisionHolder.addAll(entities);
                final Iterator iterator = PistonPieceEntity.collisionHolder.iterator();
                while (iterator.hasNext()) {
                    ((Entity)iterator.next()).move(amount * Facing.STEP_X[this.facing], amount * Facing.STEP_Y[this.facing], amount * Facing.STEP_Z[this.facing]);
                }
                PistonPieceEntity.collisionHolder.clear();
            }
        }
    }
    
    public void finalTick() {
        if (this.progressO < 1.0f) {
            final float n = 1.0f;
            this.progress = n;
            this.progressO = n;
            this.level.removeTileEntity(this.x, this.y, this.z);
            this.setRemoved();
            if (this.level.getTile(this.x, this.y, this.z) == Tile.pistonMovingPiece.id) {
                this.level.setTileAndData(this.x, this.y, this.z, this.id, this.data);
            }
        }
    }
    
    @Override
    public void tick() {
        this.progressO = this.progress;
        if (this.progressO >= 1.0f) {
            this.moveCollidedEntities(1.0f, 0.25f);
            this.level.removeTileEntity(this.x, this.y, this.z);
            this.setRemoved();
            if (this.level.getTile(this.x, this.y, this.z) == Tile.pistonMovingPiece.id) {
                this.level.setTileAndData(this.x, this.y, this.z, this.id, this.data);
            }
            return;
        }
        this.progress += 0.5f;
        if (this.progress >= 1.0f) {
            this.progress = 1.0f;
        }
        if (this.extending) {
            this.moveCollidedEntities(this.progress, this.progress - this.progressO + 0.0625f);
        }
    }
    
    @Override
    public void load(final CompoundTag compoundTag) {
        super.load(compoundTag);
        this.id = compoundTag.getInt("blockId");
        this.data = compoundTag.getInt("blockData");
        this.facing = compoundTag.getInt("facing");
        final float float1 = compoundTag.getFloat("progress");
        this.progress = float1;
        this.progressO = float1;
        this.extending = compoundTag.getBoolean("extending");
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
    
    static {
        PistonPieceEntity.collisionHolder = new ArrayList();
    }
}
