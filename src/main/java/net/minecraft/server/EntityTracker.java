// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import net.minecraft.network.packet.Packet;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.world.entity.Painting;
import net.minecraft.world.entity.item.FallingTile;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.Creature;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.item.Boat;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import java.util.HashSet;
import util.IntHashMap;
import java.util.Set;

public class EntityTracker
{
    private Set<TrackedEntity> entities = new HashSet<>();
    private IntHashMap<TrackedEntity> entityMap = new IntHashMap<>();
    private MinecraftServer server;
    private int maxRange;
    private int dimension;
    
    public EntityTracker(final MinecraftServer server, final int dimension) {
        this.server = server;
        this.dimension = dimension;
        this.maxRange = server.players.getMaxRange();
    }
    
    public void addEntity(final Entity e) {
        if (e instanceof ServerPlayer) {
            this.addEntity(e, 32 * 16, 2);
            final ServerPlayer player = (ServerPlayer)e;
            for (final TrackedEntity te : this.entities) {
                if (te.e != player) {
                    te.updatePlayer(player);
                }
            }
        }
        else if (e instanceof FishingHook) this.addEntity(e, 16 * 4, 5, true);
        else if (e instanceof Arrow) this.addEntity(e, 16 * 4, 20, false);
        else if (e instanceof Fireball) this.addEntity(e, 16 * 4, 10, false);
        else if (e instanceof Snowball) this.addEntity(e, 16 * 4, 10, true);
        else if (e instanceof ThrownEgg) this.addEntity(e, 16 * 4, 10, true);
        else if (e instanceof ItemEntity) this.addEntity(e, 16 * 4, 20, true);
        else if (e instanceof Minecart) this.addEntity(e, 16 * 10, 5, true);
        else if (e instanceof Boat) this.addEntity(e, 16 * 10, 5, true);
        else if (e instanceof Squid) this.addEntity(e, 16 * 10, 3, true);
        else if (e instanceof Creature) this.addEntity(e, 16 * 10, 3);
        else if (e instanceof PrimedTnt) this.addEntity(e, 16 * 10, 10, true);
        else if (e instanceof FallingTile) this.addEntity(e, 16 * 10, 20, true);
        else if (e instanceof Painting) this.addEntity(e, 16 * 10, Integer.MAX_VALUE, false);
    }
    
    public void addEntity(final Entity e, final int range, final int updateInterval) {
        this.addEntity(e, range, updateInterval, false);
    }
    
    public void addEntity(final Entity e, int range, final int updateInterval, final boolean trackDeltas) {
        if (range > this.maxRange) range = this.maxRange;
        if (this.entityMap.containsKey(e.entityId)) throw new IllegalStateException("Entity is already tracked!");

        final TrackedEntity te = new TrackedEntity(e, range, updateInterval, trackDeltas);
        this.entities.add(te);
        this.entityMap.put(e.entityId, te);
        te.updatePlayers(this.server.getLevel(this.dimension).players);
    }
    
    public void removeEntity(final Entity e) {
        if (e instanceof ServerPlayer) {
            final ServerPlayer player = (ServerPlayer)e;
            for (TrackedEntity te : this.entities) {
                te.removePlayer(player);
            }
        }

        final TrackedEntity te = (TrackedEntity)this.entityMap.remove(e.entityId);
        if (te != null) {
            this.entities.remove(te);
            te.broadcastRemoved();
        }
    }
    
    public void tick() {
        final ArrayList<Entity> movedPlayers = new ArrayList<>();
        for (final TrackedEntity te : this.entities) {
            te.tick(this.server.getLevel(this.dimension).players);
            if (te.moved && te.e instanceof ServerPlayer) {
                movedPlayers.add(te.e);
            }
        }

        for (int i = 0; i < movedPlayers.size(); ++i) {
            final ServerPlayer player = (ServerPlayer)movedPlayers.get(i);
            for (final TrackedEntity te : this.entities) {
                if (te.e != player) {
                    te.updatePlayer(player);
                }
            }
        }
    }
    
    public void broadcast(final Entity e, final Packet packet) {
        final TrackedEntity te = this.entityMap.get(e.entityId);
        if (te != null) {
            te.broadcast(packet);
        }
    }
    
    public void broadcastAndSend(final Entity e, final Packet packet) {
        final TrackedEntity te = this.entityMap.get(e.entityId);
        if (te != null) {
            te.broadcastAndSend(packet);
        }
    }
    
    public void clear(final ServerPlayer serverPlayer) {
        for (TrackedEntity te : this.entities) {
            te.clear(serverPlayer);
        }
    }
}
