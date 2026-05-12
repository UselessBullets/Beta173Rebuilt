// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.DisconnectPacket;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelListener;
import net.minecraft.Pos;
import java.util.HashSet;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.storage.MockedLevelStorage;
import java.util.Set;
import util.IntHashMap;
import java.util.LinkedList;
import net.minecraft.world.level.Level;

public class MultiPlayerLevel extends Level
{
    private LinkedList updatesToReset;
    private ClientConnection connection;
    private MultiPlayerChunkCache chunkCache;
    private IntHashMap entitiesById;
    private Set forced;
    private Set reEntries;
    
    public MultiPlayerLevel(final ClientConnection connection, final long seed, final int dimension) {
        super(new MockedLevelStorage(), "MpServer", Dimension.getNew(dimension), seed);
        this.updatesToReset = new LinkedList();
        this.entitiesById = new IntHashMap();
        this.forced = new HashSet();
        this.reEntries = new HashSet();
        this.connection = connection;
        this.setSpawnPos(new Pos(8, 64, 8));
        this.savedDataStorage = connection.savedDataStorage;
    }
    
    @Override
    public void tick() {
        this.setTime(this.getTime() + 1L);
        final int skyDarken = this.getSkyDarken(1.0f);
        if (skyDarken != this.skyDarken) {
            this.skyDarken = skyDarken;
            for (int i = 0; i < this.listeners.size(); ++i) {
                ((LevelListener)this.listeners.get(i)).skyColorChanged();
            }
        }
        for (int n = 0; n < 10 && !this.reEntries.isEmpty(); ++n) {
            final Entity e = this.reEntries.iterator().next();
            if (!this.entities.contains(e)) {
                this.addEntity(e);
            }
        }
        this.connection.tick();
        for (int j = 0; j < this.updatesToReset.size(); ++j) {
            final MultiPlayerLevel_ResetInfo multiPlayerLevel_ResetInfo2;
            final MultiPlayerLevel_ResetInfo multiPlayerLevel_ResetInfo = multiPlayerLevel_ResetInfo2 = this.updatesToReset.get(j);
            if (--multiPlayerLevel_ResetInfo2.ticks == 0) {
                super.setTileAndDataNoUpdate(multiPlayerLevel_ResetInfo.x, multiPlayerLevel_ResetInfo.y, multiPlayerLevel_ResetInfo.z, multiPlayerLevel_ResetInfo.tile, multiPlayerLevel_ResetInfo.data);
                super.sendTileUpdated(multiPlayerLevel_ResetInfo.x, multiPlayerLevel_ResetInfo.y, multiPlayerLevel_ResetInfo.z);
                this.updatesToReset.remove(j--);
            }
        }
    }
    
    public void clearResetRegion(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        for (int i = 0; i < this.updatesToReset.size(); ++i) {
            final MultiPlayerLevel_ResetInfo multiPlayerLevel_ResetInfo = this.updatesToReset.get(i);
            if (multiPlayerLevel_ResetInfo.x >= x0 && multiPlayerLevel_ResetInfo.y >= y0 && multiPlayerLevel_ResetInfo.z >= z0 && multiPlayerLevel_ResetInfo.x <= x1 && multiPlayerLevel_ResetInfo.y <= y1 && multiPlayerLevel_ResetInfo.z <= z1) {
                this.updatesToReset.remove(i--);
            }
        }
    }
    
    @Override
    protected ChunkSource createChunkSource() {
        return this.chunkCache = new MultiPlayerChunkCache(this);
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
            this.setTilesDirty(x * 16, 0, z * 16, x * 16 + 15, 128, z * 16 + 15);
        }
    }
    
    @Override
    public boolean addEntity(final Entity e) {
        final boolean addEntity = super.addEntity(e);
        this.forced.add(e);
        if (!addEntity) {
            this.reEntries.add(e);
        }
        return addEntity;
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
        final Entity entity = this.getEntity(id);
        if (entity != null) {
            this.removeEntity(entity);
        }
        this.forced.add(e);
        e.entityId = id;
        if (!this.addEntity(e)) {
            this.reEntries.add(e);
        }
        this.entitiesById.put(id, e);
    }
    
    public Entity getEntity(final int id) {
        return (Entity)this.entitiesById.get(id);
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
        final int tile = this.getTile(x, y, z);
        final int data2 = this.getData(x, y, z);
        if (super.setDataNoUpdate(x, y, z, data)) {
            this.updatesToReset.add(new MultiPlayerLevel_ResetInfo(this, x, y, z, tile, data2));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean setTileAndDataNoUpdate(final int x, final int y, final int z, final int tile, final int data) {
        final int tile2 = this.getTile(x, y, z);
        final int data2 = this.getData(x, y, z);
        if (super.setTileAndDataNoUpdate(x, y, z, tile, data)) {
            this.updatesToReset.add(new MultiPlayerLevel_ResetInfo(this, x, y, z, tile2, data2));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean setTileNoUpdate(final int x, final int y, final int z, final int tile) {
        final int tile2 = this.getTile(x, y, z);
        final int data = this.getData(x, y, z);
        if (super.setTileNoUpdate(x, y, z, tile)) {
            this.updatesToReset.add(new MultiPlayerLevel_ResetInfo(this, x, y, z, tile2, data));
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
        if (this.dimension.hasCeiling) {
            return;
        }
        if (this.lightningTime > 0) {
            --this.lightningTime;
        }
        this.oRainLevel = this.rainLevel;
        if (this.levelData.isRaining()) {
            this.rainLevel += (float)0.01;
        }
        else {
            this.rainLevel -= (float)0.01;
        }
        if (this.rainLevel < 0.0f) {
            this.rainLevel = 0.0f;
        }
        if (this.rainLevel > 1.0f) {
            this.rainLevel = 1.0f;
        }
        this.oThunderLevel = this.thunderLevel;
        if (this.levelData.isThundering()) {
            this.thunderLevel += (float)0.01;
        }
        else {
            this.thunderLevel -= (float)0.01;
        }
        if (this.thunderLevel < 0.0f) {
            this.thunderLevel = 0.0f;
        }
        if (this.thunderLevel > 1.0f) {
            this.thunderLevel = 1.0f;
        }
    }
}
