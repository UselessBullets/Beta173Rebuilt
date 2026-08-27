// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class FenceTile extends Tile
{
    public FenceTile(final int id, final int tex) {
        super(id, tex, Material.wood);
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.getTile(x, y - 1, z) == this.id || (level.getMaterial(x, y - 1, z).isSolid() && super.mayPlace(level, x, y, z));
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return AABB.newTemp(x, y, z, x + 1, y + 1.5f, z + 1);
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
    public int getRenderShape() {
        return Tile.SHAPE_FENCE;
    }
}
