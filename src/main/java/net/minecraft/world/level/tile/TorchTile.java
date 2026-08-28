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
    
    @Override
    public int getRenderShape() {
        return Tile.SHAPE_TORCH;
    }
    
    private boolean isConnection(final Level level, final int x, final int y, final int z) {
        if (level.isSolidBlockingTile(x, y, z)) return true;
        int tile = level.getTile(x, y, z);
        if (tile == Tile.fence.id) {
            return true;
        }
        return false;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        if (level.isSolidBlockingTile(x - 1, y, z)) return true;
        if (level.isSolidBlockingTile(x + 1, y, z)) return true;
        if (level.isSolidBlockingTile(x, y, z - 1)) return true;
        if (level.isSolidBlockingTile(x, y, z + 1)) return true;
        if (this.isConnection(level, x, y - 1, z)) return true;
        return false;
    }
    
    @Override
    public void setPlacedOnFace(final Level level, final int x, final int y, final int z, final int face) {
        int dir = level.getData(x, y, z);

        if (face == 1 && this.isConnection(level, x, y - 1, z)) dir = 5;
        if (face == 2 && level.isSolidBlockingTile(x, y, z + 1)) dir = 4;
        if (face == 3 && level.isSolidBlockingTile(x, y, z - 1)) dir = 3;
        if (face == 4 && level.isSolidBlockingTile(x + 1, y, z)) dir = 2;
        if (face == 5 && level.isSolidBlockingTile(x - 1, y, z)) dir = 1;

        level.setData(x, y, z, dir);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        super.tick(level, x, y, z, random);
        if (level.getData(x, y, z) == 0) this.onPlace(level, x, y, z);
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
            final int dir = level.getData(x, y, z);
            boolean replace = false;

            if (!level.isSolidBlockingTile(x - 1, y, z) && dir == 1) replace = true;
            if (!level.isSolidBlockingTile(x + 1, y, z) && dir == 2) replace = true;
            if (!level.isSolidBlockingTile(x, y, z - 1) && dir == 3) replace = true;
            if (!level.isSolidBlockingTile(x, y, z + 1) && dir == 4) replace = true;
            if (!this.isConnection(level, x, y - 1, z) && dir == 5) replace = true;

            if (replace) {
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
        final int dir = level.getData(xt, yt, zt) & 0x7;

        float r = 0.15f;
        if (dir == 1) {
            this.setShape(0.0f, 0.2f, 0.5f - r, r * 2.0f, 0.8f, 0.5f + r);
        }
        else if (dir == 2) {
            this.setShape(1.0f - r * 2.0f, 0.2f, 0.5f - r, 1.0f, 0.8f, 0.5f + r);
        }
        else if (dir == 3) {
            this.setShape(0.5f - r, 0.2f, 0.0f, 0.5f + r, 0.8f, r * 2.0f);
        }
        else if (dir == 4) {
            this.setShape(0.5f - r, 0.2f, 1.0f - r * 2.0f, 0.5f + r, 0.8f, 1.0f);
        }
        else {
            r = 0.1f;
            this.setShape(0.5f - r, 0.0f, 0.5f - r, 0.5f + r, 0.6f, 0.5f + r);
        }
        return super.clip(level, xt, yt, zt, a, b);
    }
    
    @Override
    public void animateTick(final Level level, final int xt, final int yt, final int zt, final Random random) {
        final int data = level.getData(xt, yt, zt);
        final double x = xt + 0.5f;
        final double y = yt + 0.7f;
        final double z = zt + 0.5f;
        final double h = 0.22f;
        final double r = 0.27f;
        if (data == 1) {
            level.addParticle("smoke", x - r, y + h, z, 0.0, 0.0, 0.0);
            level.addParticle("flame", x - r, y + h, z, 0.0, 0.0, 0.0);
        }
        else if (data == 2) {
            level.addParticle("smoke", x + r, y + h, z, 0.0, 0.0, 0.0);
            level.addParticle("flame", x + r, y + h, z, 0.0, 0.0, 0.0);
        }
        else if (data == 3) {
            level.addParticle("smoke", x, y + h, z - r, 0.0, 0.0, 0.0);
            level.addParticle("flame", x, y + h, z - r, 0.0, 0.0, 0.0);
        }
        else if (data == 4) {
            level.addParticle("smoke", x, y + h, z + r, 0.0, 0.0, 0.0);
            level.addParticle("flame", x, y + h, z + r, 0.0, 0.0, 0.0);
        }
        else {
            level.addParticle("smoke", x, y, z, 0.0, 0.0, 0.0);
            level.addParticle("flame", x, y, z, 0.0, 0.0, 0.0);
        }
    }
}
