// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class ReedTile extends Tile
{
    protected ReedTile(final int id, final int tex) {
        super(id, Material.replaceable_plant);
        this.tex = tex;
        final float n = 0.375f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, 1.0f, 0.5f + n);
        this.setTicking(true);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (level.isEmptyTile(x, y + 1, z)) {
            int n;
            for (n = 1; level.getTile(x, y - n, z) == this.id; ++n) {}
            if (n < 3) {
                final int data = level.getData(x, y, z);
                if (data == 15) {
                    level.setTile(x, y + 1, z, this.id);
                    level.setData(x, y, z, 0);
                }
                else {
                    level.setData(x, y, z, data + 1);
                }
            }
        }
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        final int tile = level.getTile(x, y - 1, z);
        return tile == this.id || ((tile == Tile.grass.id || tile == Tile.dirt.id) && (level.getMaterial(x - 1, y - 1, z) == Material.water || level.getMaterial(x + 1, y - 1, z) == Material.water || level.getMaterial(x, y - 1, z - 1) == Material.water || level.getMaterial(x, y - 1, z + 1) == Material.water));
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        this.checkAlive(level, x, y, z);
    }
    
    protected final void checkAlive(final Level level, final int x, final int y, final int z) {
        if (!this.canSurvive(level, x, y, z)) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public boolean canSurvive(final Level level, final int x, final int y, final int z) {
        return this.mayPlace(level, x, y, z);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Item.reeds.id;
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
        return Tile.SHAPE_CROSS_TEXTURE;
    }
}
