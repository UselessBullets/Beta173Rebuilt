// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import net.minecraft.SharedConstants;
import net.minecraft.network.packet.AwardStatPacket;
import net.minecraft.network.packet.LevelEventPacket;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.Item;
import net.minecraft.network.packet.ComplexItemDataPacket;
import net.minecraft.network.packet.GameEventPacket;
import net.minecraft.network.packet.TileEventPacket;
import net.minecraft.network.packet.ContainerClosePacket;
import net.minecraft.network.packet.SetEquippedItemPacket;
import net.minecraft.network.packet.ContainerSetDataPacket;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.SignTileEntity;
import net.minecraft.network.packet.SignUpdatePacket;
import net.minecraft.network.packet.ContainerSetContentPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.packet.ContainerAckPacket;
import net.minecraft.network.packet.ContainerSetSlotPacket;
import util.Mth;
import net.minecraft.world.level.tile.entity.DispenserTileEntity;
import net.minecraft.world.level.tile.entity.FurnaceTileEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.network.packet.ContainerOpenPacket;
import net.minecraft.world.level.Explosion;
import net.minecraft.network.packet.ExplodePacket;
import net.minecraft.network.packet.RespawnPacket;
import net.minecraft.network.packet.SetHealthPacket;
import net.minecraft.network.packet.EntityEventPacket;
import net.minecraft.network.packet.SetRidingPacket;
import net.minecraft.Pos;
import net.minecraft.network.packet.SetSpawnPositionPacket;
import net.minecraft.network.packet.SetTimePacket;

import java.io.IOException;
import java.util.List;

import net.minecraft.network.packet.AddMobPacket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import net.minecraft.network.packet.PreLoginPacket;
import net.minecraft.network.packet.EntityActionAtPositionPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.packet.AnimatePacket;
import net.minecraft.network.packet.ChatPacket;
import net.minecraft.client.particle.TakeAnimationParticle;
import net.minecraft.network.packet.TakeItemEntityPacket;
import net.minecraft.network.packet.DisconnectPacket;
import net.minecraft.network.packet.TileUpdatePacket;
import net.minecraft.network.packet.BlockRegionUpdatePacket;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.network.packet.ChunkTilesUpdatePacket;
import net.minecraft.network.packet.ChunkVisibilityPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.MovePlayerPacket;
import net.minecraft.network.packet.RemoveEntityPacket;
import net.minecraft.network.packet.MoveEntityPacket;
import net.minecraft.network.packet.TeleportEntityPacket;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.packet.AddPlayerPacket;
import net.minecraft.network.packet.SetEntityDataPacket;
import net.minecraft.network.packet.SetEntityMotionPacket;
import net.minecraft.network.packet.AddPaintingPacket;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.network.packet.AddGlobalEntityPacketPacket;
import net.minecraft.world.entity.item.FallingTile;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.item.Boat;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.network.packet.AddEntityPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.network.packet.AddItemEntityPacket;
import net.minecraft.stats.Stats;
import net.minecraft.network.packet.LoginPacket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.Random;
import net.minecraft.world.level.saveddata.SavedDataStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.packet.PacketListener;

public class ClientConnection extends PacketListener
{
    private boolean done = false;
    private Connection connection;
    public String message;
    private Minecraft minecraft;
    private MultiPlayerLevel level;
    private boolean started = false;
    public SavedDataStorage savedDataStorage = new SavedDataStorage(null);
    Random random = new Random();
    
    public ClientConnection(final Minecraft minecraft, final String ip, final int port) throws IOException {
        this.minecraft = minecraft;
        this.connection = new Connection(new Socket(InetAddress.getByName(ip), port), "Client", this);
    }
    
    public void tick() {
        if (!this.done) this.connection.tick();
        this.connection.flush();
    }
    
    @Override
    public void handleLogin(final LoginPacket packet) {
        this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
        this.minecraft.stats.award(Stats.joinMultiplayer, 1);
        this.level = new MultiPlayerLevel(this, packet.seed, packet.dimension);
        this.level.isClientSide = true;
        this.minecraft.setLevel(this.level);
        this.minecraft.player.dimension = packet.dimension;
        this.minecraft.setScreen(new ReceivingLevelScreen(this));
        this.minecraft.player.entityId = packet.clientVersion;
    }
    
    @Override
    public void handleAddItemEntity(final AddItemEntityPacket packet) {
        final ItemEntity e = new ItemEntity(this.level, packet.x / 32.0, packet.y / 32.0, packet.z / 32.0, new ItemInstance(packet.itemId, packet.itemCount, packet.auxValue));
        e.xd = packet.xa / 128.0;
        e.yd = packet.ya / 128.0;
        e.zd = packet.za / 128.0;
        e.xp = packet.x;
        e.yp = packet.y;
        e.zp = packet.z;
        this.level.putEntity(packet.id, e);
    }
    
    @Override
    public void handleAddEntity(final AddEntityPacket packet) {
        final double x = packet.x / 32.0;
        final double y = packet.y / 32.0;
        final double z = packet.z / 32.0;
        Entity e = null;

        if (packet.type == AddEntityPacket.MINECART_RIDEABLE) e = new Minecart(this.level, x, y, z, 0);
        if (packet.type == AddEntityPacket.MINECART_CHEST) e = new Minecart(this.level, x, y, z, 1);
        if (packet.type == AddEntityPacket.MINECART_FURNACE) e = new Minecart(this.level, x, y, z, 2);
        if (packet.type == AddEntityPacket.FISH_HOOK) e = new FishingHook(this.level, x, y, z);
        if (packet.type == AddEntityPacket.ARROW) e = new Arrow(this.level, x, y, z);
        if (packet.type == AddEntityPacket.SNOWBALL) e = new Snowball(this.level, x, y, z);
        if (packet.type == AddEntityPacket.FIREBALL) {
            e = new Fireball(this.level, x, y, z, packet.xa / 8000.0, packet.ya / 8000.0, packet.za / 8000.0);
            packet.data = 0;
        }
        if (packet.type == AddEntityPacket.EGG) e = new ThrownEgg(this.level, x, y, z);
        if (packet.type == AddEntityPacket.BOAT) e = new Boat(this.level, x, y, z);
        if (packet.type == AddEntityPacket.PRIMED_TNT) e = new PrimedTnt(this.level, x, y, z);
        if (packet.type == AddEntityPacket.FALLING_SAND) e = new FallingTile(this.level, x, y, z, Tile.sand.id);
        if (packet.type == AddEntityPacket.FALLING_GRAVEL) e = new FallingTile(this.level, x, y, z, Tile.gravel.id);

        if (e != null) {
            e.xp = packet.x;
            e.yp = packet.y;
            e.zp = packet.z;
            e.yRot = 0.0f;
            e.xRot = 0.0f;
            e.entityId = packet.id;
            this.level.putEntity(packet.id, e);

            if (packet.data > 0) {
                if (packet.type == AddEntityPacket.ARROW) {
                    final Entity owner = this.getEntity(packet.data);
                    if (owner instanceof Mob) {
                        ((Arrow)e).owner = (Mob)owner;
                    }
                }

                e.lerpMotion(packet.xa / 8000.0, packet.ya / 8000.0, packet.za / 8000.0);
            }
        }
    }
    
    @Override
    public void handleAddGlobalEntity(final AddGlobalEntityPacketPacket packet) {
        final double x = packet.x / 32.0;
        final double y = packet.y / 32.0;
        final double z = packet.z / 32.0;
        Entity e = null;
        if (packet.type == AddGlobalEntityPacketPacket.LIGHTNING) e = new LightningBolt(this.level, x, y, z);
        if (e != null) {
            e.xp = packet.x;
            e.yp = packet.y;
            e.zp = packet.z;
            e.yRot = 0.0f;
            e.xRot = 0.0f;
            e.entityId = packet.id;
            this.level.addGlobalEntity(e);
        }
    }
    
    @Override
    public void handleAddPainting(final AddPaintingPacket packet) {
        Painting painting = new Painting(this.level, packet.x, packet.y, packet.z, packet.dir, packet.motive);
        this.level.putEntity(packet.id, painting);
    }
    
    @Override
    public void handleSetEntityMotion(final SetEntityMotionPacket packet) {
        final Entity e = this.getEntity(packet.id);
        if (e == null) return;
        e.lerpMotion(packet.xa / 8000.0, packet.ya / 8000.0, packet.za / 8000.0);
    }
    
    @Override
    public void handleSetEntityData(final SetEntityDataPacket packet) {
        final Entity e = this.getEntity(packet.id);
        if (e != null && packet.getUnpackedData() != null) {
            e.getEntityData().assignValues(packet.getUnpackedData());
        }
    }
    
    @Override
    public void handleAddPlayer(final AddPlayerPacket packet) {
        final double x = packet.x / 32.0;
        final double y = packet.y / 32.0;
        final double z = packet.z / 32.0;
        final float yRot = packet.yRot * 360 / 256.0f;
        final float xRot = packet.xRot * 360 / 256.0f;
        final RemotePlayer player = new RemotePlayer(this.minecraft.level, packet.name);
        player.xo = player.xOld = player.xp = packet.x;
        player.yo = player.yOld = player.yp = packet.y;
        player.zo = player.zOld = player.zp = packet.z;

        final int carriedItem = packet.carriedItem;
        if (carriedItem == 0) {
            player.inventory.items[player.inventory.selected] = null;
        }
        else {
            player.inventory.items[player.inventory.selected] = new ItemInstance(carriedItem, 1, 0);
        }
        player.absMoveTo(x, y, z, yRot, xRot);

        this.level.putEntity(packet.id, player);
    }
    
    @Override
    public void handleTeleportEntity(final TeleportEntityPacket packet) {
        final Entity entity = this.getEntity(packet.id);
        if (entity == null) return;

        entity.xp = packet.x;
        entity.yp = packet.y;
        entity.zp = packet.z;
        double x = entity.xp / 32.0;
        double y = entity.yp / 32.0 + 1 / 64.0f;
        double z = entity.zp / 32.0;
        float yRot = packet.yRot * 360 / 256.0f;
        float xRot = packet.xRot * 360 / 256.0f;

        entity.lerpTo(x, y, z, yRot, xRot, 3);
    }
    
    @Override
    public void handleMoveEntity(final MoveEntityPacket packet) {
        final Entity e = this.getEntity(packet.id);
        if (e == null) return;

        e.xp += packet.xa;
        e.yp += packet.ya;
        e.zp += packet.za;
        double x = e.xp / 32.0;
        double y = e.yp / 32.0;
        double z = e.zp / 32.0;
        float yRot = packet.hasRot ? (packet.yRot * 360 / 256.0f) : e.yRot;
        float xRot = packet.hasRot ? (packet.xRot * 360 / 256.0f) : e.xRot;
        e.lerpTo(x, y, z, yRot, xRot, 3);
    }
    
    @Override
    public void handleRemoveEntity(final RemoveEntityPacket packet) {
        this.level.removeEntity(packet.id);
    }
    
    @Override
    public void handleMovePlayer(final MovePlayerPacket packet) {
        final LocalPlayer player = this.minecraft.player;

        double x = player.x;
        double y = player.y;
        double z = player.z;
        float yRot = player.yRot;
        float xRot = player.xRot;

        if (packet.hasPos) {
            x = packet.x;
            y = packet.y;
            z = packet.z;
        }
        if (packet.hasRot) {
            yRot = packet.yRot;
            xRot = packet.xRot;
        }

        player.ySlideOffset = 0.0f;
        player.xd = player.yd = player.zd = 0.0;
        player.absMoveTo(x, y, z, yRot, xRot);
        packet.x = player.x;
        packet.y = player.bb.y0;
        packet.z = player.z;
        packet.yView = player.y;
        this.connection.send(packet);
        if (!this.started) {
            this.minecraft.player.xo = this.minecraft.player.x;
            this.minecraft.player.yo = this.minecraft.player.y;
            this.minecraft.player.zo = this.minecraft.player.z;

            this.started = true;
            this.minecraft.setScreen(null);
        }
    }
    
    @Override
    public void handleChunkVisibility(final ChunkVisibilityPacket packet) {
        this.level.setChunkVisible(packet.x, packet.y, packet.visible);
    }
    
    @Override
    public void handleChunkTilesUpdate(final ChunkTilesUpdatePacket packet) {
        final LevelChunk lc = this.level.getChunk(packet.xc, packet.zc);
        final int xo = packet.xc * 16;
        final int zo = packet.zc * 16;
        for (int i = 0; i < packet.count; ++i) {
            final short pos = packet.positions[i];
            final int tile = packet.blocks[i] & 0xFF;
            final byte data = packet.data[i];

            final int x = pos >> 12 & 0xF;
            final int z = pos >> 8 & 0xF;
            final int y = pos & 0xFF;

            lc.setTileAndData(x, y, z, tile, data);
            this.level.clearResetRegion(x + xo, y, z + zo, x + xo, y, z + zo);
            this.level.setTilesDirty(x + xo, y, z + zo, x + xo, y, z + zo);
        }
    }
    
    @Override
    public void handleBlockRegionUpdate(final BlockRegionUpdatePacket packet) {
        this.level.clearResetRegion(packet.x, packet.y, packet.z, packet.x + packet.xs - 1, packet.y + packet.ys - 1, packet.z + packet.zs - 1);
        this.level.setBlocksAndData(packet.x, packet.y, packet.z, packet.xs, packet.ys, packet.zs, packet.buffer);
    }
    
    @Override
    public void handleTileUpdate(final TileUpdatePacket packet) {
        this.level.doSetTileAndData(packet.x, packet.y, packet.z, packet.block, packet.data);
    }
    
    @Override
    public void handleDisconnect(final DisconnectPacket packet) {
        this.connection.close("disconnect.kicked");
        this.done = true;

        this.minecraft.setLevel(null);
        this.minecraft.setScreen(new DisconnectedScreen("disconnect.disconnected", "disconnect.genericReason", packet.reason));
    }
    
    @Override
    public void onDisconnect(final String reason, final Object[] reasonObjects) {
        if (this.done) return;
        this.done = true;

        this.minecraft.setLevel(null);
        this.minecraft.setScreen(new DisconnectedScreen("disconnect.lost", reason, reasonObjects));
    }
    
    public void sendAndDisconnect(final Packet packet) {
        if (this.done) return;
        this.connection.send(packet);
        this.connection.sendAndQuit();
    }
    
    public void send(final Packet packet) {
        if (this.done) return;
        this.connection.send(packet);
    }
    
    @Override
    public void handleTakeItemEntity(final TakeItemEntityPacket packet) {
        final Entity from = this.getEntity(packet.itemId);
        Mob to = (Mob)this.getEntity(packet.playerId);
        if (to == null) to = this.minecraft.player;

        if (from != null) {
            this.level.playSound(from, "random.pop", 0.2f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            this.minecraft.particleEngine.add(new TakeAnimationParticle(this.minecraft.level, from, to, -0.5f));
            this.level.removeEntity(packet.itemId);
        }
    }
    
    @Override
    public void handleChat(final ChatPacket packet) {
        this.minecraft.gui.addMessage(packet.message);
    }
    
    @Override
    public void handleAnimate(final AnimatePacket packet) {
        final Entity e = this.getEntity(packet.id);
        if (e == null) return;

        if (packet.action == AnimatePacket.SWING) {
            Player player = (Player)e;
            player.swing();
        }
        else if (packet.action == AnimatePacket.HURT) {
            e.animateHurt();
        }
        else if (packet.action == AnimatePacket.WAKE_UP) {
            Player player = (Player)e;
            player.stopSleepInBed(false, false, false);
        }
        else if (packet.action == AnimatePacket.RESPAWN) {
            Player player = (Player)e;
            player.animateRespawn();
        }
    }
    
    @Override
    public void handleEntityActionAtPosition(final EntityActionAtPositionPacket packet) {
        final Entity e = this.getEntity(packet.id);
        if (e == null) return;

        if (packet.action == EntityActionAtPositionPacket.START_SLEEP) {
            Player player = (Player)e;
            player.startSleepInBed(packet.x, packet.y, packet.z);
        }
    }
    
    @Override
    public void handlePreLogin(final PreLoginPacket packet) {
        if (packet.userName.equals("-")) {
            this.send(new LoginPacket(this.minecraft.user.name, SharedConstants.NETWORK_PROTOCOL_VERSION));
        }
        else {
            try {
                URL url = new URL("http://www.minecraft.net/game/joinserver.jsp?user=" + this.minecraft.user.name + "&sessionId=" + this.minecraft.user.sessionId + "&serverId=" + packet.userName);
                final BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                final String msg = br.readLine();
                br.close();

                if ("ok".equalsIgnoreCase(msg)) {
                    this.send(new LoginPacket(this.minecraft.user.name, SharedConstants.NETWORK_PROTOCOL_VERSION));
                }
                else {
                    this.connection.close("disconnect.loginFailedInfo", msg);
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
                this.connection.close("disconnect.genericReason", "Internal client error: " + e);
            }
        }
    }
    
    public void close() {
        this.done = true;
        this.connection.flush();
        this.connection.close("disconnect.closed");
    }
    
    @Override
    public void handleAddMob(final AddMobPacket packet) {
        final double x = packet.x / 32.0;
        final double y = packet.y / 32.0;
        final double z = packet.z / 32.0;
        final float yRot = packet.yRot * 360 / 256.0f;
        final float xRot = packet.xRot * 360 / 256.0f;

        final Mob e = (Mob)EntityIO.newById(packet.type, this.minecraft.level);
        e.xp = packet.x;
        e.yp = packet.y;
        e.zp = packet.z;

        e.entityId = packet.id;

        e.absMoveTo(x, y, z, yRot, xRot);
        e.interpolateOnly = true;

        this.level.putEntity(packet.id, e);

        final List<SynchedEntityData.DataItem> unpackedData = packet.getUnpackedData();
        if (unpackedData != null) {
            e.getEntityData().assignValues(unpackedData);
        }
    }
    
    @Override
    public void handleSetTime(final SetTimePacket packet) {
        this.minecraft.level.setTime(packet.time);
    }
    
    @Override
    public void handleSetSpawn(final SetSpawnPositionPacket packet) {
        this.minecraft.player.setRespawnPosition(new Pos(packet.x, packet.y, packet.z));
        this.minecraft.level.getLevelData().setSpawn(packet.x, packet.y, packet.z);
    }
    
    @Override
    public void handleRidePacket(final SetRidingPacket packet) {
        Entity rider = this.getEntity(packet.riderId);
        Entity ridden = this.getEntity(packet.riddenId);

        if (packet.riderId == this.minecraft.player.entityId) rider = this.minecraft.player;
        if (rider == null) return;

        rider.ride(ridden);
    }
    
    @Override
    public void handleEntityEvent(final EntityEventPacket packet) {
        final Entity e = this.getEntity(packet.entityId);
        if (e != null) e.handleEntityEvent(packet.eventId);
    }
    
    private Entity getEntity(final int entityId) {
        if (entityId == this.minecraft.player.entityId) {
            return this.minecraft.player;
        }
        return this.level.getEntity(entityId);
    }
    
    @Override
    public void handleSetHealth(final SetHealthPacket packet) {
        this.minecraft.player.hurtTo(packet.health);
    }
    
    @Override
    public void handleRespawn(final RespawnPacket packet) {
        if (packet.dimension != this.minecraft.player.dimension) {
            this.started = false;

            this.level = new MultiPlayerLevel(this, this.level.getLevelData().getSeed(), packet.dimension);
            this.level.isClientSide = true;

            this.minecraft.setLevel(this.level);
            this.minecraft.player.dimension = packet.dimension;
            this.minecraft.setScreen(new ReceivingLevelScreen(this));
        }
        this.minecraft.respawnPlayer(true, packet.dimension);
    }
    
    @Override
    public void handleExplosion(final ExplodePacket packet) {
        final Explosion e = new Explosion(this.minecraft.level, null, packet.x, packet.y, packet.z, packet.r);
        e.toBlow = packet.toBlow;
        e.finalizeExplosion(true);
    }
    
    @Override
    public void handleContainerOpen(final ContainerOpenPacket packet) {
        if (packet.type == ContainerOpenPacket.CONTAINER) {
            this.minecraft.player.openContainer(new SimpleContainer(packet.title, packet.size));
            this.minecraft.player.containerMenu.containerId = packet.containerId;
        }
        else if (packet.type == ContainerOpenPacket.FURNACE) {
            this.minecraft.player.openFurnace(new FurnaceTileEntity());
            this.minecraft.player.containerMenu.containerId = packet.containerId;
        }
        else if (packet.type == ContainerOpenPacket.TRAP) {
            this.minecraft.player.openTrap(new DispenserTileEntity());
            this.minecraft.player.containerMenu.containerId = packet.containerId;
        }
        else if (packet.type == ContainerOpenPacket.WORKBENCH) {
            final LocalPlayer player = this.minecraft.player;
            this.minecraft.player.startCrafting(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
            this.minecraft.player.containerMenu.containerId = packet.containerId;
        }
    }
    
    @Override
    public void handleContainerSetSlot(final ContainerSetSlotPacket packet) {
        if (packet.containerId == AbstractContainerMenu.CONTAINER_ID_CARRIED) {
            this.minecraft.player.inventory.setCarried(packet.item);
        }
        else if (packet.containerId == AbstractContainerMenu.CONTAINER_ID_INVENTORY && packet.slot >= 36 && packet.slot < 36 + 9) {
            final ItemInstance lastItem = this.minecraft.player.inventoryMenu.getSlot(packet.slot).getItem();
            if (packet.item != null) {
                if (lastItem == null || lastItem.count < packet.item.count) {
                    packet.item.popTime = Inventory.POP_TIME_DURATION;
                }
            }
            this.minecraft.player.inventoryMenu.setItem(packet.slot, packet.item);
        }
        else if (packet.containerId == this.minecraft.player.containerMenu.containerId) {
            this.minecraft.player.containerMenu.setItem(packet.slot, packet.item);
        }
    }
    
    @Override
    public void handleContainerAck(final ContainerAckPacket packet) {
        AbstractContainerMenu menu = null;
        if (packet.containerId == AbstractContainerMenu.CONTAINER_ID_INVENTORY) {
            menu = this.minecraft.player.inventoryMenu;
        }
        else if (packet.containerId == this.minecraft.player.containerMenu.containerId) {
            menu = this.minecraft.player.containerMenu;
        }
        if (menu != null) {
            if (!packet.accepted) {
                menu.rollbackToBackup(packet.uid);
                this.send(new ContainerAckPacket(packet.containerId, packet.uid, true));
            } else {
                menu.deleteBackup(packet.uid);
            }
        }
    }
    
    @Override
    public void handleContainerContent(final ContainerSetContentPacket packet) {
        if (packet.containerId == AbstractContainerMenu.CONTAINER_ID_INVENTORY) {
            this.minecraft.player.inventoryMenu.setAll(packet.items);
        }
        else if (packet.containerId == this.minecraft.player.containerMenu.containerId) {
            this.minecraft.player.containerMenu.setAll(packet.items);
        }
    }
    
    @Override
    public void handleSignUpdate(final SignUpdatePacket packet) {
        if (this.minecraft.level.hasChunkAt(packet.x, packet.y, packet.z)) {
            final TileEntity te = this.minecraft.level.getTileEntity(packet.x, packet.y, packet.z);
            if (te instanceof SignTileEntity) {
                final SignTileEntity ste = (SignTileEntity)te;
                for (int i = 0; i < SignTileEntity.MAX_SIGN_LINES; ++i) {
                    ste.messages[i] = packet.lines[i];
                }

                ste.setChanged();
            }
        }
    }
    
    @Override
    public void handleContainerSetData(final ContainerSetDataPacket packet) {
        this.onUnhandledPacket(packet);
        if (this.minecraft.player.containerMenu != null && this.minecraft.player.containerMenu.containerId == packet.containerId) {
            this.minecraft.player.containerMenu.setData(packet.id, packet.value);
        }
    }
    
    @Override
    public void handleSetEquippedItem(final SetEquippedItemPacket packet) {
        final Entity e = this.getEntity(packet.entity);
        if (e != null) {
            e.setEquippedSlot(packet.slot, packet.item, packet.auxValue);
        }
    }
    
    @Override
    public void handleContainerClose(final ContainerClosePacket packet) {
        this.minecraft.player.closeContainer();
    }
    
    @Override
    public void handleTileEvent(final TileEventPacket packet) {
        this.minecraft.level.tileEvent(packet.x, packet.y, packet.z, packet.b0, packet.b1);
    }
    
    @Override
    public void handleGameEvent(final GameEventPacket packet) {
        final int event = packet.event;
        if (event >= 0 && event < GameEventPacket.EVENT_LANGUAGE_ID.length) {
            if (GameEventPacket.EVENT_LANGUAGE_ID[event] != null) {
                this.minecraft.player.displayClientMessage(GameEventPacket.EVENT_LANGUAGE_ID[event]);
            }
        }
        if (event == GameEventPacket.START_RAINING) {
            this.level.getLevelData().setRaining(true);
            this.level.setRainLevel(1.0f);
        }
        else if (event == GameEventPacket.STOP_RAINING) {
            this.level.getLevelData().setRaining(false);
            this.level.setRainLevel(0.0f);
        }
    }
    
    @Override
    public void handleComplexItemData(final ComplexItemDataPacket packet) {
        if (packet.itemType == Item.map.id) {
            MapItem.getSavedData(packet.itemId, this.minecraft.level).handleComplexItemData(packet.data);
        }
        else {
            System.out.println("Unknown itemid: " + packet.itemId);
        }
    }
    
    @Override
    public void handleLevelEvent(final LevelEventPacket packet) {
        this.minecraft.level.levelEvent(packet.type, packet.x, packet.y, packet.z, packet.data);
    }
    
    @Override
    public void handleAwardStat(final AwardStatPacket packet) {
        ((MultiplayerLocalPlayer)this.minecraft.player).awardStatFromServer(Stats.getStat(packet.statId), packet.count);
    }
    
    @Override
    public boolean isServerPacketListener() {
        return false;
    }
}
