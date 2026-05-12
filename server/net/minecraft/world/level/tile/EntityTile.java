// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public abstract class EntityTile extends Tile
{
    protected EntityTile(final int id, final Material material) {
        super(id, material);
        EntityTile.isEntityTile[id] = true;
    }
    
    protected EntityTile(final int id, final int tex, final Material material) {
        super(id, tex, material);
        EntityTile.isEntityTile[id] = true;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        super.onPlace(level, x, y, z);
        level.setTileEntity(x, y, z, this.newTileEntity());
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        super.onRemove(level, x, y, z);
        level.removeTileEntity(x, y, z);
    }
    
    protected abstract TileEntity newTileEntity();
}
