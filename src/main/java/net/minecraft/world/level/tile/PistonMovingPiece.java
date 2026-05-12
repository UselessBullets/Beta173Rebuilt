// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelSource;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.tile.entity.PistonPieceEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.material.Material;

public class PistonMovingPiece extends EntityTile
{
    public PistonMovingPiece(final int id) {
        super(id, Material.piston);
        this.setDestroyTime(-1.0f);
    }
    
    @Override
    protected TileEntity newTileEntity() {
        return null;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        final TileEntity tileEntity = level.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof PistonPieceEntity) {
            ((PistonPieceEntity)tileEntity).finalTick();
        }
        else {
            super.onRemove(level, x, y, z);
        }
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return false;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z, final int face) {
        return false;
    }
    
    @Override
    public int getRenderShape() {
        return -1;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (!level.isClientSide && level.getTileEntity(x, y, z) == null) {
            level.setTile(x, y, z, 0);
            return true;
        }
        return false;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return 0;
    }
    
    @Override
    public void spawnResources(final Level level, final int x, final int y, final int z, final int data, final float odds) {
        if (level.isClientSide) {
            return;
        }
        final PistonPieceEntity entity = this.getEntity(level, x, y, z);
        if (entity == null) {
            return;
        }
        Tile.tiles[entity.getId()].spawnResources(level, x, y, z, entity.getData());
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (level.isClientSide || level.getTileEntity(x, y, z) == null) {}
    }
    
    public static TileEntity newMovingPieceEntity(final int block, final int data, final int facing, final boolean extending, final boolean isSourcePiston) {
        return new PistonPieceEntity(block, data, facing, extending, isSourcePiston);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        final PistonPieceEntity entity = this.getEntity(level, x, y, z);
        if (entity == null) {
            return null;
        }
        float progress = entity.getProgress(0.0f);
        if (entity.isExtending()) {
            progress = 1.0f - progress;
        }
        return this.getAABB(level, x, y, z, entity.getId(), progress, entity.getFacing());
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        final PistonPieceEntity entity = this.getEntity(level, x, y, z);
        if (entity != null) {
            final Tile tile = Tile.tiles[entity.getId()];
            if (tile == null || tile == this) {
                return;
            }
            tile.updateShape(level, x, y, z);
            float progress = entity.getProgress(0.0f);
            if (entity.isExtending()) {
                progress = 1.0f - progress;
            }
            final int facing = entity.getFacing();
            this.xx0 = tile.xx0 - Facing.STEP_X[facing] * progress;
            this.yy0 = tile.yy0 - Facing.STEP_Y[facing] * progress;
            this.zz0 = tile.zz0 - Facing.STEP_Z[facing] * progress;
            this.xx1 = tile.xx1 - Facing.STEP_X[facing] * progress;
            this.yy1 = tile.yy1 - Facing.STEP_Y[facing] * progress;
            this.zz1 = tile.zz1 - Facing.STEP_Z[facing] * progress;
        }
    }
    
    public AABB getAABB(final Level level, final int x, final int y, final int z, final int tile, final float progress, final int facing) {
        if (tile == 0 || tile == this.id) {
            return null;
        }
        final AABB aabb = Tile.tiles[tile].getAABB(level, x, y, z);
        if (aabb == null) {
            return null;
        }
        final AABB aabb2 = aabb;
        aabb2.x0 -= Facing.STEP_X[facing] * progress;
        final AABB aabb3 = aabb;
        aabb3.x1 -= Facing.STEP_X[facing] * progress;
        final AABB aabb4 = aabb;
        aabb4.y0 -= Facing.STEP_Y[facing] * progress;
        final AABB aabb5 = aabb;
        aabb5.y1 -= Facing.STEP_Y[facing] * progress;
        final AABB aabb6 = aabb;
        aabb6.z0 -= Facing.STEP_Z[facing] * progress;
        final AABB aabb7 = aabb;
        aabb7.z1 -= Facing.STEP_Z[facing] * progress;
        return aabb;
    }
    
    private PistonPieceEntity getEntity(final LevelSource level, final int x, final int y, final int z) {
        final TileEntity tileEntity = level.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof PistonPieceEntity) {
            return (PistonPieceEntity)tileEntity;
        }
        return null;
    }
}
