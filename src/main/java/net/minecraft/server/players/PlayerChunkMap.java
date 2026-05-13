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
    public List<ServerPlayer> players;
    private LongHashMap<PlayerChunk> chunks;
    private List<PlayerChunk> changedChunks;
    private MinecraftServer server;
    private int dimension;
    private int radius;
    private final int[][] direction;
    
    public PlayerChunkMap(final MinecraftServer server, final int dimension, final int radius) {
        this.players = new ArrayList<>();
        this.chunks = new LongHashMap<>();
        this.changedChunks = new ArrayList<>();
        this.direction = new int[][] { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
        if (radius > 15) {
            throw new IllegalArgumentException("Too big view radius!");
        }
        if (radius < 3) {
            throw new IllegalArgumentException("Too small view radius!");
        }
        this.radius = radius;
        this.server = server;
        this.dimension = dimension;
    }
    
    public ServerLevel getLevel() {
        return this.server.getLevel(this.dimension);
    }
    
    public void tick() {
        for (int i = 0; i < this.changedChunks.size(); ++i) {
            ((PlayerChunk)this.changedChunks.get(i)).broadcastChanges();
        }
        this.changedChunks.clear();
    }
    
    private PlayerChunk getChunk(final int x, final int z, final boolean create) {
        final long n = x + 2147483647L | z + 2147483647L << 32;
        PlayerChunk value = (PlayerChunk)this.chunks.get(n);
        if (value == null && create) {
            value = new PlayerChunk(this, x, z);
            this.chunks.put(n, value);
        }
        return value;
    }
    
    public void isTrackingTile(final int x, final int y, final int z) {
        final PlayerChunk chunk = this.getChunk(x >> 4, z >> 4, false);
        if (chunk != null) {
            chunk.tileChanged(x & 0xF, y, z & 0xF);
        }
    }
    
    public void add(final ServerPlayer player) {
        final int x = (int)player.x >> 4;
        final int z = (int)player.z >> 4;
        player.lastMoveX = player.x;
        player.lastMoveZ = player.z;
        int n = 0;
        final int radius = this.radius;
        int n2 = 0;
        int n3 = 0;
        this.getChunk(x, z, true).add(player);
        for (int i = 1; i <= radius * 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                final int[] array = this.direction[n++ % 4];
                for (int k = 0; k < i; ++k) {
                    n2 += array[0];
                    n3 += array[1];
                    this.getChunk(x + n2, z + n3, true).add(player);
                }
            }
        }
        final int n4 = n % 4;
        for (int l = 0; l < radius * 2; ++l) {
            n2 += this.direction[n4][0];
            n3 += this.direction[n4][1];
            this.getChunk(x + n2, z + n3, true).add(player);
        }
        this.players.add(player);
    }
    
    public void remove(final ServerPlayer player) {
        final int n = (int)player.lastMoveX >> 4;
        final int n2 = (int)player.lastMoveZ >> 4;
        for (int i = n - this.radius; i <= n + this.radius; ++i) {
            for (int j = n2 - this.radius; j <= n2 + this.radius; ++j) {
                final PlayerChunk chunk = this.getChunk(i, j, false);
                if (chunk != null) {
                    chunk.remove(player);
                }
            }
        }
        this.players.remove(player);
    }
    
    private boolean chunkInRange(final int x, final int z, final int xc, final int zc) {
        final int n = x - xc;
        final int n2 = z - zc;
        return n >= -this.radius && n <= this.radius && n2 >= -this.radius && n2 <= this.radius;
    }
    
    public void move(final ServerPlayer player) {
        final int xc = (int)player.x >> 4;
        final int zc = (int)player.z >> 4;
        final double n = player.lastMoveX - player.x;
        final double n2 = player.lastMoveZ - player.z;
        if (n * n + n2 * n2 < 64.0) {
            return;
        }
        final int xc2 = (int)player.lastMoveX >> 4;
        final int zc2 = (int)player.lastMoveZ >> 4;
        final int n3 = xc - xc2;
        final int n4 = zc - zc2;
        if (n3 == 0 && n4 == 0) {
            return;
        }
        for (int i = xc - this.radius; i <= xc + this.radius; ++i) {
            for (int j = zc - this.radius; j <= zc + this.radius; ++j) {
                if (!this.chunkInRange(i, j, xc2, zc2)) {
                    this.getChunk(i, j, true).add(player);
                }
                if (!this.chunkInRange(i - n3, j - n4, xc, zc)) {
                    final PlayerChunk chunk = this.getChunk(i - n3, j - n4, false);
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

    static class PlayerChunk
    {
        private List<ServerPlayer> players;
        private int x;
        private int z;
        private ChunkPos pos;
        private short[] changedTiles;
        private int changes;
        private int xChangeMin;
        private int xChangeMax;
        private int yChangeMin;
        private int yChangeMax;
        private int zChangeMin;
        private int zChangeMax;
        final /* synthetic */ PlayerChunkMap playerChunkMap;

        public PlayerChunk(final PlayerChunkMap pcm, final int x, final int z) {
            this.playerChunkMap = pcm;
            this.players = new ArrayList<>();
            this.changedTiles = new short[10];
            this.changes = 0;
            this.x = x;
            this.z = z;
            this.pos = new ChunkPos(x, z);
            pcm.getLevel().cache.create(x, z);
        }

        public void add(final ServerPlayer player) {
            if (this.players.contains(player)) {
                throw new IllegalStateException("Failed to add player. " + player + " already is in chunk " + this.x + ", " + this.z);
            }
            player.seenChunks.add(this.pos);
            player.connection.send(new ChunkVisibilityPacket(this.pos.x, this.pos.z, true));
            this.players.add(player);
            player.chunksToSend.add(this.pos);
        }

        public void remove(final ServerPlayer player) {
            if (!this.players.contains(player)) {
                return;
            }
            this.players.remove(player);
            if (this.players.size() == 0) {
                this.playerChunkMap.chunks.remove(this.x + 2147483647L | this.z + 2147483647L << 32);
                if (this.changes > 0) {
                    this.playerChunkMap.changedChunks.remove(this);
                }
                this.playerChunkMap.getLevel().cache.drop(this.x, this.z);
            }
            player.chunksToSend.remove(this.pos);
            if (player.seenChunks.contains(this.pos)) {
                player.connection.send(new ChunkVisibilityPacket(this.x, this.z, false));
            }
        }

        public void tileChanged(final int x, final int y, final int z) {
            if (this.changes == 0) {
                this.playerChunkMap.changedChunks.add(this);
                this.xChangeMax = x;
                this.xChangeMin = x;
                this.yChangeMax = y;
                this.yChangeMin = y;
                this.zChangeMax = z;
                this.zChangeMin = z;
            }
            if (this.xChangeMin > x) {
                this.xChangeMin = x;
            }
            if (this.xChangeMax < x) {
                this.xChangeMax = x;
            }
            if (this.yChangeMin > y) {
                this.yChangeMin = y;
            }
            if (this.yChangeMax < y) {
                this.yChangeMax = y;
            }
            if (this.zChangeMin > z) {
                this.zChangeMin = z;
            }
            if (this.zChangeMax < z) {
                this.zChangeMax = z;
            }
            if (this.changes < 10) {
                final short n = (short)(x << 12 | z << 8 | y);
                for (int i = 0; i < this.changes; ++i) {
                    if (this.changedTiles[i] == n) {
                        return;
                    }
                }
                this.changedTiles[this.changes++] = n;
            }
        }

        public void broadcast(final Packet packet) {
            for (int i = 0; i < this.players.size(); ++i) {
                final ServerPlayer serverPlayer = this.players.get(i);
                if (serverPlayer.seenChunks.contains(this.pos)) {
                    serverPlayer.connection.send(packet);
                }
            }
        }

        public void broadcastChanges() {
            final ServerLevel level = this.playerChunkMap.getLevel();
            if (this.changes == 0) {
                return;
            }
            if (this.changes == 1) {
                final int x = this.x * 16 + this.xChangeMin;
                final int yChangeMin = this.yChangeMin;
                final int z = this.z * 16 + this.zChangeMin;
                this.broadcast(new TileUpdatePacket(x, yChangeMin, z, level));
                if (Tile.isEntityTile[level.getTile(x, yChangeMin, z)]) {
                    this.broadcast(level.getTileEntity(x, yChangeMin, z));
                }
            }
            else if (this.changes == 10) {
                this.yChangeMin = this.yChangeMin / 2 * 2;
                this.yChangeMax = (this.yChangeMax / 2 + 1) * 2;
                final int n = this.xChangeMin + this.x * 16;
                final int yChangeMin2 = this.yChangeMin;
                final int n2 = this.zChangeMin + this.z * 16;
                final int xs = this.xChangeMax - this.xChangeMin + 1;
                final int ys = this.yChangeMax - this.yChangeMin + 2;
                final int zs = this.zChangeMax - this.zChangeMin + 1;
                this.broadcast(new BlockRegionUpdatePacket(n, yChangeMin2, n2, xs, ys, zs, level));
                final List tileEntitiesInRegion = level.getTileEntitiesInRegion(n, yChangeMin2, n2, n + xs, yChangeMin2 + ys, n2 + zs);
                for (int i = 0; i < tileEntitiesInRegion.size(); ++i) {
                    this.broadcast((TileEntity)tileEntitiesInRegion.get(i));
                }
            }
            else {
                this.broadcast(new ChunkTilesUpdatePacket(this.x, this.z, this.changedTiles, this.changes, level));
                for (int j = 0; j < this.changes; ++j) {
                    final int n3 = this.x * 16 + (this.changes >> 12 & 0xF);
                    final int n4 = this.changes & 0xFF;
                    final int n5 = this.z * 16 + (this.changes >> 8 & 0xF);
                    if (Tile.isEntityTile[level.getTile(n3, n4, n5)]) {
                        System.out.println("Sending!");
                        this.broadcast(level.getTileEntity(n3, n4, n5));
                    }
                }
            }
            this.changes = 0;
        }

        private void broadcast(final TileEntity te) {
            if (te != null) {
                final Packet updatePacket = te.getUpdatePacket();
                if (updatePacket != null) {
                    this.broadcast(updatePacket);
                }
            }
        }
    }
}
