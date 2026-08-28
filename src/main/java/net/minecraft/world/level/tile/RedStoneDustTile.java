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
    private boolean shouldSignal = true;
    private Set<TilePos> toUpdate = new HashSet<>();
    
    public RedStoneDustTile(final int id, final int tex) {
        super(id, tex, Material.decoration);
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1 / 16.0f, 1.0f);
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
        return 0x800000;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x, y - 1, z);
    }
    
    private void updatePowerStrength(final Level level, final int x, final int y, final int z) {
        this.updatePowerStrength(level, x, y, z, x, y, z);

        final ArrayList<TilePos> updates = new ArrayList<>(this.toUpdate);
        this.toUpdate.clear();

        for (int i = 0; i < updates.size(); ++i) {
            final TilePos tp = updates.get(i);
            level.updateNeighborsAt(tp.x, tp.y, tp.z, this.id);
        }
    }
    
    private void updatePowerStrength(final Level level, final int x, final int y, final int z, final int xFrom, final int yFrom, final int zFrom) {
        final int old = level.getData(x, y, z);
        int target = 0;

        this.shouldSignal = false;
        final boolean hasNeighborSignal = level.hasNeighborSignal(x, y, z);
        this.shouldSignal = true;

        if (hasNeighborSignal) {
            target = 15;
        }
        else {
            for (int i = 0; i < 4; ++i) {
                int xt = x;
                int zt = z;
                if (i == 0) xt--;
                if (i == 1) xt++;
                if (i == 2) zt--;
                if (i == 3) zt++;

                if (xt != xFrom || y != yFrom || zt != zFrom) target = this.checkTarget(level, xt, y, zt, target);
                if (level.isSolidBlockingTile(xt, y, zt) && !level.isSolidBlockingTile(x, y + 1, z)) {
                    if (xt != xFrom || y + 1 != yFrom || zt != zFrom) target = this.checkTarget(level, xt, y + 1, zt, target);
                }
                else if (!level.isSolidBlockingTile(xt, y, zt)) {
                    if (xt != xFrom || y - 1 != yFrom || zt != zFrom) target = this.checkTarget(level, xt, y - 1, zt, target);
                }
            }
            if (target > 0) --target;
            else target = 0;
        }

        if (old != target) {
            level.noNeighborUpdate = true;
            level.setData(x, y, z, target);
            level.setTilesDirty(x, y, z, x, y, z);
            level.noNeighborUpdate = false;

            for (int i = 0; i < 4; ++i) {
                int xt = x;
                int zt = z;
                int yt = y - 1;
                if (i == 0) xt--;
                if (i == 1) xt++;
                if (i == 2) zt--;
                if (i == 3) zt++;

                if (level.isSolidBlockingTile(xt, y, zt)) yt += 2;

                int current = 0;
                current = this.checkTarget(level, xt, y, zt, -1);
                target = level.getData(x, y, z);
                if (target > 0) target--;
                if (current >= 0 && current != target) {
                    this.updatePowerStrength(level, xt, y, zt, x, y, z);
                }
                current = this.checkTarget(level, xt, yt, zt, -1);
                target = level.getData(x, y, z);
                if (target > 0) target--;
                if (current >= 0 && current != target) {
                    this.updatePowerStrength(level, xt, yt, zt, x, y, z);
                }
            }

            if (old == 0 || target == 0) {
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
        if (level.getTile(x, y, z) != this.id) return;

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
        if (level.isClientSide) return;

        this.updatePowerStrength(level, x, y, z);
        level.updateNeighborsAt(x, y + 1, z, this.id);
        level.updateNeighborsAt(x, y - 1, z, this.id);

        this.checkCornerChangeAt(level, x - 1, y, z);
        this.checkCornerChangeAt(level, x + 1, y, z);
        this.checkCornerChangeAt(level, x, y, z - 1);
        this.checkCornerChangeAt(level, x, y, z + 1);

        if (level.isSolidBlockingTile(x - 1, y, z)) this.checkCornerChangeAt(level, x - 1, y + 1, z);
        else this.checkCornerChangeAt(level, x - 1, y - 1, z);
        if (level.isSolidBlockingTile(x + 1, y, z)) this.checkCornerChangeAt(level, x + 1, y + 1, z);
        else this.checkCornerChangeAt(level, x + 1, y - 1, z);
        if (level.isSolidBlockingTile(x, y, z - 1)) this.checkCornerChangeAt(level, x, y + 1, z - 1);
        else this.checkCornerChangeAt(level, x, y - 1, z - 1);
        if (level.isSolidBlockingTile(x, y, z + 1)) this.checkCornerChangeAt(level, x, y + 1, z + 1);
        else this.checkCornerChangeAt(level, x, y - 1, z + 1);
    }
    
    @Override
    public void onRemove(final Level level, final int x, final int y, final int z) {
        super.onRemove(level, x, y, z);
        if (level.isClientSide) return;

        level.updateNeighborsAt(x, y + 1, z, this.id);
        level.updateNeighborsAt(x, y - 1, z, this.id);
        this.updatePowerStrength(level, x, y, z);

        this.checkCornerChangeAt(level, x - 1, y, z);
        this.checkCornerChangeAt(level, x + 1, y, z);
        this.checkCornerChangeAt(level, x, y, z - 1);
        this.checkCornerChangeAt(level, x, y, z + 1);

        if (level.isSolidBlockingTile(x - 1, y, z)) this.checkCornerChangeAt(level, x - 1, y + 1, z);
        else this.checkCornerChangeAt(level, x - 1, y - 1, z);
        if (level.isSolidBlockingTile(x + 1, y, z)) this.checkCornerChangeAt(level, x + 1, y + 1, z);
        else this.checkCornerChangeAt(level, x + 1, y - 1, z);
        if (level.isSolidBlockingTile(x, y, z - 1)) this.checkCornerChangeAt(level, x, y + 1, z - 1);
        else this.checkCornerChangeAt(level, x, y - 1, z - 1);
        if (level.isSolidBlockingTile(x, y, z + 1)) this.checkCornerChangeAt(level, x, y + 1, z + 1);
        else this.checkCornerChangeAt(level, x, y - 1, z + 1);
    }
    
    private int checkTarget(final Level level, final int x, final int y, final int z, final int target) {
        if (level.getTile(x, y, z) != this.id) return target;
        final int d = level.getData(x, y, z);
        if (d > target) return d;
        return target;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (level.isClientSide) return;
        final int face = level.getData(x, y, z);

        boolean ok = this.mayPlace(level, x, y, z);

        if (ok) {
            this.updatePowerStrength(level, x, y, z);
        } else {
            this.spawnResources(level, x, y, z, face);
            level.setTile(x, y, z, 0);
        }

        super.neighborChanged(level, x, y, z, type);
    }
    
    @Override
    public int getResource(final int data, final Random random) {
        return Item.redStone.id;
    }
    
    @Override
    public boolean getDirectSignal(final Level level, final int x, final int y, final int z, final int dir) {
        if (!this.shouldSignal) return false;
        return this.getSignal(level, x, y, z, dir);
    }
    
    @Override
    public boolean getSignal(final LevelSource level, final int x, final int y, final int z, final int dir) {
        if (!this.shouldSignal) return false;
        if (level.getData(x, y, z) == 0) return false;

        if (dir == 1) return true;

        boolean w = shouldReceivePowerFrom(level, x - 1, y, z, Direction.WEST) || (!level.isSolidBlockingTile(x - 1, y, z) && shouldReceivePowerFrom(level, x - 1, y - 1, z, Direction.UNDEFINED));
        boolean e = shouldReceivePowerFrom(level, x + 1, y, z, Direction.EAST) || (!level.isSolidBlockingTile(x + 1, y, z) && shouldReceivePowerFrom(level, x + 1, y - 1, z, Direction.UNDEFINED));
        boolean n = shouldReceivePowerFrom(level, x, y, z - 1, Direction.NORTH) || (!level.isSolidBlockingTile(x, y, z - 1) && shouldReceivePowerFrom(level, x, y - 1, z - 1, Direction.UNDEFINED));
        boolean s = shouldReceivePowerFrom(level, x, y, z + 1, Direction.SOUTH) || (!level.isSolidBlockingTile(x, y, z + 1) && shouldReceivePowerFrom(level, x, y - 1, z + 1, Direction.UNDEFINED));

        if (!level.isSolidBlockingTile(x, y + 1, z)) {
            if (level.isSolidBlockingTile(x - 1, y, z) && shouldReceivePowerFrom(level, x - 1, y + 1, z, Direction.UNDEFINED)) w = true;
            if (level.isSolidBlockingTile(x + 1, y, z) && shouldReceivePowerFrom(level, x + 1, y + 1, z, Direction.UNDEFINED)) e = true;
            if (level.isSolidBlockingTile(x, y, z - 1) && shouldReceivePowerFrom(level, x, y + 1, z - 1, Direction.UNDEFINED)) n = true;
            if (level.isSolidBlockingTile(x, y, z + 1) && shouldReceivePowerFrom(level, x, y + 1, z + 1, Direction.UNDEFINED)) s = true;
        }

        if (!n && !e && !w && !s && dir >= 2 && dir <= 5) return true;

        if (dir == 2 && n && !w && !e) return true;
        if (dir == 3 && s && !w && !e) return true;
        if (dir == 4 && w && !n && !s) return true;
        if (dir == 5 && e && !n && !s) return true;

        return false;
    }
    
    @Override
    public boolean isSignalSource() {
        return this.shouldSignal;
    }
    
    @Override
    public void animateTick(final Level level, final int x, final int y, final int z, final Random random) {
        final int data = level.getData(x, y, z);
        if (data > 0) {
            final double xx = x + 0.5 + (random.nextFloat() - 0.5) * 0.2;
            final double yy = y + 1 / 16.0f;
            final double zz = z + 0.5 + (random.nextFloat() - 0.5) * 0.2;

            // use the x movement variable to determine particle color
            final float pow = data / 15.0f;
            float red = pow * 0.6f + 0.4f;
            if (data == 0) red = 0.0f;

            float green = pow * pow * 0.7f - 0.5f;
            float blue = pow * pow * 0.6f - 0.7f;
            if (green < 0.0f) green = 0.0f;
            if (blue < 0.0f) blue = 0.0f;

            level.addParticle("reddust", xx, yy, zz, red, green, blue);
        }
    }
    
    public static boolean shouldReceivePowerFrom(final LevelSource level, final int x, final int y, final int z, final int direction) {
        final int t = level.getTile(x, y, z);
        if (t == Tile.redStoneDust.id) return true;
        if (t == 0) return false;
        if (t == Tile.diode_off.id || t == Tile.diode_on.id) {
            int data = level.getData(x, y, z);
            if (direction == Direction.DIRECTION_OPPOSITE[data & DiodeTile.DIRECTION_MASK]) return true;
        }
        return Tile.tiles[t].isSignalSource();
    }
}
