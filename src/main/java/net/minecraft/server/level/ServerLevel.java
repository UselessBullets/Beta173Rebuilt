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
        return this.cache = new ServerChunkCache(this, this.levelStorage.createChunkStorage(this.dimension), this.dimension.createRandomLevelSource());
    }
    
    public List<TileEntity> getTileEntitiesInRegion(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
        final ArrayList<TileEntity> list = new ArrayList<>();
        for (int i = 0; i < this.tileEntityList.size(); ++i) {
            final TileEntity tileEntity = this.tileEntityList.get(i);
            if (tileEntity.x >= x0 && tileEntity.y >= y0 && tileEntity.z >= z0 && tileEntity.x < x1 && tileEntity.y < y1 && tileEntity.z < z1) {
                list.add(tileEntity);
            }
        }
        return list;
    }
    
    @Override
    public boolean mayInteract(final Player player, final int xt, final int yt, final int zt) {
        final int n = (int)Mth.abs((float)(xt - this.levelData.getXSpawn()));
        int n2 = (int)Mth.abs((float)(zt - this.levelData.getZSpawn()));
        if (n > n2) {
            n2 = n;
        }
        return n2 > 16 || this.server.players.isOp(player.name);
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
        return (Entity)this.entitiesById.get(id);
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
        this.server.getTracker(this.dimension.id).broadcastAndSend(e, new EntityEventPacket(e.entityId, event));
    }
    
    @Override
    public Explosion explode(final Entity source, final double x, final double y, final double z, final float r, final boolean fire) {
        final Explosion explosion = new Explosion(this, source, x, y, z, r);
        explosion.fire = fire;
        explosion.explode();
        explosion.addParticles(false);
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
        final boolean raining = this.isRaining();
        super.tickWeather();
        if (raining != this.isRaining()) {
            if (raining) {
                this.server.players.broadcastAll(new GameEventPacket(GameEventPacket.STOP_RAINING));
            }
            else {
                this.server.players.broadcastAll(new GameEventPacket(GameEventPacket.START_RAINING));
            }
        }
    }
}
