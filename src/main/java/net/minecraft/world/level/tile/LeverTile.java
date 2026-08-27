// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;

public class LeverTile extends Tile
{
    protected LeverTile(final int id, final int tex) {
        super(id, tex, Material.decoration);
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
        return Tile.SHAPE_LEVER;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z, final int face) {
        return (face == 1 && level.isSolidBlockingTile(x, y - 1, z)) || (face == 2 && level.isSolidBlockingTile(x, y, z + 1)) || (face == 3 && level.isSolidBlockingTile(x, y, z - 1)) || (face == 4 && level.isSolidBlockingTile(x + 1, y, z)) || (face == 5 && level.isSolidBlockingTile(x - 1, y, z));
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x - 1, y, z) || level.isSolidBlockingTile(x + 1, y, z) || level.isSolidBlockingTile(x, y, z - 1) || level.isSolidBlockingTile(x, y, z + 1) || level.isSolidBlockingTile(x, y - 1, z);
    }
    
    @Override
    public void setPlacedOnFace(final Level level, final int x, final int y, final int z, final int face) {
        final int n = level.getData(x, y, z) & 0x8;
        int n2 = -1;
        if (face == 1 && level.isSolidBlockingTile(x, y - 1, z)) {
            n2 = 5 + level.random.nextInt(2);
        }
        if (face == 2 && level.isSolidBlockingTile(x, y, z + 1)) {
            n2 = 4;
        }
        if (face == 3 && level.isSolidBlockingTile(x, y, z - 1)) {
            n2 = 3;
        }
        if (face == 4 && level.isSolidBlockingTile(x + 1, y, z)) {
            n2 = 2;
        }
        if (face == 5 && level.isSolidBlockingTile(x - 1, y, z)) {
            n2 = 1;
        }
        if (n2 == -1) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
            return;
        }
        level.setData(x, y, z, n2 + n);
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (this.checkCanSurvive(level, x, y, z)) {
            final int n = level.getData(x, y, z) & 0x7;
            boolean b = false;
            if (!level.isSolidBlockingTile(x - 1, y, z) && n == 1) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x + 1, y, z) && n == 2) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x, y, z - 1) && n == 3) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x, y, z + 1) && n == 4) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x, y - 1, z) && n == 5) {
                b = true;
            }
            if (!level.isSolidBlockingTile(x, y - 1, z) && n == 6) {
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
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        final int n = level.getData(x, y, z) & 0x7;
        final float n2 = 0.1875f;
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
            final float n3 = 0.25f;
            this.setShape(0.5f - n3, 0.0f, 0.5f - n3, 0.5f + n3, 0.6f, 0.5f + n3);
        }
    }
    
    @Override
    public void attack(final Level level, final int x, final int y, final int z, final Player player) {
        this.use(level, x, y, z, player);
    }
    
    @Override
    public boolean use(final Level level, final int x, final int y, final int z, final Player player) {
        if (level.isClientSide) {
            return true;
        }
        final int data = level.getData(x, y, z);
        final int n = data & 0x7;
        final int n2 = 8 - (data & 0x8);
        level.setData(x, y, z, n + n2);
        level.setTilesDirty(x, y, z, x, y, z);
        level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3f, (n2 > 0) ? 0.6f : 0.5f);
        level.updateNeighborsAt(x, y, z, this.id);
        if (n == 1) {
            level.updateNeighborsAt(x - 1, y, z, this.id);
        }
        else if (n == 2) {
            level.updateNeighborsAt(x + 1, y, z, this.id);
        }
        else if (n == 3) {
            level.updateNeighborsAt(x, y, z - 1, this.id);
        }
        else if (n == 4) {
            level.updateNeighborsAt(x, y, z + 1, this.id);
        }
        else {
            level.updateNeighborsAt(x, y - 1, z, this.id);
        }
        return true;
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        if ((data & 0x8) > 0) {
            level.updateNeighborsAt(x, y, z, this.id);
            final int n = data & 0x7;
            if (n == 1) {
                level.updateNeighborsAt(x - 1, y, z, this.id);
            }
            else if (n == 2) {
                level.updateNeighborsAt(x + 1, y, z, this.id);
            }
            else if (n == 3) {
                level.updateNeighborsAt(x, y, z - 1, this.id);
            }
            else if (n == 4) {
                level.updateNeighborsAt(x, y, z + 1, this.id);
            }
            else {
                level.updateNeighborsAt(x, y - 1, z, this.id);
            }
        }
        super.onRemove(level, x, y, z);
    }
    
    @Override
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        return (level.getData(x, y, z) & 0x8) > 0;
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int dir) {
        final int data = level.getData(x, y, z);
        if ((data & 0x8) == 0x0) {
            return false;
        }
        final int n = data & 0x7;
        return (n == 6 && dir == 1) || (n == 5 && dir == 1) || (n == 4 && dir == 2) || (n == 3 && dir == 3) || (n == 2 && dir == 4) || (n == 1 && dir == 5);
    }
    
    @Override
    public boolean isSignalSource() {
        return true;
    }
}
