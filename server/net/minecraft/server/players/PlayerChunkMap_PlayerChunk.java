// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.players;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.packet.ChunkTilesUpdatePacket;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.network.packet.BlockRegionUpdatePacket;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.network.packet.TileUpdatePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.ChunkVisibilityPacket;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import net.minecraft.world.level.ChunkPos;
import java.util.List;

class PlayerChunkMap_PlayerChunk
{
    private List players;
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
    
    public PlayerChunkMap_PlayerChunk(final PlayerChunkMap pcm, final int x, final int z) {
        this.playerChunkMap = pcm;
        this.players = new ArrayList();
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
