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
    private Set entities;
    private IntHashMap entityMap;
    private MinecraftServer server;
    private int maxRange;
    private int dimension;
    
    public EntityTracker(final MinecraftServer server, final int dimension) {
        this.entities = new HashSet();
        this.entityMap = new IntHashMap();
        this.server = server;
        this.dimension = dimension;
        this.maxRange = server.players.getMaxRange();
    }
    
    public void addEntity(final Entity e) {
        if (e instanceof ServerPlayer) {
            this.addEntity(e, 512, 2);
            final ServerPlayer sp = (ServerPlayer)e;
            for (final TrackedEntity trackedEntity : this.entities) {
                if (trackedEntity.e != sp) {
                    trackedEntity.updatePlayer(sp);
                }
            }
        }
        else if (e instanceof FishingHook) {
            this.addEntity(e, 64, 5, true);
        }
        else if (e instanceof Arrow) {
            this.addEntity(e, 64, 20, false);
        }
        else if (e instanceof Fireball) {
            this.addEntity(e, 64, 10, false);
        }
        else if (e instanceof Snowball) {
            this.addEntity(e, 64, 10, true);
        }
        else if (e instanceof ThrownEgg) {
            this.addEntity(e, 64, 10, true);
        }
        else if (e instanceof ItemEntity) {
            this.addEntity(e, 64, 20, true);
        }
        else if (e instanceof Minecart) {
            this.addEntity(e, 160, 5, true);
        }
        else if (e instanceof Boat) {
            this.addEntity(e, 160, 5, true);
        }
        else if (e instanceof Squid) {
            this.addEntity(e, 160, 3, true);
        }
        else if (e instanceof Creature) {
            this.addEntity(e, 160, 3);
        }
        else if (e instanceof PrimedTnt) {
            this.addEntity(e, 160, 10, true);
        }
        else if (e instanceof FallingTile) {
            this.addEntity(e, 160, 20, true);
        }
        else if (e instanceof Painting) {
            this.addEntity(e, 160, Integer.MAX_VALUE, false);
        }
    }
    
    public void addEntity(final Entity e, final int range, final int updateInterval) {
        this.addEntity(e, range, updateInterval, false);
    }
    
    public void addEntity(final Entity e, int range, final int updateInterval, final boolean trackDeltas) {
        if (range > this.maxRange) {
            range = this.maxRange;
        }
        if (this.entityMap.containsKey(e.entityId)) {
            throw new IllegalStateException("Entity is already tracked!");
        }
        final TrackedEntity value = new TrackedEntity(e, range, updateInterval, trackDeltas);
        this.entities.add(value);
        this.entityMap.put(e.entityId, value);
        value.updatePlayers(this.server.getLevel(this.dimension).players);
    }
    
    public void removePlayer(final Entity e) {
        if (e instanceof ServerPlayer) {
            final ServerPlayer sp = (ServerPlayer)e;
            final Iterator iterator = this.entities.iterator();
            while (iterator.hasNext()) {
                ((TrackedEntity)iterator.next()).removePlayer(sp);
            }
        }
        final TrackedEntity trackedEntity = (TrackedEntity)this.entityMap.remove(e.entityId);
        if (trackedEntity != null) {
            this.entities.remove(trackedEntity);
            trackedEntity.broadcastRemoved();
        }
    }
    
    public void tick() {
        final ArrayList list = new ArrayList();
        for (final TrackedEntity trackedEntity : this.entities) {
            trackedEntity.tick(this.server.getLevel(this.dimension).players);
            if (trackedEntity.moved && trackedEntity.e instanceof ServerPlayer) {
                list.add(trackedEntity.e);
            }
        }
        for (int i = 0; i < list.size(); ++i) {
            final ServerPlayer sp = (ServerPlayer)list.get(i);
            for (final TrackedEntity trackedEntity2 : this.entities) {
                if (trackedEntity2.e != sp) {
                    trackedEntity2.updatePlayer(sp);
                }
            }
        }
    }
    
    public void broadcast(final Entity e, final Packet packet) {
        final TrackedEntity trackedEntity = (TrackedEntity)this.entityMap.get(e.entityId);
        if (trackedEntity != null) {
            trackedEntity.broadcast(packet);
        }
    }
    
    public void broadcastAndSend(final Entity e, final Packet packet) {
        final TrackedEntity trackedEntity = (TrackedEntity)this.entityMap.get(e.entityId);
        if (trackedEntity != null) {
            trackedEntity.broadcastAndSend(packet);
        }
    }
    
    public void clear(final ServerPlayer serverPlayer) {
        final Iterator iterator = this.entities.iterator();
        while (iterator.hasNext()) {
            ((TrackedEntity)iterator.next()).clear(serverPlayer);
        }
    }
}
