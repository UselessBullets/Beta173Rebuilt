// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.level;

import net.minecraft.network.packet.GameEventPacket;
import net.minecraft.network.packet.TileEventPacket;
import net.minecraft.network.packet.ExplodePacket;
import net.minecraft.world.level.Explosion;
import net.minecraft.network.packet.EntityEventPacket;
import net.minecraft.network.packet.AddGlobalEntityPacketPacket;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import util.Mth;
import net.minecraft.world.level.tile.entity.TileEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.storage.LevelStorage;
import util.IntHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

public class ServerLevel extends Level
{
    public ServerChunkCache cache;
    public boolean canEditSpawn;
    public boolean noSave;
    private MinecraftServer server;
    private IntHashMap<Entity> entitiesById;
    
    public ServerLevel(final MinecraftServer server, final LevelStorage levelStorage, final String levelName, final int dimension, final long seed) {
        super(levelStorage, levelName, seed, Dimension.getNew(dimension));
        this.canEditSpawn = false;
        this.entitiesById = new IntHashMap<>();
        this.server = server;
    }
    
    @Override
    public void tick(final Entity e, final boolean actual) {
        if (!this.server.isAnimals && (e instanceof Animal || e instanceof WaterAnimal)) {
            e.remove();
        }
        if (e.rider == null || !(e.rider instanceof Player)) {
            super.tick(e, actual);
        }
    }
    
    public void forceTick(final Entity e, final boolean actual) {
        super.tick(e, actual);
    }
    
    @Override
    protected ChunkSource createChunkSource() {
        ChunkStorage storage = this.levelStorage.createChunkStorage(this.dimension);
        this.cache = new ServerChunkCache(this, storage, this.dimension.createRandomLevelSource());
        return this.cache;
    }
    
    public List<TileEntity> getTileEntitiesInRegion(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        final ArrayList<TileEntity> result = new ArrayList<>();
        for (int i = 0; i < this.tileEntityList.size(); ++i) {
            final TileEntity te = this.tileEntityList.get(i);
            if (te.x >= x0 && te.y >= y0 && te.z >= z0 && te.x < x1 && te.y < y1 && te.z < z1) {
                result.add(te);
            }
        }
        return result;
    }
    
    @Override
    public boolean mayInteract(final Player player, final int xt, final int yt, final int zt) {
        int xd = (int)Mth.abs((float)(xt - this.levelData.getXSpawn()));
        int zd = (int)Mth.abs((float)(zt - this.levelData.getZSpawn()));
        if (xd > zd) zd = xd;
        return zd > 16 || this.server.players.isOp(player.name);
    }
    
    @Override
    protected void entityAdded(final Entity e) {
        super.entityAdded(e);
        this.entitiesById.put(e.entityId, e);
    }
    
    @Override
    protected void entityRemoved(final Entity e) {
        super.entityRemoved(e);
        this.entitiesById.remove(e.entityId);
    }
    
    public Entity getEntity(final int id) {
        return this.entitiesById.get(id);
    }
    
    @Override
    public boolean addGlobalEntity(final Entity e) {
        if (super.addGlobalEntity(e)) {
            this.server.players.broadcast(e.x, e.y, e.z, 512.0, this.dimension.id, new AddGlobalEntityPacketPacket(e));
            return true;
        }
        return false;
    }
    
    @Override
    public void broadcastEntityEvent(final Entity e, final byte event) {
        EntityEventPacket p = new EntityEventPacket(e.entityId, event);
        this.server.getTracker(this.dimension.id).broadcastAndSend(e, p);
    }
    
    @Override
    public Explosion explode(final Entity source, final double x, final double y, final double z, final float r, final boolean fire) {
        // instead of calling super, we run the same explosion code here except
        // we don't generate any particles
        final Explosion explosion = new Explosion(this, source, x, y, z, r);
        explosion.fire = fire;
        explosion.explode();
        explosion.finalizeExplosion(false);

        this.server.players.broadcast(x, y, z, 64.0, this.dimension.id, new ExplodePacket(x, y, z, r, explosion.toBlow));
        return explosion;
    }
    
    @Override
    public void tileEvent(final int x, final int y, final int z, final int b0, final int b1) {
        super.tileEvent(x, y, z, b0, b1);
        this.server.players.broadcast(x, y, z, 64.0, this.dimension.id, new TileEventPacket(x, y, z, b0, b1));
    }
    
    public void closeLevelStorage() {
        this.levelStorage.closeAll();
    }
    
    @Override
    protected void tickWeather() {
        final boolean wasRaining = this.isRaining();
        super.tickWeather();
        if (wasRaining != this.isRaining()) {
            if (wasRaining) {
                this.server.players.broadcastAll(new GameEventPacket(GameEventPacket.STOP_RAINING));
            }
            else {
                this.server.players.broadcastAll(new GameEventPacket(GameEventPacket.START_RAINING));
            }
        }
    }
}
