// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import net.minecraft.network.packet.AddPaintingPacket;
import net.minecraft.world.entity.Painting;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.item.FallingTile;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.network.packet.AddMobPacket;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Creature;
import net.minecraft.world.entity.item.Boat;
import net.minecraft.network.packet.AddEntityPacket;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.network.packet.AddPlayerPacket;
import net.minecraft.network.packet.AddItemEntityPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.network.packet.EntityActionAtPositionPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.packet.SetEquippedItemPacket;
import net.minecraft.network.packet.RemoveEntityPacket;
import java.util.Iterator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SynchedEntityData;
import net.minecraft.network.packet.SetEntityDataPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.SetEntityMotionPacket;
import net.minecraft.network.packet.MoveEntityPacket_Rot;
import net.minecraft.network.packet.MoveEntityPacket_Pos;
import net.minecraft.network.packet.MoveEntityPacket_PosRot;
import net.minecraft.network.packet.TeleportEntityPacket;
import java.util.List;
import util.Mth;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.entity.Entity;

public class TrackedEntity
{
    public Entity e;
    public int range;
    public int updateInterval;
    public int xp;
    public int yp;
    public int zp;
    public int yRotp;
    public int xRotp;
    public double xap;
    public double yap;
    public double zap;
    public int tickCount;
    private double xpu;
    private double ypu;
    private double zpu;
    private boolean updatedPlayerVisibility;
    private boolean trackDelta;
    private int teleportDelay;
    public boolean moved;
    public Set seenBy;
    
    public TrackedEntity(final Entity e, final int range, final int updateInterval, final boolean trackDelta) {
        this.tickCount = 0;
        this.updatedPlayerVisibility = false;
        this.teleportDelay = 0;
        this.moved = false;
        this.seenBy = new HashSet();
        this.e = e;
        this.range = range;
        this.updateInterval = updateInterval;
        this.trackDelta = trackDelta;
        this.xp = Mth.floor(e.x * 32.0);
        this.yp = Mth.floor(e.y * 32.0);
        this.zp = Mth.floor(e.z * 32.0);
        this.yRotp = Mth.floor(e.yRot * 256.0f / 360.0f);
        this.xRotp = Mth.floor(e.xRot * 256.0f / 360.0f);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof TrackedEntity && ((TrackedEntity)o).e.entityId == this.e.entityId;
    }
    
    @Override
    public int hashCode() {
        return this.e.entityId;
    }
    
    public void tick(final List players) {
        this.moved = false;
        if (!this.updatedPlayerVisibility || this.e.distanceToSqr(this.xpu, this.ypu, this.zpu) > 16.0) {
            this.xpu = this.e.x;
            this.ypu = this.e.y;
            this.zpu = this.e.z;
            this.updatedPlayerVisibility = true;
            this.moved = true;
            this.updatePlayers(players);
        }
        ++this.teleportDelay;
        if (++this.tickCount % this.updateInterval == 0) {
            final int floor = Mth.floor(this.e.x * 32.0);
            final int floor2 = Mth.floor(this.e.y * 32.0);
            final int floor3 = Mth.floor(this.e.z * 32.0);
            final int floor4 = Mth.floor(this.e.yRot * 256.0f / 360.0f);
            final int floor5 = Mth.floor(this.e.xRot * 256.0f / 360.0f);
            final int n = floor - this.xp;
            final int n2 = floor2 - this.yp;
            final int n3 = floor3 - this.zp;
            Packet packet = null;
            final boolean b = Math.abs(floor) >= 8 || Math.abs(floor2) >= 8 || Math.abs(floor3) >= 8;
            final boolean b2 = Math.abs(floor4 - this.yRotp) >= 8 || Math.abs(floor5 - this.xRotp) >= 8;
            if (n < -128 || n >= 128 || n2 < -128 || n2 >= 128 || n3 < -128 || n3 >= 128 || this.teleportDelay > 400) {
                this.teleportDelay = 0;
                this.e.x = floor / 32.0;
                this.e.y = floor2 / 32.0;
                this.e.z = floor3 / 32.0;
                packet = new TeleportEntityPacket(this.e.entityId, floor, floor2, floor3, (byte)floor4, (byte)floor5);
            }
            else if (b && b2) {
                packet = new MoveEntityPacket_PosRot(this.e.entityId, (byte)n, (byte)n2, (byte)n3, (byte)floor4, (byte)floor5);
            }
            else if (b) {
                packet = new MoveEntityPacket_Pos(this.e.entityId, (byte)n, (byte)n2, (byte)n3);
            }
            else if (b2) {
                packet = new MoveEntityPacket_Rot(this.e.entityId, (byte)floor4, (byte)floor5);
            }
            if (this.trackDelta) {
                final double n4 = this.e.xd - this.xap;
                final double n5 = this.e.yd - this.yap;
                final double n6 = this.e.zd - this.zap;
                final double n7 = 0.02;
                final double n8 = n4 * n4 + n5 * n5 + n6 * n6;
                if (n8 > n7 * n7 || (n8 > 0.0 && this.e.xd == 0.0 && this.e.yd == 0.0 && this.e.zd == 0.0)) {
                    this.xap = this.e.xd;
                    this.yap = this.e.yd;
                    this.zap = this.e.zd;
                    this.broadcast(new SetEntityMotionPacket(this.e.entityId, this.xap, this.yap, this.zap));
                }
            }
            if (packet != null) {
                this.broadcast(packet);
            }
            final SynchedEntityData entityData = this.e.getEntityData();
            if (entityData.isDirty()) {
                this.broadcastAndSend(new SetEntityDataPacket(this.e.entityId, entityData));
            }
            if (b) {
                this.xp = floor;
                this.yp = floor2;
                this.zp = floor3;
            }
            if (b2) {
                this.yRotp = floor4;
                this.xRotp = floor5;
            }
        }
        if (this.e.hurtMarked) {
            this.broadcastAndSend(new SetEntityMotionPacket(this.e));
            this.e.hurtMarked = false;
        }
    }
    
    public void broadcast(final Packet packet) {
        final Iterator iterator = this.seenBy.iterator();
        while (iterator.hasNext()) {
            ((ServerPlayer)iterator.next()).connection.send(packet);
        }
    }
    
    public void broadcastAndSend(final Packet packet) {
        this.broadcast(packet);
        if (this.e instanceof ServerPlayer) {
            ((ServerPlayer)this.e).connection.send(packet);
        }
    }
    
    public void broadcastRemoved() {
        this.broadcast(new RemoveEntityPacket(this.e.entityId));
    }
    
    public void removePlayer(final ServerPlayer sp) {
        if (this.seenBy.contains(sp)) {
            this.seenBy.remove(sp);
        }
    }
    
    public void updatePlayer(final ServerPlayer sp) {
        if (sp == this.e) {
            return;
        }
        final double n = sp.x - this.xp / 32;
        final double n2 = sp.z - this.zp / 32;
        if (n >= -this.range && n <= this.range && n2 >= -this.range && n2 <= this.range) {
            if (!this.seenBy.contains(sp)) {
                this.seenBy.add(sp);
                sp.connection.send(this.getAddEntityPacket());
                if (this.trackDelta) {
                    sp.connection.send(new SetEntityMotionPacket(this.e.entityId, this.e.xd, this.e.yd, this.e.zd));
                }
                final ItemInstance[] equipmentSlots = this.e.getEquipmentSlots();
                if (equipmentSlots != null) {
                    for (int i = 0; i < equipmentSlots.length; ++i) {
                        sp.connection.send(new SetEquippedItemPacket(this.e.entityId, i, equipmentSlots[i]));
                    }
                }
                if (this.e instanceof Player && ((Player)this.e).isSleeping()) {
                    sp.connection.send(new EntityActionAtPositionPacket(this.e, 0, Mth.floor(this.e.x), Mth.floor(this.e.y), Mth.floor(this.e.z)));
                }
            }
        }
        else if (this.seenBy.contains(sp)) {
            this.seenBy.remove(sp);
            sp.connection.send(new RemoveEntityPacket(this.e.entityId));
        }
    }
    
    public void updatePlayers(final List players) {
        for (int i = 0; i < players.size(); ++i) {
            this.updatePlayer((ServerPlayer)players.get(i));
        }
    }
    
    private Packet getAddEntityPacket() {
        if (this.e instanceof ItemEntity) {
            final ItemEntity itemEntity = (ItemEntity)this.e;
            final AddItemEntityPacket addItemEntityPacket = new AddItemEntityPacket(itemEntity);
            itemEntity.x = addItemEntityPacket.x / 32.0;
            itemEntity.y = addItemEntityPacket.y / 32.0;
            itemEntity.z = addItemEntityPacket.z / 32.0;
            return addItemEntityPacket;
        }
        if (this.e instanceof ServerPlayer) {
            return new AddPlayerPacket((Player)this.e);
        }
        if (this.e instanceof Minecart) {
            final Minecart minecart = (Minecart)this.e;
            if (minecart.type == 0) {
                return new AddEntityPacket(this.e, 10);
            }
            if (minecart.type == 1) {
                return new AddEntityPacket(this.e, 11);
            }
            if (minecart.type == 2) {
                return new AddEntityPacket(this.e, 12);
            }
        }
        if (this.e instanceof Boat) {
            return new AddEntityPacket(this.e, 1);
        }
        if (this.e instanceof Creature) {
            return new AddMobPacket((Mob)this.e);
        }
        if (this.e instanceof FishingHook) {
            return new AddEntityPacket(this.e, 90);
        }
        if (this.e instanceof Arrow) {
            final Mob owner = ((Arrow)this.e).owner;
            return new AddEntityPacket(this.e, 60, (owner != null) ? owner.entityId : this.e.entityId);
        }
        if (this.e instanceof Snowball) {
            return new AddEntityPacket(this.e, 61);
        }
        if (this.e instanceof Fireball) {
            final Fireball fireball = (Fireball)this.e;
            final AddEntityPacket addEntityPacket = new AddEntityPacket(this.e, 63, ((Fireball)this.e).owner.entityId);
            addEntityPacket.xa = (int)(fireball.xPower * 8000.0);
            addEntityPacket.ya = (int)(fireball.yPower * 8000.0);
            addEntityPacket.za = (int)(fireball.zPower * 8000.0);
            return addEntityPacket;
        }
        if (this.e instanceof ThrownEgg) {
            return new AddEntityPacket(this.e, 62);
        }
        if (this.e instanceof PrimedTnt) {
            return new AddEntityPacket(this.e, 50);
        }
        if (this.e instanceof FallingTile) {
            final FallingTile fallingTile = (FallingTile)this.e;
            if (fallingTile.tile == Tile.sand.id) {
                return new AddEntityPacket(this.e, 70);
            }
            if (fallingTile.tile == Tile.gravel.id) {
                return new AddEntityPacket(this.e, 71);
            }
        }
        if (this.e instanceof Painting) {
            return new AddPaintingPacket((Painting)this.e);
        }
        throw new IllegalArgumentException("Don't know how to add " + this.e.getClass() + "!");
    }
    
    public void clear(final ServerPlayer dl) {
        if (this.seenBy.contains(dl)) {
            this.seenBy.remove(dl);
            dl.connection.send(new RemoveEntityPacket(this.e.entityId));
        }
    }
}
