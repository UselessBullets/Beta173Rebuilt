// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.Facing;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class LadderTile extends Tile
{
    protected LadderTile(final int id, final int tex) {
        super(id, tex, Material.decoration);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        final int dir = level.getData(x, y, z);
        final float r = 2 / 16.0f;

        if (dir == 2) this.setShape(0.0f, 0.0f, 1.0f - r, 1.0f, 1.0f, 1.0f);
        if (dir == 3) this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, r);
        if (dir == 4) this.setShape(1.0f - r, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        if (dir == 5) this.setShape(0.0f, 0.0f, 0.0f, r, 1.0f, 1.0f);
        return super.getAABB(level, x, y, z);
    }
    
    @Override
    public AABB getTileAABB(final Level level, final int x, final int y, final int z) {
        final int dir = level.getData(x, y, z);
        final float r = 2 / 16.0f;

        if (dir == 2) this.setShape(0.0f, 0.0f, 1.0f - r, 1.0f, 1.0f, 1.0f);
        if (dir == 3) this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, r);
        if (dir == 4) this.setShape(1.0f - r, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        if (dir == 5) this.setShape(0.0f, 0.0f, 0.0f, r, 1.0f, 1.0f);
        return super.getTileAABB(level, x, y, z);
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
        return Tile.SHAPE_LADDER;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        if (level.isSolidBlockingTile(x - 1, y, z)) return true;
        if (level.isSolidBlockingTile(x + 1, y, z)) return true;
        if (level.isSolidBlockingTile(x, y, z - 1)) return true;
        if (level.isSolidBlockingTile(x, y, z + 1)) return true;
        return false;
    }
    
    @Override
    public void setPlacedOnFace(final Level level, final int x, final int y, final int z, final int face) {
        int dir = level.getData(x, y, z);

        if ((dir == 0 || face == 2) && level.isSolidBlockingTile(x, y, z + 1)) dir = 2;
        if ((dir == 0 || face == 3) && level.isSolidBlockingTile(x, y, z - 1)) dir = 3;
        if ((dir == 0 || face == 4) && level.isSolidBlockingTile(x + 1, y, z)) dir = 4;
        if ((dir == 0 || face == 5) && level.isSolidBlockingTile(x - 1, y, z)) dir = 5;

        level.setData(x, y, z, dir);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        final int face = level.getData(x, y, z);
        boolean ok = false;

        if (face == 2 && level.isSolidBlockingTile(x, y, z + 1)) ok = true;
        if (face == 3 && level.isSolidBlockingTile(x, y, z - 1)) ok = true;
        if (face == 4 && level.isSolidBlockingTile(x + 1, y, z)) ok = true;
        if (face == 5 && level.isSolidBlockingTile(x - 1, y, z)) ok = true;
        if (!ok) {
            this.spawnResources(level, x, y, z, face);
            level.setTile(x, y, z, 0);
        }

        super.neighborChanged(level, x, y, z, type);
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 1;
    }
}
