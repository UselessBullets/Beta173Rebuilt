// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Facing;
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
            int height = 1;
            while (level.getTile(x, y - height, z) == this.id) {
                height++;
            }
            if (height < 3) {
                final int age = level.getData(x, y, z);
                if (age == 15) {
                    level.setTile(x, y + 1, z, this.id);
                    level.setData(x, y, z, 0);
                }
                else {
                    level.setData(x, y, z, age + 1);
                }
            }
        }
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        final float r = 1 / 16.0f;
        return AABB.newTemp(x + r, y, z + r, x + 1 - r, y + 1 - r, z + 1 - r);
    }
    
    @Override
    public AABB getTileAABB(final Level level, final int x, final int y, final int z) {
        final float r = 1 / 16.0f;
        return AABB.newTemp(x + r, y, z + r, x + 1 - r, y + 1, z + 1 - r);
    }
    
    @Override
    public int getTexture(final int face) {
        if (face == Facing.UP) return this.tex - 1;
        if (face == Facing.DOWN) return this.tex + 1;
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
        return Tile.SHAPE_CACTUS;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        if (!super.mayPlace(level, x, y, z)) return false;

        return this.canSurvive(level, x, y, z);
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
        if (level.getMaterial(x - 1, y, z).isSolid()) return false;
        if (level.getMaterial(x + 1, y, z).isSolid()) return false;
        if (level.getMaterial(x, y, z - 1).isSolid()) return false;
        if (level.getMaterial(x, y, z + 1).isSolid()) return false;
        final int below = level.getTile(x, y - 1, z);
        return below == Tile.cactus.id || below == Tile.sand.id;
    }
    
    @Override
    public void entityInside(final Level level, final int x, final int y, final int z, final Entity entity) {
        entity.hurt(null, 1);
    }
}
