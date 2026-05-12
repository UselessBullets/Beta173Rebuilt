// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.Random;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.Level;

public class RailTile extends Tile
{
    private final boolean usesDataBit;
    
    public static final boolean isRail(final Level level, final int x, final int y, final int z) {
        final int tile = level.getTile(x, y, z);
        return tile == Tile.rail.id || tile == Tile.goldenRail.id || tile == Tile.detectorRail.id;
    }
    
    public static final boolean isRail(final int id) {
        return id == Tile.rail.id || id == Tile.goldenRail.id || id == Tile.detectorRail.id;
    }
    
    protected RailTile(final int id, final int tex, final boolean usesDataBit) {
        super(id, tex, Material.decoration);
        this.usesDataBit = usesDataBit;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
    }
    
    public boolean isUsesDataBit() {
        return this.usesDataBit;
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
    public HitResult clip(final Level level, final int xt, final int yt, final int zt, final Vec3 a, final Vec3 b) {
        this.updateShape(level, xt, yt, zt);
        return super.clip(level, xt, yt, zt, a, b);
    }
    
    @Override
    public void updateShape(final LevelSource level, final int x, final int y, final int z) {
        final int data = level.getData(x, y, z);
        if (data >= 2 && data <= 5) {
            this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.625f, 1.0f);
        }
        else {
            this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
        }
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (this.usesDataBit) {
            if (this.id == Tile.goldenRail.id && (data & 0x8) == 0x0) {
                return this.tex - 16;
            }
        }
        else if (data >= 6) {
            return this.tex - 16;
        }
        return this.tex;
    }
    
    @Override
    public boolean isCubeShaped() {
        return false;
    }
    
    @Override
    public int getRenderShape() {
        return 9;
    }
    
    @Override
    public int getResourceCount(final Random random) {
        return 1;
    }
    
    @Override
    public boolean mayPlace(final Level level, final int x, final int y, final int z) {
        return level.isSolidBlockingTile(x, y - 1, z);
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        if (!level.isClientSide) {
            this.updateDir(level, x, y, z, true);
        }
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (level.isClientSide) {
            return;
        }
        int data;
        final int n = data = level.getData(x, y, z);
        if (this.usesDataBit) {
            data &= 0x7;
        }
        boolean b = false;
        if (!level.isSolidBlockingTile(x, y - 1, z)) {
            b = true;
        }
        if (data == 2 && !level.isSolidBlockingTile(x + 1, y, z)) {
            b = true;
        }
        if (data == 3 && !level.isSolidBlockingTile(x - 1, y, z)) {
            b = true;
        }
        if (data == 4 && !level.isSolidBlockingTile(x, y, z - 1)) {
            b = true;
        }
        if (data == 5 && !level.isSolidBlockingTile(x, y, z + 1)) {
            b = true;
        }
        if (b) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
        else if (this.id == Tile.goldenRail.id) {
            final boolean b2 = level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z) || this.findGoldenRailSignal(level, x, y, z, n, true, 0) || this.findGoldenRailSignal(level, x, y, z, n, false, 0);
            boolean b3 = false;
            if (b2 && (n & 0x8) == 0x0) {
                level.setData(x, y, z, data | 0x8);
                b3 = true;
            }
            else if (!b2 && (n & 0x8) != 0x0) {
                level.setData(x, y, z, data);
                b3 = true;
            }
            if (b3) {
                level.updateNeighborsAt(x, y - 1, z, this.id);
                if (data == 2 || data == 3 || data == 4 || data == 5) {
                    level.updateNeighborsAt(x, y + 1, z, this.id);
                }
            }
        }
        else if (type > 0 && Tile.tiles[type].isSignalSource() && !this.usesDataBit && new RailTile_Rail(this, level, x, y, z).countPotentialConnections() == 3) {
            this.updateDir(level, x, y, z, false);
        }
    }
    
    private void updateDir(final Level level, final int x, final int y, final int z, final boolean first) {
        if (level.isClientSide) {
            return;
        }
        new RailTile_Rail(this, level, x, y, z).place(level.hasNeighborSignal(x, y, z), first);
    }
    
    private boolean findGoldenRailSignal(final Level level, int x, int y, int z, final int data, final boolean forward, final int searchDepth) {
        if (searchDepth >= 8) {
            return false;
        }
        int n = data & 0x7;
        boolean b = true;
        switch (n) {
            case 0: {
                if (forward) {
                    ++z;
                    break;
                }
                --z;
                break;
            }
            case 1: {
                if (forward) {
                    --x;
                    break;
                }
                ++x;
                break;
            }
            case 2: {
                if (forward) {
                    --x;
                }
                else {
                    ++x;
                    ++y;
                    b = false;
                }
                n = 1;
                break;
            }
            case 3: {
                if (forward) {
                    --x;
                    ++y;
                    b = false;
                }
                else {
                    ++x;
                }
                n = 1;
                break;
            }
            case 4: {
                if (forward) {
                    ++z;
                }
                else {
                    --z;
                    ++y;
                    b = false;
                }
                n = 0;
                break;
            }
            case 5: {
                if (forward) {
                    ++z;
                    ++y;
                    b = false;
                }
                else {
                    --z;
                }
                n = 0;
                break;
            }
        }
        return this.isGoldenRailWithPower(level, x, y, z, forward, searchDepth, n) || (b && this.isGoldenRailWithPower(level, x, y - 1, z, forward, searchDepth, n));
    }
    
    private boolean isGoldenRailWithPower(final Level level, final int x, final int y, final int z, final boolean forward, final int searchDepth, final int dir) {
        if (level.getTile(x, y, z) == Tile.goldenRail.id) {
            final int data = level.getData(x, y, z);
            final int n = data & 0x7;
            if (dir == 1 && (n == 0 || n == 4 || n == 5)) {
                return false;
            }
            if (dir == 0 && (n == 1 || n == 2 || n == 3)) {
                return false;
            }
            if ((data & 0x8) != 0x0) {
                return level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z) || this.findGoldenRailSignal(level, x, y, z, data, forward, searchDepth + 1);
            }
        }
        return false;
    }
    
    @Override
    public int getPistonPushReaction() {
        return 0;
    }
}
