// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.Direction;
import net.minecraft.world.item.Item;
import java.util.Random;
import net.minecraft.world.level.TilePos;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import java.util.HashSet;
import net.minecraft.world.level.material.Material;
import java.util.Set;

public class RedStoneDustTile extends Tile
{
    private boolean shouldSignal;
    private Set toUpdate;
    
    public RedStoneDustTile(final int id, final int tex) {
        super(id, tex, Material.decoration);
        this.shouldSignal = true;
        this.toUpdate = new HashSet();
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.0625f, 1.0f);
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        return this.tex;
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
        return Tile.SHAPE_RED_DUST;
    }
    
    @Override
    public int getColor(final LevelSource level, final int x, final int y, final int z) {
        return 8388608;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x, y - 1, z);
    }
    
    private void updatePowerStrength(final Level level, final int x, final int y, final int z) {
        this.updatePowerStrength(level, x, y, z, x, y, z);
        final ArrayList list = new ArrayList(this.toUpdate);
        this.toUpdate.clear();
        for (int i = 0; i < list.size(); ++i) {
            final TilePos tilePos = (TilePos)list.get(i);
            level.updateNeighborsAt(tilePos.x, tilePos.y, tilePos.z, this.id);
        }
    }
    
    private void updatePowerStrength(final Level level, final int x, final int y, final int z, final int xFrom, final int yFrom, final int zFrom) {
        final int data = level.getData(x, y, z);
        int n = 0;
        this.shouldSignal = false;
        final boolean hasNeighborSignal = level.hasNeighborSignal(x, y, z);
        this.shouldSignal = true;
        if (hasNeighborSignal) {
            n = 15;
        }
        else {
            for (int i = 0; i < 4; ++i) {
                int x2 = x;
                int z2 = z;
                if (i == 0) {
                    --x2;
                }
                if (i == 1) {
                    ++x2;
                }
                if (i == 2) {
                    --z2;
                }
                if (i == 3) {
                    ++z2;
                }
                if (x2 != xFrom || y != yFrom || z2 != zFrom) {
                    n = this.checkTarget(level, x2, y, z2, n);
                }
                if (level.isSolidBlockingTile(x2, y, z2) && !level.isSolidBlockingTile(x, y + 1, z)) {
                    if (x2 != xFrom || y + 1 != yFrom || z2 != zFrom) {
                        n = this.checkTarget(level, x2, y + 1, z2, n);
                    }
                }
                else if (!level.isSolidBlockingTile(x2, y, z2) && (x2 != xFrom || y - 1 != yFrom || z2 != zFrom)) {
                    n = this.checkTarget(level, x2, y - 1, z2, n);
                }
            }
            if (n > 0) {
                --n;
            }
            else {
                n = 0;
            }
        }
        if (data != n) {
            level.noNeighborUpdate = true;
            level.setData(x, y, z, n);
            level.setTilesDirty(x, y, z, x, y, z);
            level.noNeighborUpdate = false;
            for (int j = 0; j < 4; ++j) {
                int x3 = x;
                int z3 = z;
                int n2 = y - 1;
                if (j == 0) {
                    --x3;
                }
                if (j == 1) {
                    ++x3;
                }
                if (j == 2) {
                    --z3;
                }
                if (j == 3) {
                    ++z3;
                }
                if (level.isSolidBlockingTile(x3, y, z3)) {
                    n2 += 2;
                }
                final int checkTarget = this.checkTarget(level, x3, y, z3, -1);
                int data2 = level.getData(x, y, z);
                if (data2 > 0) {
                    --data2;
                }
                if (checkTarget >= 0 && checkTarget != data2) {
                    this.updatePowerStrength(level, x3, y, z3, x, y, z);
                }
                final int checkTarget2 = this.checkTarget(level, x3, n2, z3, -1);
                n = level.getData(x, y, z);
                if (n > 0) {
                    --n;
                }
                if (checkTarget2 >= 0 && checkTarget2 != n) {
                    this.updatePowerStrength(level, x3, n2, z3, x, y, z);
                }
            }
            if (data == 0 || n == 0) {
                this.toUpdate.add(new TilePos(x, y, z));
                this.toUpdate.add(new TilePos(x - 1, y, z));
                this.toUpdate.add(new TilePos(x + 1, y, z));
                this.toUpdate.add(new TilePos(x, y - 1, z));
                this.toUpdate.add(new TilePos(x, y + 1, z));
                this.toUpdate.add(new TilePos(x, y, z - 1));
                this.toUpdate.add(new TilePos(x, y, z + 1));
            }
        }
    }
    
    private void checkCornerChangeAt(final Level level, final int x, final int y, final int z) {
        if (level.getTile(x, y, z) != this.id) {
            return;
        }
        level.updateNeighborsAt(x, y, z, this.id);
        level.updateNeighborsAt(x - 1, y, z, this.id);
        level.updateNeighborsAt(x + 1, y, z, this.id);
        level.updateNeighborsAt(x, y, z - 1, this.id);
        level.updateNeighborsAt(x, y, z + 1, this.id);
        level.updateNeighborsAt(x, y - 1, z, this.id);
        level.updateNeighborsAt(x, y + 1, z, this.id);
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        super.onPlace(level, x, y, z);
        if (level.isClientSide) {
            return;
        }
        this.updatePowerStrength(level, x, y, z);
        level.updateNeighborsAt(x, y + 1, z, this.id);
        level.updateNeighborsAt(x, y - 1, z, this.id);
        this.checkCornerChangeAt(level, x - 1, y, z);
        this.checkCornerChangeAt(level, x + 1, y, z);
        this.checkCornerChangeAt(level, x, y, z - 1);
        this.checkCornerChangeAt(level, x, y, z + 1);
        if (level.isSolidBlockingTile(x - 1, y, z)) {
            this.checkCornerChangeAt(level, x - 1, y + 1, z);
        }
        else {
            this.checkCornerChangeAt(level, x - 1, y - 1, z);
        }
        if (level.isSolidBlockingTile(x + 1, y, z)) {
            this.checkCornerChangeAt(level, x + 1, y + 1, z);
        }
        else {
            this.checkCornerChangeAt(level, x + 1, y - 1, z);
        }
        if (level.isSolidBlockingTile(x, y, z - 1)) {
            this.checkCornerChangeAt(level, x, y + 1, z - 1);
        }
        else {
            this.checkCornerChangeAt(level, x, y - 1, z - 1);
        }
        if (level.isSolidBlockingTile(x, y, z + 1)) {
            this.checkCornerChangeAt(level, x, y + 1, z + 1);
        }
        else {
            this.checkCornerChangeAt(level, x, y - 1, z + 1);
        }
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        super.onRemove(level, x, y, z);
        if (level.isClientSide) {
            return;
        }
        level.updateNeighborsAt(x, y + 1, z, this.id);
        level.updateNeighborsAt(x, y - 1, z, this.id);
        this.updatePowerStrength(level, x, y, z);
        this.checkCornerChangeAt(level, x - 1, y, z);
        this.checkCornerChangeAt(level, x + 1, y, z);
        this.checkCornerChangeAt(level, x, y, z - 1);
        this.checkCornerChangeAt(level, x, y, z + 1);
        if (level.isSolidBlockingTile(x - 1, y, z)) {
            this.checkCornerChangeAt(level, x - 1, y + 1, z);
        }
        else {
            this.checkCornerChangeAt(level, x - 1, y - 1, z);
        }
        if (level.isSolidBlockingTile(x + 1, y, z)) {
            this.checkCornerChangeAt(level, x + 1, y + 1, z);
        }
        else {
            this.checkCornerChangeAt(level, x + 1, y - 1, z);
        }
        if (level.isSolidBlockingTile(x, y, z - 1)) {
            this.checkCornerChangeAt(level, x, y + 1, z - 1);
        }
        else {
            this.checkCornerChangeAt(level, x, y - 1, z - 1);
        }
        if (level.isSolidBlockingTile(x, y, z + 1)) {
            this.checkCornerChangeAt(level, x, y + 1, z + 1);
        }
        else {
            this.checkCornerChangeAt(level, x, y - 1, z + 1);
        }
    }
    
    private int checkTarget(final Level level, final int x, final int y, final int z, final int target) {
        if (level.getTile(x, y, z) != this.id) {
            return target;
        }
        final int data = level.getData(x, y, z);
        if (data > target) {
            return data;
        }
        return target;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (level.isClientSide) {
            return;
        }
        final int data = level.getData(x, y, z);
        if (!this.mayPlace(level, x, y, z)) {
            this.spawnResources(level, x, y, z, data);
            level.setTile(x, y, z, 0);
        }
        else {
            this.updatePowerStrength(level, x, y, z);
        }
        super.neighborChanged(level, x, y, z, type);
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Item.redStone.id;
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int dir) {
        return this.shouldSignal && this.getSignal(level, x, y, z, dir);
    }
    
    @Override
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        if (!this.shouldSignal) {
            return false;
        }
        if (level.getData(x, y, z) == 0) {
            return false;
        }
        if (dir == 1) {
            return true;
        }
        boolean b = shouldReceivePowerFrom(level, x - 1, y, z, 1) || (!level.isSolidBlockingTile(x - 1, y, z) && shouldReceivePowerFrom(level, x - 1, y - 1, z, -1));
        boolean b2 = shouldReceivePowerFrom(level, x + 1, y, z, 3) || (!level.isSolidBlockingTile(x + 1, y, z) && shouldReceivePowerFrom(level, x + 1, y - 1, z, -1));
        boolean b3 = shouldReceivePowerFrom(level, x, y, z - 1, 2) || (!level.isSolidBlockingTile(x, y, z - 1) && shouldReceivePowerFrom(level, x, y - 1, z - 1, -1));
        boolean b4 = shouldReceivePowerFrom(level, x, y, z + 1, 0) || (!level.isSolidBlockingTile(x, y, z + 1) && shouldReceivePowerFrom(level, x, y - 1, z + 1, -1));
        if (!level.isSolidBlockingTile(x, y + 1, z)) {
            if (level.isSolidBlockingTile(x - 1, y, z) && shouldReceivePowerFrom(level, x - 1, y + 1, z, -1)) {
                b = true;
            }
            if (level.isSolidBlockingTile(x + 1, y, z) && shouldReceivePowerFrom(level, x + 1, y + 1, z, -1)) {
                b2 = true;
            }
            if (level.isSolidBlockingTile(x, y, z - 1) && shouldReceivePowerFrom(level, x, y + 1, z - 1, -1)) {
                b3 = true;
            }
            if (level.isSolidBlockingTile(x, y, z + 1) && shouldReceivePowerFrom(level, x, y + 1, z + 1, -1)) {
                b4 = true;
            }
        }
        return (!b3 && !b2 && !b && !b4 && dir >= 2 && dir <= 5) || (dir == 2 && b3 && !b && !b2) || (dir == 3 && b4 && !b && !b2) || (dir == 4 && b && !b3 && !b4) || (dir == 5 && b2 && !b3 && !b4);
    }
    
    @Override
    public boolean isSignalSource() {
        return this.shouldSignal;
    }
    
    @Override
    public void animateTick(final Level level, final int x, final int y, final int z, final Random random) {
        final int data = level.getData(x, y, z);
        if (data > 0) {
            final double x2 = x + 0.5 + (random.nextFloat() - 0.5) * 0.2;
            final double y2 = y + 0.0625f;
            final double z2 = z + 0.5 + (random.nextFloat() - 0.5) * 0.2;
            final float n = data / 15.0f;
            float n2 = n * 0.6f + 0.4f;
            if (data == 0) {
                n2 = 0.0f;
            }
            float n3 = n * n * 0.7f - 0.5f;
            float n4 = n * n * 0.6f - 0.7f;
            if (n3 < 0.0f) {
                n3 = 0.0f;
            }
            if (n4 < 0.0f) {
                n4 = 0.0f;
            }
            level.addParticle("reddust", x2, y2, z2, n2, n3, n4);
        }
    }
    
    public static boolean shouldReceivePowerFrom(final LevelSource level, final int x, final int y, final int z, final int direction) {
        final int tile = level.getTile(x, y, z);
        return tile == Tile.redStoneDust.id || (tile != 0 && (Tile.tiles[tile].isSignalSource() || ((tile == Tile.diode_off.id || tile == Tile.diode_on.id) && direction == Direction.DIRECTION_OPPOSITE[level.getData(x, y, z) & 0x3])));
    }
}
