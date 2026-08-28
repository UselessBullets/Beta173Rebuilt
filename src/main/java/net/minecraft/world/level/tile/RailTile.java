// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.LevelSource;
import net.minecraft.world.level.TilePos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.Level;

public class RailTile extends Tile
{
    public static final int DIR_FLAT_Z = 0;
    public static final int DIR_FLAT_X = 1;
    // the data bit is used by boosters and detectors, so they can't turn
    public static final int RAIL_DATA_BIT = 0b1000;
    public static final int RAIL_DIRECTION_MASK = 0b111;
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
        return Tile.SHAPE_RAIL;
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
        else if (type > 0 && Tile.tiles[type].isSignalSource() && !this.usesDataBit && new Rail(this, level, x, y, z).countPotentialConnections() == 3) {
            this.updateDir(level, x, y, z, false);
        }
    }
    
    private void updateDir(final Level level, final int x, final int y, final int z, final boolean first) {
        if (level.isClientSide) {
            return;
        }
        new Rail(this, level, x, y, z).place(level.hasNeighborSignal(x, y, z), first);
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
        return Material.PUSH_NORMAL;
    }

    static class Rail
    {
        private Level level;
        private int x;
        private int y;
        private int z;
        private final boolean usesDataBit;
        private List<TilePos> connections;
        final /* synthetic */ RailTile rt;

        public Rail(final RailTile rt, final Level level, final int x, final int y, final int z) {
            this.rt = rt;
            this.connections = new ArrayList();
            this.level = level;
            this.x = x;
            this.y = y;
            this.z = z;
            final int tile = level.getTile(x, y, z);
            int data = level.getData(x, y, z);
            if (((RailTile) tiles[tile]).usesDataBit) {
                this.usesDataBit = true;
                data &= 0xFFFFFFF7;
            }
            else {
                this.usesDataBit = false;
            }
            this.updateConnections(data);
        }

        private void updateConnections(final int direction) {
            this.connections.clear();
            if (direction == 0) {
                this.connections.add(new TilePos(this.x, this.y, this.z - 1));
                this.connections.add(new TilePos(this.x, this.y, this.z + 1));
            }
            else if (direction == 1) {
                this.connections.add(new TilePos(this.x - 1, this.y, this.z));
                this.connections.add(new TilePos(this.x + 1, this.y, this.z));
            }
            else if (direction == 2) {
                this.connections.add(new TilePos(this.x - 1, this.y, this.z));
                this.connections.add(new TilePos(this.x + 1, this.y + 1, this.z));
            }
            else if (direction == 3) {
                this.connections.add(new TilePos(this.x - 1, this.y + 1, this.z));
                this.connections.add(new TilePos(this.x + 1, this.y, this.z));
            }
            else if (direction == 4) {
                this.connections.add(new TilePos(this.x, this.y + 1, this.z - 1));
                this.connections.add(new TilePos(this.x, this.y, this.z + 1));
            }
            else if (direction == 5) {
                this.connections.add(new TilePos(this.x, this.y, this.z - 1));
                this.connections.add(new TilePos(this.x, this.y + 1, this.z + 1));
            }
            else if (direction == 6) {
                this.connections.add(new TilePos(this.x + 1, this.y, this.z));
                this.connections.add(new TilePos(this.x, this.y, this.z + 1));
            }
            else if (direction == 7) {
                this.connections.add(new TilePos(this.x - 1, this.y, this.z));
                this.connections.add(new TilePos(this.x, this.y, this.z + 1));
            }
            else if (direction == 8) {
                this.connections.add(new TilePos(this.x - 1, this.y, this.z));
                this.connections.add(new TilePos(this.x, this.y, this.z - 1));
            }
            else if (direction == 9) {
                this.connections.add(new TilePos(this.x + 1, this.y, this.z));
                this.connections.add(new TilePos(this.x, this.y, this.z - 1));
            }
        }

        private void removeSoftConnections() {
            for (int i = 0; i < this.connections.size(); ++i) {
                final Rail rail = this.getRail(this.connections.get(i));
                if (rail == null || !rail.connectsTo(this)) {
                    this.connections.remove(i--);
                }
                else {
                    this.connections.set(i, new TilePos(rail.x, rail.y, rail.z));
                }
            }
        }

        private boolean hasRail(final int x, final int y, final int z) {
            return isRail(this.level, x, y, z) || isRail(this.level, x, y + 1, z) || isRail(this.level, x, y - 1, z);
        }

        private Rail getRail(final TilePos p) {
            if (isRail(this.level, p.x, p.y, p.z)) {
                return new Rail(this.rt, this.level, p.x, p.y, p.z);
            }
            if (isRail(this.level, p.x, p.y + 1, p.z)) {
                return new Rail(this.rt, this.level, p.x, p.y + 1, p.z);
            }
            if (isRail(this.level, p.x, p.y - 1, p.z)) {
                return new Rail(this.rt, this.level, p.x, p.y - 1, p.z);
            }
            return null;
        }

        private boolean connectsTo(final Rail rail) {
            for (int i = 0; i < this.connections.size(); ++i) {
                final TilePos tilePos = this.connections.get(i);
                if (tilePos.x == rail.x && tilePos.z == rail.z) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasConnection(final int x, final int y, final int z) {
            for (int i = 0; i < this.connections.size(); ++i) {
                final TilePos tilePos = this.connections.get(i);
                if (tilePos.x == x && tilePos.z == z) {
                    return true;
                }
            }
            return false;
        }

        private int countPotentialConnections() {
            int n = 0;
            if (this.hasRail(this.x, this.y, this.z - 1)) {
                ++n;
            }
            if (this.hasRail(this.x, this.y, this.z + 1)) {
                ++n;
            }
            if (this.hasRail(this.x - 1, this.y, this.z)) {
                ++n;
            }
            if (this.hasRail(this.x + 1, this.y, this.z)) {
                ++n;
            }
            return n;
        }

        private boolean canConnectTo(final Rail rail) {
            if (this.connectsTo(rail)) {
                return true;
            }
            if (this.connections.size() == 2) {
                return false;
            }
            if (this.connections.size() == 0) {
                return true;
            }
            final TilePos tilePos = this.connections.get(0);
            return rail.y != this.y || tilePos.y != this.y || true;
        }

        private void connectTo(final Rail rail) {
            this.connections.add(new TilePos(rail.x, rail.y, rail.z));
            final boolean hasConnection = this.hasConnection(this.x, this.y, this.z - 1);
            final boolean hasConnection2 = this.hasConnection(this.x, this.y, this.z + 1);
            final boolean hasConnection3 = this.hasConnection(this.x - 1, this.y, this.z);
            final boolean hasConnection4 = this.hasConnection(this.x + 1, this.y, this.z);
            int n = -1;
            if (hasConnection || hasConnection2) {
                n = 0;
            }
            if (hasConnection3 || hasConnection4) {
                n = 1;
            }
            if (!this.usesDataBit) {
                if (hasConnection2 && hasConnection4 && !hasConnection && !hasConnection3) {
                    n = 6;
                }
                if (hasConnection2 && hasConnection3 && !hasConnection && !hasConnection4) {
                    n = 7;
                }
                if (hasConnection && hasConnection3 && !hasConnection2 && !hasConnection4) {
                    n = 8;
                }
                if (hasConnection && hasConnection4 && !hasConnection2 && !hasConnection3) {
                    n = 9;
                }
            }
            if (n == 0) {
                if (isRail(this.level, this.x, this.y + 1, this.z - 1)) {
                    n = 4;
                }
                if (isRail(this.level, this.x, this.y + 1, this.z + 1)) {
                    n = 5;
                }
            }
            if (n == 1) {
                if (isRail(this.level, this.x + 1, this.y + 1, this.z)) {
                    n = 2;
                }
                if (isRail(this.level, this.x - 1, this.y + 1, this.z)) {
                    n = 3;
                }
            }
            if (n < 0) {
                n = 0;
            }
            int data = n;
            if (this.usesDataBit) {
                data = ((this.level.getData(this.x, this.y, this.z) & 0x8) | n);
            }
            this.level.setData(this.x, this.y, this.z, data);
        }

        private boolean hasNeighborRail(final int x, final int y, final int z) {
            final Rail rail = this.getRail(new TilePos(x, y, z));
            if (rail == null) {
                return false;
            }
            rail.removeSoftConnections();
            return rail.canConnectTo(this);
        }

        public void place(final boolean hasSignal, final boolean first) {
            final boolean hasNeighborRail = this.hasNeighborRail(this.x, this.y, this.z - 1);
            final boolean hasNeighborRail2 = this.hasNeighborRail(this.x, this.y, this.z + 1);
            final boolean hasNeighborRail3 = this.hasNeighborRail(this.x - 1, this.y, this.z);
            final boolean hasNeighborRail4 = this.hasNeighborRail(this.x + 1, this.y, this.z);
            int direction = -1;
            if ((hasNeighborRail || hasNeighborRail2) && !hasNeighborRail3 && !hasNeighborRail4) {
                direction = 0;
            }
            if ((hasNeighborRail3 || hasNeighborRail4) && !hasNeighborRail && !hasNeighborRail2) {
                direction = 1;
            }
            if (!this.usesDataBit) {
                if (hasNeighborRail2 && hasNeighborRail4 && !hasNeighborRail && !hasNeighborRail3) {
                    direction = 6;
                }
                if (hasNeighborRail2 && hasNeighborRail3 && !hasNeighborRail && !hasNeighborRail4) {
                    direction = 7;
                }
                if (hasNeighborRail && hasNeighborRail3 && !hasNeighborRail2 && !hasNeighborRail4) {
                    direction = 8;
                }
                if (hasNeighborRail && hasNeighborRail4 && !hasNeighborRail2 && !hasNeighborRail3) {
                    direction = 9;
                }
            }
            if (direction == -1) {
                if (hasNeighborRail || hasNeighborRail2) {
                    direction = 0;
                }
                if (hasNeighborRail3 || hasNeighborRail4) {
                    direction = 1;
                }
                if (!this.usesDataBit) {
                    if (hasSignal) {
                        if (hasNeighborRail2 && hasNeighborRail4) {
                            direction = 6;
                        }
                        if (hasNeighborRail3 && hasNeighborRail2) {
                            direction = 7;
                        }
                        if (hasNeighborRail4 && hasNeighborRail) {
                            direction = 9;
                        }
                        if (hasNeighborRail && hasNeighborRail3) {
                            direction = 8;
                        }
                    }
                    else {
                        if (hasNeighborRail && hasNeighborRail3) {
                            direction = 8;
                        }
                        if (hasNeighborRail4 && hasNeighborRail) {
                            direction = 9;
                        }
                        if (hasNeighborRail3 && hasNeighborRail2) {
                            direction = 7;
                        }
                        if (hasNeighborRail2 && hasNeighborRail4) {
                            direction = 6;
                        }
                    }
                }
            }
            if (direction == 0) {
                if (isRail(this.level, this.x, this.y + 1, this.z - 1)) {
                    direction = 4;
                }
                if (isRail(this.level, this.x, this.y + 1, this.z + 1)) {
                    direction = 5;
                }
            }
            if (direction == 1) {
                if (isRail(this.level, this.x + 1, this.y + 1, this.z)) {
                    direction = 2;
                }
                if (isRail(this.level, this.x - 1, this.y + 1, this.z)) {
                    direction = 3;
                }
            }
            if (direction < 0) {
                direction = 0;
            }
            this.updateConnections(direction);
            int data = direction;
            if (this.usesDataBit) {
                data = ((this.level.getData(this.x, this.y, this.z) & 0x8) | direction);
            }
            if (first || this.level.getData(this.x, this.y, this.z) != data) {
                this.level.setData(this.x, this.y, this.z, data);
                for (int i = 0; i < this.connections.size(); ++i) {
                    final Rail rail = this.getRail(this.connections.get(i));
                    if (rail != null) {
                        rail.removeSoftConnections();
                        if (rail.canConnectTo(this)) {
                            rail.connectTo(this);
                        }
                    }
                }
            }
        }
    }
}
