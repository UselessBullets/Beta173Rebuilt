// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.players;

import net.minecraft.network.packet.BlockRegionUpdatePacket;
import net.minecraft.network.packet.ChunkTilesUpdatePacket;
import net.minecraft.network.packet.ChunkVisibilityPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.TileUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import java.util.ArrayList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.tile.entity.TileEntity;
import util.LongHashMap;
import java.util.List;

public class PlayerChunkMap
{
    public static final int MAX_VIEW_DISTANCE = 15;
    public static final int MIN_VIEW_DISTANCE = 3;
    public static final int MAX_CHANGES_BEFORE_RESEND = 10;
    public static final int MIN_TICKS_BETWEEN_REGION_UPDATE = 10;
    public List<ServerPlayer> players = new ArrayList<>();
    private LongHashMap<PlayerChunk> chunks = new LongHashMap<>();
    private List<PlayerChunk> changedChunks = new ArrayList<>();
    private MinecraftServer server;
    private int dimension;
    private int radius;
    private final int[][] direction = new int[][] { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
    
    public PlayerChunkMap(final MinecraftServer server, final int dimension, final int radius) {
        if (radius > MAX_VIEW_DISTANCE) throw new IllegalArgumentException("Too big view radius!");
        if (radius < MIN_VIEW_DISTANCE) throw new IllegalArgumentException("Too small view radius!");
        this.radius = radius;
        this.server = server;
        this.dimension = dimension;
    }
    
    public ServerLevel getLevel() {
        return this.server.getLevel(this.dimension);
    }
    
    public void tick() {
        for (int i = 0; i < this.changedChunks.size(); ++i) {
            this.changedChunks.get(i).broadcastChanges();
        }
        this.changedChunks.clear();
    }
    
    private PlayerChunk getChunk(final int x, final int z, final boolean create) {
        final long id = x + 0x7fffffffL | z + 0x7fffffffL << 32;
        PlayerChunk chunk = this.chunks.get(id);
        if (chunk == null && create) {
            chunk = new PlayerChunk(x, z);
            this.chunks.put(id, chunk);
        }
        return chunk;
    }
    
    public void isTrackingTile(final int x, final int y, final int z) {
        int xc = x >> 4;
        int zc = z >> 4;
        final PlayerChunk chunk = this.getChunk(xc, zc, false);
        if (chunk != null) {
            chunk.tileChanged(x & 0xF, y, z & 0xF);
        }
    }
    
    public void add(final ServerPlayer player) {
        final int xc = (int)player.x >> 4;
        final int zc = (int)player.z >> 4;

        player.lastMoveX = player.x;
        player.lastMoveZ = player.z;

//        for (int x = xc - radius; x <= xc + radius; x++) {
//            for (int z = zc - radius; z <= zc + radius; z++) {
//                getChunk(x, z, true).add(player);
//            }
//        }

        // CraftBukkit start
        int facing = 0;
        final int size = this.radius;
        int dx = 0;
        int dz = 0;

        // Origin
        this.getChunk(xc, zc, true).add(player);

        for (int legSize = 1; legSize <= size * 2; ++legSize) {
            for (int leg = 0; leg < 2; ++leg) {
                final int[] dir = this.direction[facing++ % 4];

                for (int k = 0; k < legSize; ++k) {
                    dx += dir[0];
                    dz += dir[1];

                    this.getChunk(xc + dx, zc + dz, true).add(player);
                }
            }
        }

        // Final leg
        facing %= 4;
        for (int k = 0; k < size * 2; ++k) {
            dx += this.direction[facing][0];
            dz += this.direction[facing][1];
            this.getChunk(xc + dx, zc + dz, true).add(player);
        }
        // CraftBukkit end

        this.players.add(player);
    }
    
    public void remove(final ServerPlayer player) {
        final int xc = (int)player.lastMoveX >> 4;
        final int zc = (int)player.lastMoveZ >> 4;

        for (int x = xc - this.radius; x <= xc + this.radius; ++x) {
            for (int z = zc - this.radius; z <= zc + this.radius; ++z) {
                final PlayerChunk playerChunk = this.getChunk(x, z, false);
                if (playerChunk != null) playerChunk.remove(player);
            }
        }

        this.players.remove(player);
    }
    
    private boolean chunkInRange(final int x, final int z, final int xc, final int zc) {
        final int xd = x - xc;
        final int zd = z - zc;
        if (xd < -this.radius || xd > this.radius) return false;
        if (zd < -this.radius || zd > this.radius) return false;
        return true;
    }
    
    public void move(final ServerPlayer player) {
        final int xc = (int)player.x >> 4;
        final int zc = (int)player.z >> 4;

        final double _xd = player.lastMoveX - player.x;
        final double _zd = player.lastMoveZ - player.z;
        if (_xd * _xd + _zd * _zd < 8 * 8) return;

        final int last_xc = (int)player.lastMoveX >> 4;
        final int last_zc = (int)player.lastMoveZ >> 4;

        final int xd = xc - last_xc;
        final int zd = zc - last_zc;
        if (xd == 0 && zd == 0) return;

        for (int x = xc - this.radius; x <= xc + this.radius; ++x) {
            for (int z = zc - this.radius; z <= zc + this.radius; ++z) {
                if (!this.chunkInRange(x, z, last_xc, last_zc)) {
                    this.getChunk(x, z, true).add(player);
                }

                if (!this.chunkInRange(x - xd, z - zd, xc, zc)) {
                    final PlayerChunk chunk = this.getChunk(x - xd, z - zd, false);
                    if (chunk != null) {
                        chunk.remove(player);
                    }
                }
            }
        }
        
        player.lastMoveX = player.x;
        player.lastMoveZ = player.z;
    }
    
    public int getMaxRange() {
        return this.radius * 16 - 16;
    }

    class PlayerChunk
    {
        private List<ServerPlayer> players = new ArrayList<>();
        private int x,  z;
        private ChunkPos pos;
        private short[] changedTiles = new short[MAX_CHANGES_BEFORE_RESEND];
        private int changes = 0;
        private int xChangeMin, xChangeMax;
        private int yChangeMin, yChangeMax;
        private int zChangeMin, zChangeMax;

        public PlayerChunk(final int x, final int z) {
            this.x = x;
            this.z = z;
            this.pos = new ChunkPos(x, z);
            getLevel().cache.create(x, z);
        }

        public void add(final ServerPlayer player) {
            if (this.players.contains(player)) throw new IllegalStateException("Failed to add player. " + player + " already is in chunk " + this.x + ", " + this.z);
            player.seenChunks.add(this.pos);
            player.connection.send(new ChunkVisibilityPacket(this.pos.x, this.pos.z, true));
            this.players.add(player);
            player.chunksToSend.add(this.pos);
        }

        public void remove(final ServerPlayer player) {
            if (!this.players.contains(player)) return;

            this.players.remove(player);
            if (this.players.size() == 0) {
                long id = this.x + 0x7fffffffL | this.z + 0x7fffffffL << 32;
                PlayerChunkMap.this.chunks.remove(id);
                if (this.changes > 0) {
                    PlayerChunkMap.this.changedChunks.remove(this);
                }
                getLevel().cache.drop(this.x, this.z);
            }

            player.chunksToSend.remove(this.pos);
            if (player.seenChunks.contains(this.pos)) {
                player.connection.send(new ChunkVisibilityPacket(this.x, this.z, false));
            }
        }

        public void tileChanged(final int x, final int y, final int z) {
            if (this.changes == 0) {
                PlayerChunkMap.this.changedChunks.add(this);
                this.xChangeMin = this.xChangeMax = x;
                this.yChangeMin = this.yChangeMax = y;
                this.zChangeMin =  this.zChangeMax = z;
            }
            if (this.xChangeMin > x) this.xChangeMin = x;
            if (this.xChangeMax < x) this.xChangeMax = x;

            if (this.yChangeMin > y) this.yChangeMin = y;
            if (this.yChangeMax < y) this.yChangeMax = y;

            if (this.zChangeMin > z) this.zChangeMin = z;
            if (this.zChangeMax < z) this.zChangeMax = z;

            if (this.changes < MAX_CHANGES_BEFORE_RESEND) {
                final short id = (short)(x << 12 | z << 8 | y);
                for (int i = 0; i < this.changes; ++i) {
                    if (this.changedTiles[i] == id) return;
                }

                this.changedTiles[this.changes++] = id;
            }
        }

        public void broadcast(final Packet packet) {
            for (int i = 0; i < this.players.size(); ++i) {
                final ServerPlayer player = this.players.get(i);
                if (player.seenChunks.contains(this.pos)) {
                    player.connection.send(packet);
                }
            }
        }

        public void broadcastChanges() {
            final ServerLevel level = getLevel();
            if (this.changes == 0) {
                return;
            }
            if (this.changes == 1) {
                final int x = this.x * 16 + this.xChangeMin;
                final int y = this.yChangeMin;
                final int z = this.z * 16 + this.zChangeMin;
                this.broadcast(new TileUpdatePacket(x, y, z, level));
                if (Tile.isEntityTile[level.getTile(x, y, z)]) {
                    this.broadcast(level.getTileEntity(x, y, z));
                }
            }
            else if (this.changes == MAX_CHANGES_BEFORE_RESEND) {
                this.yChangeMin = this.yChangeMin / 2 * 2;
                this.yChangeMax = (this.yChangeMax / 2 + 1) * 2;
                final int xp = this.xChangeMin + this.x * 16;
                final int yp = this.yChangeMin;
                final int zp = this.zChangeMin + this.z * 16;
                final int xs = this.xChangeMax - this.xChangeMin + 1;
                final int ys = this.yChangeMax - this.yChangeMin + 2;
                final int zs = this.zChangeMax - this.zChangeMin + 1;

                this.broadcast(new BlockRegionUpdatePacket(xp, yp, zp, xs, ys, zs, level));
                final List<TileEntity> tes = level.getTileEntitiesInRegion(xp, yp, zp, xp + xs, yp + ys, zp + zs);
                for (int i = 0; i < tes.size(); ++i) {
                    this.broadcast(tes.get(i));
                }
            }
            else {
                this.broadcast(new ChunkTilesUpdatePacket(this.x, this.z, this.changedTiles, this.changes, level));
                for (int i = 0; i < this.changes; ++i) {
                    final int x = this.x * 16 + (this.changes >> 12 & 0xF);
                    final int y = this.changes & 0xFF;
                    final int z = this.z * 16 + (this.changes >> 8 & 0xF);

                    if (Tile.isEntityTile[level.getTile(x, y, z)]) {
                        System.out.println("Sending!");
                        this.broadcast(level.getTileEntity(x, y, z));
                    }
                }
            }
            this.changes = 0;
        }

        private void broadcast(final TileEntity te) {
            if (te != null) {
                final Packet p = te.getUpdatePacket();
                if (p != null) {
                    this.broadcast(p);
                }
            }
        }
    }
}
