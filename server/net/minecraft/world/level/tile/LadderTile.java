// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
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
        final int data = level.getData(x, y, z);
        final float n = 0.125f;
        if (data == 2) {
            this.setShape(0.0f, 0.0f, 1.0f - n, 1.0f, 1.0f, 1.0f);
        }
        if (data == 3) {
            this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, n);
        }
        if (data == 4) {
            this.setShape(1.0f - n, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
        if (data == 5) {
            this.setShape(0.0f, 0.0f, 0.0f, n, 1.0f, 1.0f);
        }
        return super.getAABB(level, x, y, z);
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
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x - 1, y, z) || level.isSolidBlockingTile(x + 1, y, z) || level.isSolidBlockingTile(x, y, z - 1) || level.isSolidBlockingTile(x, y, z + 1);
    }
    
    @Override
    public void setPlacedOnFace(final Level level, final int x, final int y, final int z, final int face) {
        int data = level.getData(x, y, z);
        if ((data == 0 || face == 2) && level.isSolidBlockingTile(x, y, z + 1)) {
            data = 2;
        }
        if ((data == 0 || face == 3) && level.isSolidBlockingTile(x, y, z - 1)) {
            data = 3;
        }
        if ((data == 0 || face == 4) && level.isSolidBlockingTile(x + 1, y, z)) {
            data = 4;
        }
        if ((data == 0 || face == 5) && level.isSolidBlockingTile(x - 1, y, z)) {
            data = 5;
        }
        level.setData(x, y, z, data);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        final int data = level.getData(x, y, z);
        boolean b = false;
        if (data == 2 && level.isSolidBlockingTile(x, y, z + 1)) {
            b = true;
        }
        if (data == 3 && level.isSolidBlockingTile(x, y, z - 1)) {
            b = true;
        }
        if (data == 4 && level.isSolidBlockingTile(x + 1, y, z)) {
            b = true;
        }
        if (data == 5 && level.isSolidBlockingTile(x - 1, y, z)) {
            b = true;
        }
        if (!b) {
            this.spawnResources(level, x, y, z, data);
            level.setTile(x, y, z, 0);
        }
        super.neighborChanged(level, x, y, z, type);
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 1;
    }
}
