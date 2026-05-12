// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.Random;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class TorchTile extends Tile
{
    protected TorchTile(final int id, final int tex) {
        super(id, tex, Material.decoration);
        this.setTicking(true);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public boolean isSolidRender() {
        return false;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    private boolean isConnection(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x, y, z) || level.getTile(x, y, z) == Tile.fence.id;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x - 1, y, z) || level.isSolidBlockingTile(x + 1, y, z) || level.isSolidBlockingTile(x, y, z - 1) || level.isSolidBlockingTile(x, y, z + 1) || this.isConnection(level, x, y - 1, z);
    }
    
    @Override
    public void setPlacedOnFace(final Level level, final int x, final int y, final int z, final int face) {
        int data = level.getData(x, y, z);
        if (face == 1 && this.isConnection(level, x, y - 1, z)) {
            data = 5;
        }
        if (face == 2 && level.isSolidBlockingTile(x, y, z + 1)) {
            data = 4;
        }
        if (face == 3 && level.isSolidBlockingTile(x, y, z - 1)) {
            data = 3;
        }
        if (face == 4 && level.isSolidBlockingTile(x + 1, y, z)) {
            data = 2;
        }
        if (face == 5 && level.isSolidBlockingTile(x - 1, y, z)) {
            data = 1;
        }
        level.setData(x, y, z, data);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        super.tick(level, x, y, z, random);
        if (level.getData(x, y, z) == 0) {
            this.onPlace(level, x, y, z);
        }
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        if (level.isSolidBlockingTile(x - 1, y, z)) {
            level.setData(x, y, z, 1);
        }
        else if (level.isSolidBlockingTile(x + 1, y, z)) {
            level.setData(x, y, z, 2);
        }
        else if (level.isSolidBlockingTile(x, y, z - 1)) {
            level.setData(x, y, z, 3);
        }
        else if (level.isSolidBlockingTile(x, y, z + 1)) {
            level.setData(x, y, z, 4);
        }
        else if (this.isConnection(level, x, y - 1, z)) {
            level.setData(x, y, z, 5);
        }
        this.checkCanSurvive(level, x, y, z);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (this.checkCanSurvive(level, x, y, z)) {
            final int data = level.getData(x, y, z);
            boolean b = false;
            if (!level.isSolidBlockingTile(x - 1, y, z) && data == 1) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x + 1, y, z) && data == 2) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x, y, z - 1) && data == 3) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x, y, z + 1) && data == 4) {
                b = true;
            }
            if (!this.isConnection(level, x, y - 1, z) && data == 5) {
                b = true;
            }
            if (b) {
                this.spawnResources(level, x, y, z, level.getData(x, y, z));
                level.setTile(x, y, z, 0);
            }
        }
    }
    
    private boolean checkCanSurvive(final Level level, final int x, final int y, final int z) {
        if (!this.mayPlace(level, x, y, z)) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
            return false;
        }
        return true;
    }
    
    @Override
    public HitResult clip(final Level level, final int xt, final int yt, final int zt, final Vec3 a, final Vec3 b) {
        final int n = level.getData(xt, yt, zt) & 0x7;
        final float n2 = 0.15f;
        if (n == 1) {
            this.setShape(0.0f, 0.2f, 0.5f - n2, n2 * 2.0f, 0.8f, 0.5f + n2);
        }
        else if (n == 2) {
            this.setShape(1.0f - n2 * 2.0f, 0.2f, 0.5f - n2, 1.0f, 0.8f, 0.5f + n2);
        }
        else if (n == 3) {
            this.setShape(0.5f - n2, 0.2f, 0.0f, 0.5f + n2, 0.8f, n2 * 2.0f);
        }
        else if (n == 4) {
            this.setShape(0.5f - n2, 0.2f, 1.0f - n2 * 2.0f, 0.5f + n2, 0.8f, 1.0f);
        }
        else {
            final float n3 = 0.1f;
            this.setShape(0.5f - n3, 0.0f, 0.5f - n3, 0.5f + n3, 0.6f, 0.5f + n3);
        }
        return super.clip(level, xt, yt, zt, a, b);
    }
}
