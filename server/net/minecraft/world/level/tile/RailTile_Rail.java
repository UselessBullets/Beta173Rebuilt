// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.tile;

import net.minecraft.world.level.TilePos;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.Level;

class RailTile_Rail
{
    private Level level;
    private int x;
    private int y;
    private int z;
    private final boolean usesDataBit;
    private List connections;
    final /* synthetic */ RailTile rt;
    
    public RailTile_Rail(final RailTile rt, final Level level, final int x, final int y, final int z) {
        this.rt = rt;
        this.connections = new ArrayList();
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        final int tile = level.getTile(x, y, z);
        int data = level.getData(x, y, z);
        if (((RailTile)Tile.tiles[tile]).usesDataBit) {
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
            final RailTile_Rail rail = this.getRail(this.connections.get(i));
            if (rail == null || !rail.connectsTo(this)) {
                this.connections.remove(i--);
            }
            else {
                this.connections.set(i, new TilePos(rail.x, rail.y, rail.z));
            }
        }
    }
    
    private boolean hasRail(final int x, final int y, final int z) {
        return RailTile.isRail(this.level, x, y, z) || RailTile.isRail(this.level, x, y + 1, z) || RailTile.isRail(this.level, x, y - 1, z);
    }
    
    private RailTile_Rail getRail(final TilePos p) {
        if (RailTile.isRail(this.level, p.x, p.y, p.z)) {
            return new RailTile_Rail(this.rt, this.level, p.x, p.y, p.z);
        }
        if (RailTile.isRail(this.level, p.x, p.y + 1, p.z)) {
            return new RailTile_Rail(this.rt, this.level, p.x, p.y + 1, p.z);
        }
        if (RailTile.isRail(this.level, p.x, p.y - 1, p.z)) {
            return new RailTile_Rail(this.rt, this.level, p.x, p.y - 1, p.z);
        }
        return null;
    }
    
    private boolean connectsTo(final RailTile_Rail rail) {
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
    
    private boolean canConnectTo(final RailTile_Rail rail) {
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
    
    private void connectTo(final RailTile_Rail rail) {
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
            if (RailTile.isRail(this.level, this.x, this.y + 1, this.z - 1)) {
                n = 4;
            }
            if (RailTile.isRail(this.level, this.x, this.y + 1, this.z + 1)) {
                n = 5;
            }
        }
        if (n == 1) {
            if (RailTile.isRail(this.level, this.x + 1, this.y + 1, this.z)) {
                n = 2;
            }
            if (RailTile.isRail(this.level, this.x - 1, this.y + 1, this.z)) {
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
        final RailTile_Rail rail = this.getRail(new TilePos(x, y, z));
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
            if (RailTile.isRail(this.level, this.x, this.y + 1, this.z - 1)) {
                direction = 4;
            }
            if (RailTile.isRail(this.level, this.x, this.y + 1, this.z + 1)) {
                direction = 5;
            }
        }
        if (direction == 1) {
            if (RailTile.isRail(this.level, this.x + 1, this.y + 1, this.z)) {
                direction = 2;
            }
            if (RailTile.isRail(this.level, this.x - 1, this.y + 1, this.z)) {
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
                final RailTile_Rail rail = this.getRail(this.connections.get(i));
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
