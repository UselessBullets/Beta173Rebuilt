// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import net.minecraft.network.packet.DisconnectPacket;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelListener;
import net.minecraft.Pos;
import java.util.HashSet;

import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.storage.MockedLevelStorage;
import java.util.Set;
import util.IntHashMap;
import java.util.LinkedList;
import net.minecraft.world.level.Level;

public class MultiPlayerLevel extends Level
{
    private static final int TICKS_BEFORE_RESET = 80;
    private LinkedList<ResetInfo> updatesToReset = new LinkedList<>();
    private ClientConnection connection;
    private MultiPlayerChunkCache chunkCache;
    private IntHashMap<Entity> entitiesById = new IntHashMap<>();
    private Set<Entity> forced = new HashSet<>();
    private Set<Entity> reEntries = new HashSet<>();
    
    public MultiPlayerLevel(final ClientConnection connection, final long seed, final int dimension) {
        super(new MockedLevelStorage(), "MpServer", Dimension.getNew(dimension), seed);
        this.connection = connection;
        this.setSpawnPos(new Pos(8, 64, 8));
        this.savedDataStorage = connection.savedDataStorage;
    }
    
    @Override
    public void tick() {
        this.setTime(this.getTime() + 1L);
        final int newDark = this.getSkyDarken(1.0f);
        if (newDark != this.skyDarken) {
            this.skyDarken = newDark;
            for (int i = 0; i < this.listeners.size(); ++i) {
                this.listeners.get(i).skyColorChanged();
            }
        }

        for (int i = 0; i < 10 && !this.reEntries.isEmpty(); ++i) {
            final Entity e = this.reEntries.iterator().next();
            if (!this.entities.contains(e)) this.addEntity(e);
        }

        this.connection.tick();
        for (int i = 0; i < this.updatesToReset.size(); ++i) {
            final ResetInfo r = this.updatesToReset.get(i);
            if (--r.ticks == 0) {
                super.setTileAndDataNoUpdate(r.x, r.y, r.z, r.tile, r.data);
                super.sendTileUpdated(r.x, r.y, r.z);

                this.updatesToReset.remove(i);

                i--;
            }
        }
    }
    
    public void clearResetRegion(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        for (int i = 0; i < this.updatesToReset.size(); ++i) {
            final ResetInfo r = this.updatesToReset.get(i);
            if (r.x >= x0 && r.y >= y0 && r.z >= z0 && r.x <= x1 && r.y <= y1 && r.z <= z1) {
                this.updatesToReset.remove(i);
                i--;
            }
        }
    }
    
    @Override
    protected ChunkSource createChunkSource() {
        this.chunkCache = new MultiPlayerChunkCache(this);

        return this.chunkCache;
    }
    
    @Override
    public void validateSpawn() {
        this.setSpawnPos(new Pos(8, 64, 8));
    }
    
    @Override
    protected void tickTiles() {
    }
    
    @Override
    public void addToTickNextTick(final int x, final int y, final int z, final int tileId, final int tickDelay) {
    }
    
    @Override
    public boolean tickPendingTiles(final boolean force) {
        return false;
    }
    
    public void setChunkVisible(final int x, final int z, final boolean visible) {
        if (visible) {
            this.chunkCache.create(x, z);
        }
        else {
            this.chunkCache.drop(x, z);
        }
        if (!visible) {
            this.setTilesDirty(x * 16, 0, z * 16, x * 16 + 15, Level.maxBuildHeight, z * 16 + 15);
        }
    }
    
    @Override
    public boolean addEntity(final Entity e) {
        final boolean ok = super.addEntity(e);
        this.forced.add(e);

        if (!ok) {
            this.reEntries.add(e);
        }

        return ok;
    }
    
    @Override
    public void removeEntity(final Entity e) {
        super.removeEntity(e);
        this.forced.remove(e);
    }
    
    @Override
    protected void entityAdded(final Entity e) {
        super.entityAdded(e);
        if (this.reEntries.contains(e)) {
            this.reEntries.remove(e);
        }
    }
    
    @Override
    protected void entityRemoved(final Entity e) {
        super.entityRemoved(e);
        if (this.forced.contains(e)) {
            this.reEntries.add(e);
        }
    }
    
    public void putEntity(final int id, final Entity e) {
        final Entity old = this.getEntity(id);
        if (old != null) {
            this.removeEntity(old);
        }

        this.forced.add(e);
        e.entityId = id;
        if (!this.addEntity(e)) {
            this.reEntries.add(e);
        }
        this.entitiesById.put(id, e);
    }
    
    public Entity getEntity(final int id) {
        return this.entitiesById.get(id);
    }
    
    public Entity removeEntity(final int id) {
        final Entity e = (Entity)this.entitiesById.remove(id);
        if (e != null) {
            this.forced.remove(e);
            this.removeEntity(e);
        }
        return e;
    }
    
    @Override
    public boolean setDataNoUpdate(final int x, final int y, final int z, final int data) {
        final int t = this.getTile(x, y, z);
        final int d = this.getData(x, y, z);

        if (super.setDataNoUpdate(x, y, z, data)) {
            this.updatesToReset.add(new ResetInfo(x, y, z, t, d));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean setTileAndDataNoUpdate(final int x, final int y, final int z, final int tile, final int data) {
        final int t = this.getTile(x, y, z);
        final int d = this.getData(x, y, z);

        if (super.setTileAndDataNoUpdate(x, y, z, tile, data)) {
            this.updatesToReset.add(new ResetInfo(x, y, z, t, d));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean setTileNoUpdate(final int x, final int y, final int z, final int tile) {
        final int t = this.getTile(x, y, z);
        final int d = this.getData(x, y, z);

        if (super.setTileNoUpdate(x, y, z, tile)) {
            this.updatesToReset.add(new ResetInfo(x, y, z, t, d));
            return true;
        }
        return false;
    }
    
    public boolean doSetTileAndData(final int x, final int y, final int z, final int tile, final int data) {
        this.clearResetRegion(x, y, z, x, y, z);

        if (super.setTileAndDataNoUpdate(x, y, z, tile, data)) {
            this.tileUpdated(x, y, z, tile);
            return true;
        }
        return false;
    }
    
    @Override
    public void disconnect() {
        this.connection.sendAndDisconnect(new DisconnectPacket("Quitting"));
    }
    
    @Override
    protected void tickWeather() {
        if (this.dimension.hasCeiling) return;

        if (this.lightningTime > 0) {
            --this.lightningTime;
        }

        this.oRainLevel = this.rainLevel;
        if (this.levelData.isRaining()) {
            this.rainLevel += 0.01f;
        }
        else {
            this.rainLevel -= 0.01f;
        }
        if (this.rainLevel < 0.0f) this.rainLevel = 0.0f;
        if (this.rainLevel > 1.0f) this.rainLevel = 1.0f;

        this.oThunderLevel = this.thunderLevel;
        if (this.levelData.isThundering()) {
            this.thunderLevel += 0.01f;
        }
        else {
            this.thunderLevel -= 0.01f;
        }
        if (this.thunderLevel < 0.0f) this.thunderLevel = 0.0f;
        if (this.thunderLevel > 1.0f) this.thunderLevel = 1.0f;
    }

    static class ResetInfo
    {
        int x;
        int y;
        int z;
        int ticks;
        int tile;
        int data;

        public ResetInfo(final int x, final int y, final int z, final int tile, final int data) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.ticks = TICKS_BEFORE_RESET;
            this.tile = tile;
            this.data = data;
        }
    }
}
