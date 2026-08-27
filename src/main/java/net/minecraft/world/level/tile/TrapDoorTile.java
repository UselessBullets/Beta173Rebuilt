// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class TrapDoorTile extends Tile
{
    protected TrapDoorTile(final int id, final Material material) {
        super(id, material);
        this.tex = 84;
        if (material == Material.metal) {
            ++this.tex;
        }
        final float n = 0.5f;
        this.setShape(0.5f - n, 0.0f, 0.5f - n, 0.5f + n, 1.0f, 0.5f + n);
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
        return Tile.SHAPE_BLOCK;
    }
    
    @Override
    public AABB getTileAABB(final Level level, final int x, final int y, final int z) {
        this.updateShape(level, x, y, z);
        return super.getTileAABB(level, x, y, z);
    }
    
    @Override
    public AABB getAABB(final Level level, final int x, final int y, final int z) {
        this.updateShape(level, x, y, z);
        return super.getAABB(level, x, y, z);
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        this.setShape(level.getData(x, y, z));
    }
    
    @Override
    public void updateDefaultShape() {
        final float n = 0.1875f;
        this.setShape(0.0f, 0.5f - n / 2.0f, 0.0f, 1.0f, 0.5f + n / 2.0f, 1.0f);
    }
    
    public void setShape(final int data) {
        final float x1 = 0.1875f;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, x1, 1.0f);
        if (isOpen(data)) {
            if ((data & 0x3) == 0x0) {
                this.setShape(0.0f, 0.0f, 1.0f - x1, 1.0f, 1.0f, 1.0f);
            }
            if ((data & 0x3) == 0x1) {
                this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, x1);
            }
            if ((data & 0x3) == 0x2) {
                this.setShape(1.0f - x1, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            }
            if ((data & 0x3) == 0x3) {
                this.setShape(0.0f, 0.0f, 0.0f, x1, 1.0f, 1.0f);
            }
        }
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        this.use(level, x, y, z, player);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (this.material == Material.metal) {
            return true;
        }
        level.setData(x, y, z, level.getData(x, y, z) ^ 0x4);
        level.levelEvent(player, 1003, x, y, z, 0);
        return true;
    }
    
    public void setOpen(final Level level, final int x, final int y, final int z, final boolean shouldOpen) {
        final int data = level.getData(x, y, z);
        if ((data & 0x4) > 0 == shouldOpen) {
            return;
        }
        level.setData(x, y, z, data ^ 0x4);
        level.levelEvent(null, 1003, x, y, z, 0);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (level.isClientSide) {
            return;
        }
        final int data = level.getData(x, y, z);
        int x2 = x;
        int z2 = z;
        if ((data & 0x3) == 0x0) {
            ++z2;
        }
        if ((data & 0x3) == 0x1) {
            --z2;
        }
        if ((data & 0x3) == 0x2) {
            ++x2;
        }
        if ((data & 0x3) == 0x3) {
            --x2;
        }
        if (!level.isSolidBlockingTile(x2, y, z2)) {
            level.setTile(x, y, z, 0);
            this.spawnResources(level, x, y, z, data);
        }
        if (type > 0 && Tile.tiles[type].isSignalSource()) {
            this.setOpen(level, x, y, z, level.hasNeighborSignal(x, y, z));
        }
    }
    
    @Override
    public HitResult clip(final Level level, final int xt, final int yt, final int zt, final Vec3 a, final Vec3 b) {
        this.updateShape(level, xt, yt, zt);
        return super.clip(level, xt, yt, zt, a, b);
    }
    
    @Override
    public void setPlacedOnFace(final Level level, final int x, final int y, final int z, final int face) {
        int data = 0;
        if (face == 2) {
            data = 0;
        }
        if (face == 3) {
            data = 1;
        }
        if (face == 4) {
            data = 2;
        }
        if (face == 5) {
            data = 3;
        }
        level.setData(x, y, z, data);
    }
    
    @Override
    public boolean mayPlace(final Level level, int x, final int y, int z, final int face) {
        if (face == 0) {
            return false;
        }
        if (face == 1) {
            return false;
        }
        if (face == 2) {
            ++z;
        }
        if (face == 3) {
            --z;
        }
        if (face == 4) {
            ++x;
        }
        if (face == 5) {
            --x;
        }
        return level.isSolidBlockingTile(x, y, z);
    }
    
    public static boolean isOpen(final int data) {
        return (data & 0x4) != 0x0;
    }
}
