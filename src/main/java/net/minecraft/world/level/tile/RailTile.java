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
    
    public static boolean isRail(final Level level, final int x, final int y, final int z) {
        final int tile = level.getTile(x, y, z);
        return tile == Tile.rail.id || tile == Tile.goldenRail.id || tile == Tile.detectorRail.id;
    }
    
    public static boolean isRail(final int id) {
        return id == Tile.rail.id || id == Tile.goldenRail.id || id == Tile.detectorRail.id;
    }
    
    protected RailTile(final int id, final int tex, final boolean usesDataBit) {
        super(id, tex, Material.decoration);
        this.usesDataBit = usesDataBit;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 2 / 16.0f, 1.0f);
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
            this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 2 / 16.0f + 0.5f, 1.0f);
        }
        else {
            this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 2 / 16.0f, 1.0f);
        }
    }
    
    @Override
    public int getTexture(final int face, final int data) {
        if (this.usesDataBit) {
            if (this.id == Tile.goldenRail.id) {
                if ((data & RAIL_DATA_BIT) == 0x0) {
                    return this.tex - 16;
                }
            }
        }
        else if (data >= 6) return this.tex - 16;
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
        if (level.isSolidBlockingTile(x, y - 1, z)) {
            return true;
        }
        return false;
    }
    
    @Override
    public void onPlace(final Level level, final int x, final int y, final int z) {
        if (!level.isClientSide) {
            this.updateDir(level, x, y, z, true);
        }
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (level.isClientSide) return;

        final int data = level.getData(x, y, z);
        int dir = data;
        if (this.usesDataBit) {
            dir &= RAIL_DIRECTION_MASK;
        }
        boolean remove = false;

        if (!level.isSolidBlockingTile(x, y - 1, z)) remove = true;
        if (dir == 2 && !level.isSolidBlockingTile(x + 1, y, z)) remove = true;
        if (dir == 3 && !level.isSolidBlockingTile(x - 1, y, z)) remove = true;
        if (dir == 4 && !level.isSolidBlockingTile(x, y, z - 1)) remove = true;
        if (dir == 5 && !level.isSolidBlockingTile(x, y, z + 1)) remove = true;

        if (remove) {
            this.spawnResources(level, x, y, z, level.getData(x, y, z));
            level.setTile(x, y, z, 0);
        }
        else {
            if (this.id == Tile.goldenRail.id) {
                boolean signal = level.hasNeighborSignal(x, y, z) || level.hasNeighborSignal(x, y + 1, z);
                signal = signal || this.findGoldenRailSignal(level, x, y, z, data, true, 0) || this.findGoldenRailSignal(level, x, y, z, data, false, 0);

                boolean changed = false;
                if (signal && (data & RAIL_DATA_BIT) == 0x0) {
                    level.setData(x, y, z, dir | RAIL_DATA_BIT);
                    changed = true;
                } else if (!signal && (data & RAIL_DATA_BIT) != 0x0) {
                    level.setData(x, y, z, dir);
                    changed = true;
                }

                // usually the level only updates neighbors that are in the same
                // y plane as the current tile, but sloped rails may need to
                // update tiles above or below it as well
                if (changed) {
                    level.updateNeighborsAt(x, y - 1, z, this.id);
                    if (dir == 2 || dir == 3 || dir == 4 || dir == 5) {
                        level.updateNeighborsAt(x, y + 1, z, this.id);
                    }
                }
            } else if (type > 0 && Tile.tiles[type].isSignalSource() && !this.usesDataBit) {
                Rail rail = new Rail(level, x, y, z);
                if (rail.countPotentialConnections() == 3) {
                    this.updateDir(level, x, y, z, false);
                }
            }
        }
    }
    
    private void updateDir(final Level level, final int x, final int y, final int z, final boolean first) {
        if (level.isClientSide) return;
        Rail rail = new Rail(level, x, y, z);
        rail.place(level.hasNeighborSignal(x, y, z), first);
    }
    
    private boolean findGoldenRailSignal(final Level level, int x, int y, int z, final int data, final boolean forward, final int searchDepth) {
        if (searchDepth >= 8) {
            return false;
        }

        int dir = data & RAIL_DIRECTION_MASK;
        boolean checkBelow = true;
        switch (dir) {
            case DIR_FLAT_Z: {
                if (forward) z++;
                else z--;
                break;
            }
            case DIR_FLAT_X: {
                if (forward) x--;
                else x++;
                break;
            }
            case 2: {
                if (forward) x--;
                else {
                    x++;
                    y++;
                    checkBelow = false;
                }
                dir = DIR_FLAT_X;
                break;
            }
            case 3: {
                if (forward) {
                    x--;
                    y++;
                    checkBelow = false;
                }
                else x++;
                dir = DIR_FLAT_X;
                break;
            }
            case 4: {
                if (forward) z++;
                else {
                    z--;
                    y++;
                    checkBelow = false;
                }
                dir = DIR_FLAT_Z;
                break;
            }
            case 5: {
                if (forward) {
                    z++;
                    y++;
                    checkBelow = false;
                }
                else z--;
                dir = DIR_FLAT_Z;
                break;
            }
        }
        if (this.isGoldenRailWithPower(level, x, y, z, forward, searchDepth, dir)) {
            return true;
        }
        if (checkBelow && this.isGoldenRailWithPower(level, x, y - 1, z, forward, searchDepth, dir)) {
            return true;
        }
        return false;
    }
    
    private boolean isGoldenRailWithPower(final Level level, final int x, final int y, final int z, final boolean forward, final int searchDepth, final int dir) {
        int tile = level.getTile(x, y, z);
        if (tile == Tile.goldenRail.id) {
            final int tileData = level.getData(x, y, z);
            final int myDir = tileData & RAIL_DIRECTION_MASK;
            if (dir == DIR_FLAT_X && (myDir == DIR_FLAT_Z || myDir == 4 || myDir == 5)) {
                return false;
            }
            if (dir == DIR_FLAT_Z && (myDir == DIR_FLAT_X || myDir == 2 || myDir == 3)) {
                return false;
            }

            if ((tileData & RAIL_DATA_BIT) != 0x0) {
                if (level.hasNeighborSignal(x, y, z)) return true;
                if (level.hasNeighborSignal(x, y + 1, z)) return true;
                return this.findGoldenRailSignal(level, x, y, z, tileData, forward, searchDepth + 1);
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
        private int x, y, z;
        private final boolean usesDataBit;
        private List<TilePos> connections = new ArrayList<>();

        public Rail(final Level level, final int x, final int y, final int z) {
            this.level = level;
            this.x = x;
            this.y = y;
            this.z = z;

            final int id = level.getTile(x, y, z);

            int direction = level.getData(x, y, z);
            if (((RailTile) tiles[id]).usesDataBit) {
                this.usesDataBit = true;
                direction &= ~RAIL_DATA_BIT;
            }
            else {
                this.usesDataBit = false;
            }
            this.updateConnections(direction);
        }

        private void updateConnections(final int direction) {
            this.connections.clear();
            if (direction == DIR_FLAT_Z) {
                this.connections.add(new TilePos(this.x, this.y, this.z - 1));
                this.connections.add(new TilePos(this.x, this.y, this.z + 1));
            }
            else if (direction == DIR_FLAT_X) {
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
                    this.connections.remove(i);
                    i--;
                }
                else {
                    this.connections.set(i, new TilePos(rail.x, rail.y, rail.z));
                }
            }
        }

        private boolean hasRail(final int x, final int y, final int z) {
            if (isRail(this.level, x, y, z)) return true;
            if (isRail(this.level, x, y + 1, z)) return true;
            if (isRail(this.level, x, y - 1, z)) return true;
            return false;
        }

        private Rail getRail(final TilePos p) {
            if (isRail(this.level, p.x, p.y, p.z)) return new Rail(this.level, p.x, p.y, p.z);
            if (isRail(this.level, p.x, p.y + 1, p.z)) return new Rail(this.level, p.x, p.y + 1, p.z);
            if (isRail(this.level, p.x, p.y - 1, p.z)) return new Rail(this.level, p.x, p.y - 1, p.z);
            return null;
        }

        private boolean connectsTo(final Rail rail) {
            for (int i = 0; i < this.connections.size(); ++i) {
                final TilePos p = this.connections.get(i);
                if (p.x == rail.x && p.z == rail.z) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasConnection(final int x, final int y, final int z) {
            for (int i = 0; i < this.connections.size(); ++i) {
                final TilePos p = this.connections.get(i);
                if (p.x == x && p.z == z) {
                    return true;
                }
            }
            return false;
        }

        private int countPotentialConnections() {
            int count = 0;

            if (this.hasRail(this.x, this.y, this.z - 1)) count++;
            if (this.hasRail(this.x, this.y, this.z + 1)) count++;
            if (this.hasRail(this.x - 1, this.y, this.z)) count++;
            if (this.hasRail(this.x + 1, this.y, this.z)) count++;

            return count;
        }

        private boolean canConnectTo(final Rail rail) {
            if (this.connectsTo(rail)) return true;
            if (this.connections.size() == 2) {
                return false;
            }
            if (this.connections.isEmpty()) {
                return true;
            }

            final TilePos c = this.connections.get(0);
            return rail.y == this.y && c.y == this.y ? true : true;
        }

        private void connectTo(final Rail rail) {
            this.connections.add(new TilePos(rail.x, rail.y, rail.z));

            final boolean n = this.hasConnection(this.x, this.y, this.z - 1);
            final boolean s = this.hasConnection(this.x, this.y, this.z + 1);
            final boolean w = this.hasConnection(this.x - 1, this.y, this.z);
            final boolean e = this.hasConnection(this.x + 1, this.y, this.z);

            int dir = -1;

            if (n || s) dir = DIR_FLAT_Z;
            if (w || e) dir = DIR_FLAT_X;

            if (!this.usesDataBit) {
                if (s && e && !n && !w) dir = 6;
                if (s && w && !n && !e) dir = 7;
                if (n && w && !s && !e) dir = 8;
                if (n && e && !s && !w) dir = 9;
            }
            if (dir == DIR_FLAT_Z) {
                if (isRail(this.level, this.x, this.y + 1, this.z - 1)) dir = 4;
                if (isRail(this.level, this.x, this.y + 1, this.z + 1)) dir = 5;
            }
            if (dir == DIR_FLAT_X) {
                if (isRail(this.level, this.x + 1, this.y + 1, this.z)) dir = 2;
                if (isRail(this.level, this.x - 1, this.y + 1, this.z)) dir = 3;
            }

            if (dir < 0) dir = DIR_FLAT_Z;

            int data = dir;
            if (this.usesDataBit) {
                data = ((this.level.getData(this.x, this.y, this.z) & RAIL_DATA_BIT) | dir);
            }

            this.level.setData(this.x, this.y, this.z, data);
        }

        private boolean hasNeighborRail(final int x, final int y, final int z) {
            TilePos tp = new TilePos(x, y, z);
            final Rail neighbor = this.getRail(tp);
            if (neighbor == null) return false;
            neighbor.removeSoftConnections();
            return neighbor.canConnectTo(this);
        }

        public void place(final boolean hasSignal, final boolean first) {
            final boolean n = this.hasNeighborRail(this.x, this.y, this.z - 1);
            final boolean s = this.hasNeighborRail(this.x, this.y, this.z + 1);
            final boolean w = this.hasNeighborRail(this.x - 1, this.y, this.z);
            final boolean e = this.hasNeighborRail(this.x + 1, this.y, this.z);

            int dir = -1;
            if ((n || s) && !w && !e) dir = DIR_FLAT_Z;
            if ((w || e) && !n && !s) dir = DIR_FLAT_X;

            if (!this.usesDataBit) {
                if (s && e && !n && !w) dir = 6;
                if (s && w && !n && !e) dir = 7;
                if (n && w && !s && !e) dir = 8;
                if (n && e && !s && !w) dir = 9;
            }
            if (dir == -1) {
                if (n || s) dir = DIR_FLAT_Z;
                if (w || e) dir = DIR_FLAT_X;

                if (!this.usesDataBit) {
                    if (hasSignal) {
                        if (s && e) dir = 6;
                        if (w && s) dir = 7;
                        if (e && n) dir = 9;
                        if (n && w) dir = 8;
                    }
                    else {
                        if (n && w) dir = 8;
                        if (e && n) dir = 9;
                        if (w && s) dir = 7;
                        if (s && e) dir = 6;
                    }
                }
            }

            if (dir == DIR_FLAT_Z) {
                if (isRail(this.level, this.x, this.y + 1, this.z - 1)) dir = 4;
                if (isRail(this.level, this.x, this.y + 1, this.z + 1)) dir = 5;
            }
            if (dir == DIR_FLAT_X) {
                if (isRail(this.level, this.x + 1, this.y + 1, this.z)) dir = 2;
                if (isRail(this.level, this.x - 1, this.y + 1, this.z)) dir = 3;
            }

            if (dir < 0) dir = DIR_FLAT_Z;

            this.updateConnections(dir);

            int data = dir;
            if (this.usesDataBit) {
                data = ((this.level.getData(this.x, this.y, this.z) & 0x8) | dir);
            }

            if (first || this.level.getData(this.x, this.y, this.z) != data) {
                this.level.setData(this.x, this.y, this.z, data);
                for (int i = 0; i < this.connections.size(); ++i) {
                    final Rail neighbor = this.getRail(this.connections.get(i));
                    if (neighbor == null) continue;
                    neighbor.removeSoftConnections();

                    if (neighbor.canConnectTo(this)) {
                        neighbor.connectTo(this);
                    }
                }
            }
        }
    }
}
