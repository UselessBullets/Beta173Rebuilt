// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import net.minecraft.network.packet.AddPaintingPacket;
import net.minecraft.network.packet.MoveEntityPacket;
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
import net.minecraft.network.packet.TeleportEntityPacket;
import java.util.List;
import util.Mth;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.entity.Entity;

public class TrackedEntity
{
    private static final int TOLERANCE_LEVEL = 8;
    public Entity e;
    public int range, updateInterval;
    public int xp, yp, zp, yRotp, xRotp;
    public double xap, yap, zap;
    public int tickCount = 0;
    private double xpu, ypu, zpu;
    private boolean updatedPlayerVisibility = false;
    private boolean trackDelta;
    private int teleportDelay = 0;
    public boolean moved = false;
    public Set<ServerPlayer> seenBy = new HashSet<>();
    
    public TrackedEntity(final Entity e, final int range, final int updateInterval, final boolean trackDelta) {
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
    
    public void tick(final List<Player> players) {
        this.moved = false;
        if (!this.updatedPlayerVisibility || this.e.distanceToSqr(this.xpu, this.ypu, this.zpu) > 16.0) {
            this.xpu = this.e.x;
            this.ypu = this.e.y;
            this.zpu = this.e.z;
            this.updatedPlayerVisibility = true;
            this.moved = true;
            this.updatePlayers(players);
        }

        this.teleportDelay++;
        if (this.tickCount++ % this.updateInterval == 0) {
            final int xn = Mth.floor(this.e.x * 32.0);
            final int yn = Mth.floor(this.e.y * 32.0);
            final int zn = Mth.floor(this.e.z * 32.0);
            final int yRotn = Mth.floor(this.e.yRot * 256.0f / 360.0f);
            final int zRotn = Mth.floor(this.e.xRot * 256.0f / 360.0f);

            final int xa = xn - this.xp;
            final int ya = yn - this.yp;
            final int za = zn - this.zp;

            Packet packet = null;

            final boolean pos = Math.abs(xn) >= TOLERANCE_LEVEL || Math.abs(yn) >= TOLERANCE_LEVEL || Math.abs(zn) >= TOLERANCE_LEVEL;
            final boolean rot = Math.abs(yRotn - this.yRotp) >= TOLERANCE_LEVEL || Math.abs(zRotn - this.xRotp) >= TOLERANCE_LEVEL;

            if (xa < -128 || xa >= 128 || ya < -128 || ya >= 128 || za < -128 || za >= 128 || this.teleportDelay > 400) {
                this.teleportDelay = 0;
                this.e.x = xn / 32.0;
                this.e.y = yn / 32.0;
                this.e.z = zn / 32.0;
                packet = new TeleportEntityPacket(this.e.entityId, xn, yn, zn, (byte)yRotn, (byte)zRotn);
            }
            else if (pos && rot) {
                packet = new MoveEntityPacket.PosRot(this.e.entityId, (byte)xa, (byte)ya, (byte)za, (byte)yRotn, (byte)zRotn);
            }
            else if (pos) {
                packet = new MoveEntityPacket.Pos(this.e.entityId, (byte)xa, (byte)ya, (byte)za);
            }
            else if (rot) {
                packet = new MoveEntityPacket.Rot(this.e.entityId, (byte)yRotn, (byte)zRotn);
            }

            if (this.trackDelta) {
                final double xad = this.e.xd - this.xap;
                final double yad = this.e.yd - this.yap;
                final double zad = this.e.zd - this.zap;

                final double max = 0.02;

                final double diff = xad * xad + yad * yad + zad * zad;

                if (diff > max * max || (diff > 0.0 && this.e.xd == 0.0 && this.e.yd == 0.0 && this.e.zd == 0.0)) {
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

            if (pos) {
                this.xp = xn;
                this.yp = yn;
                this.zp = zn;
            }
            if (rot) {
                this.yRotp = yRotn;
                this.xRotp = zRotn;
            }
        }

        if (this.e.hurtMarked) {
            this.broadcastAndSend(new SetEntityMotionPacket(this.e));
            this.e.hurtMarked = false;
        }
    }
    
    public void broadcast(final Packet packet) {
        for (ServerPlayer serverPlayer : this.seenBy) {
            serverPlayer.connection.send(packet);
        }
    }
    
    public void broadcastAndSend(final Packet packet) {
        this.broadcast(packet);
        if (this.e instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) this.e;
            player.connection.send(packet);
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
        if (sp == this.e) return;

        final double xd = sp.x - this.xp / 32;
        final double zd = sp.z - this.zp / 32;
        if (xd >= -this.range && xd <= this.range && zd >= -this.range && zd <= this.range) {
            if (!this.seenBy.contains(sp)) {
                this.seenBy.add(sp);
                Packet packet = this.getAddEntityPacket();
                sp.connection.send(packet);

                if (this.trackDelta) {
                    sp.connection.send(new SetEntityMotionPacket(this.e.entityId, this.e.xd, this.e.yd, this.e.zd));
                }

                final ItemInstance[] equipped = this.e.getEquipmentSlots();
                if (equipped != null) {
                    for (int i = 0; i < equipped.length; ++i) {
                        sp.connection.send(new SetEquippedItemPacket(this.e.entityId, i, equipped[i]));
                    }
                }

                if (this.e instanceof Player) {
                    Player spe = (Player) this.e;
                    if (spe.isSleeping()) {
                        sp.connection.send(new EntityActionAtPositionPacket(this.e, EntityActionAtPositionPacket.START_SLEEP, Mth.floor(this.e.x), Mth.floor(this.e.y), Mth.floor(this.e.z)));
                    }
                }
            }
        }
        else if (this.seenBy.contains(sp)) {
            this.seenBy.remove(sp);
            sp.connection.send(new RemoveEntityPacket(this.e.entityId));
        }
    }
    
    public void updatePlayers(final List<Player> players) {
        for (int i = 0; i < players.size(); ++i) {
            this.updatePlayer((ServerPlayer)players.get(i));
        }
    }
    
    private Packet getAddEntityPacket() {
        if (this.e instanceof ItemEntity) {
            final ItemEntity item = (ItemEntity)this.e;
            final AddItemEntityPacket packet = new AddItemEntityPacket(item);
            item.x = packet.x / 32.0;
            item.y = packet.y / 32.0;
            item.z = packet.z / 32.0;
            return packet;
        }
        if (this.e instanceof ServerPlayer) {
            Player player = (Player) this.e;
            return new AddPlayerPacket(player);
        }
        if (this.e instanceof Minecart) {
            final Minecart minecart = (Minecart)this.e;
            if (minecart.type == Minecart.RIDEABLE) return new AddEntityPacket(this.e, AddEntityPacket.MINECART_RIDEABLE);
            if (minecart.type == Minecart.CHEST) return new AddEntityPacket(this.e, AddEntityPacket.MINECART_CHEST);
            if (minecart.type == Minecart.FURNACE) return new AddEntityPacket(this.e, AddEntityPacket.MINECART_FURNACE);
        }
        if (this.e instanceof Boat) {
            return new AddEntityPacket(this.e, AddEntityPacket.BOAT);
        }
        if (this.e instanceof Creature) {
            return new AddMobPacket((Mob)this.e);
        }
        if (this.e instanceof FishingHook) {
            return new AddEntityPacket(this.e, AddEntityPacket.FISH_HOOK);
        }
        if (this.e instanceof Arrow) {
            final Mob owner = ((Arrow)this.e).owner;
            return new AddEntityPacket(this.e, AddEntityPacket.ARROW, (owner != null) ? owner.entityId : this.e.entityId);
        }
        if (this.e instanceof Snowball) {
            return new AddEntityPacket(this.e, AddEntityPacket.SNOWBALL);
        }
        if (this.e instanceof Fireball) {
            final Fireball fb = (Fireball)this.e;
            final AddEntityPacket aep = new AddEntityPacket(this.e, AddEntityPacket.FIREBALL, ((Fireball)this.e).owner.entityId);
            aep.xa = (int)(fb.xPower * 8000.0);
            aep.ya = (int)(fb.yPower * 8000.0);
            aep.za = (int)(fb.zPower * 8000.0);
            return aep;
        }
        if (this.e instanceof ThrownEgg) {
            return new AddEntityPacket(this.e, AddEntityPacket.EGG);
        }
        if (this.e instanceof PrimedTnt) {
            return new AddEntityPacket(this.e, AddEntityPacket.PRIMED_TNT);
        }
        if (this.e instanceof FallingTile) {
            final FallingTile ft = (FallingTile)this.e;
            if (ft.tile == Tile.sand.id) return new AddEntityPacket(this.e, AddEntityPacket.FALLING_SAND);
            if (ft.tile == Tile.gravel.id) return new AddEntityPacket(this.e, AddEntityPacket.FALLING_GRAVEL);
        }
        if (this.e instanceof Painting) {
            return new AddPaintingPacket((Painting)this.e);
        }
        throw new IllegalArgumentException("Don't know how to add " + this.e.getClass() + "!");
    }
    
    public void clear(final ServerPlayer sp) {
        if (this.seenBy.contains(sp)) {
            this.seenBy.remove(sp);
            sp.connection.send(new RemoveEntityPacket(this.e.entityId));
        }
    }
}
