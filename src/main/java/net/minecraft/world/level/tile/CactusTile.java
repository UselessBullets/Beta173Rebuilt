// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class CactusTile extends Tile
{
    protected CactusTile(final int id, final int tex) {
        super(id, tex, Material.cactus);
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
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        final float n = 0.0625f;
        return AABB.newTemp(x + n, y, z + n, x + 1 - n, y + 1 - n, z + 1 - n);
    }
    
    @Override
    public AABB getTileAABB(final Level level, final int x, final int y, final int z) {
        final float n = 0.0625f;
        return AABB.newTemp(x + n, y, z + n, x + 1 - n, y + 1, z + 1 - n);
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == 1) {
            return this.tex - 1;
        }
        if (face == 0) {
            return this.tex + 1;
        }
        return this.tex;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public int getRenderShape() {
        return 13;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return super.mayPlace(level, x, y, z) && this.canSurvive(level, x, y, z);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (!this.canSurvive(level, x, y, z)) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
    }
    
    @Override
    public boolean canSurvive(final Level level, final int x, final int y, final int z) {
        if (level.getMaterial(x - 1, y, z).isSolid()) {
            return false;
        }
        if (level.getMaterial(x + 1, y, z).isSolid()) {
            return false;
        }
        if (level.getMaterial(x, y, z - 1).isSolid()) {
            return false;
        }
        if (level.getMaterial(x, y, z + 1).isSolid()) {
            return false;
        }
        final int tile = level.getTile(x, y - 1, z);
        return tile == Tile.cactus.id || tile == Tile.sand.id;
    }
    
    @Override
    public void entityInside(final Level level, final int x, final int y, final int z, final Entity entity) {
        entity.hurt(null, 1);
    }
}
