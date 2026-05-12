// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.players;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import java.util.ArrayList;
import net.minecraft.server.MinecraftServer;
import util.LongHashMap;
import java.util.List;

public class PlayerChunkMap
{
    public List players;
    private LongHashMap chunks;
    private List changedChunks;
    private MinecraftServer server;
    private int dimension;
    private int radius;
    private final int[][] direction;
    
    public PlayerChunkMap(final MinecraftServer server, final int dimension, final int radius) {
        this.players = new ArrayList();
        this.chunks = new LongHashMap();
        this.changedChunks = new ArrayList();
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
            ((PlayerChunkMap_PlayerChunk)this.changedChunks.get(i)).broadcastChanges();
        }
        this.changedChunks.clear();
    }
    
    private PlayerChunkMap_PlayerChunk getChunk(final int x, final int z, final boolean create) {
        final long n = x + 2147483647L | z + 2147483647L << 32;
        PlayerChunkMap_PlayerChunk value = (PlayerChunkMap_PlayerChunk)this.chunks.get(n);
        if (value == null && create) {
            value = new PlayerChunkMap_PlayerChunk(this, x, z);
            this.chunks.put(n, value);
        }
        return value;
    }
    
    public void isTrackingTile(final int x, final int y, final int z) {
        final PlayerChunkMap_PlayerChunk chunk = this.getChunk(x >> 4, z >> 4, false);
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
                final PlayerChunkMap_PlayerChunk chunk = this.getChunk(i, j, false);
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
                    final PlayerChunkMap_PlayerChunk chunk = this.getChunk(i - n3, j - n4, false);
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
}
