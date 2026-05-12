// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.multiplayer;

import net.minecraft.network.packet.AwardStatPacket;
import net.minecraft.network.packet.LevelEventPacket;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.Item;
import net.minecraft.network.packet.ComplexItemDataPacket;
import net.minecraft.network.packet.BedResponsePacket;
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
import net.minecraft.world.Container;
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
import java.net.UnknownHostException;
import java.util.List;
import net.minecraft.world.entity.EntityIO;
import net.minecraft.network.packet.AddMobPacket;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import net.minecraft.network.packet.PreLoginPacket;
import net.minecraft.network.packet.EntityActionAtPositionPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.packet.AnimatePacket;
import net.minecraft.network.packet.ChatPacket;
import net.minecraft.client.particle.Particle;
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
import net.minecraft.world.entity.Painting;
import net.minecraft.network.packet.AddPaintingPacket;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.network.packet.AddGlobalEntityPacketPacket;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.network.packet.AddItemEntityPacket;
import net.minecraft.client.gui.Screen;
import net.minecraft.world.level.Level;
import net.minecraft.stats.Stats;
import net.minecraft.network.packet.LoginPacket;
import java.net.Socket;
import java.net.InetAddress;
import net.minecraft.world.level.storage.LevelStorage;
import java.util.Random;
import net.minecraft.world.level.saveddata.SavedDataStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.packet.PacketListener;

public class ClientConnection extends PacketListener
{
    private boolean done;
    private Connection connection;
    public String message;
    private Minecraft minecraft;
    private MultiPlayerLevel level;
    private boolean started;
    public SavedDataStorage savedDataStorage;
    Random random;
    
    public ClientConnection(final Minecraft minecraft, final String ip, final int port) throws IOException {
        this.done = false;
        this.started = false;
        this.savedDataStorage = new SavedDataStorage(null);
        this.random = new Random();
        this.minecraft = minecraft;
        this.connection = new Connection(new Socket(InetAddress.getByName(ip), port), "Client", this);
    }
    
    public void tick() {
        if (!this.done) {
            this.connection.tick();
        }
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
        final double n = packet.x / 32.0;
        final double n2 = packet.y / 32.0;
        final double n3 = packet.z / 32.0;
        Entity e = null;
        if (packet.type == 10) {
            e = new Minecart(this.level, n, n2, n3, 0);
        }
        if (packet.type == 11) {
            e = new Minecart(this.level, n, n2, n3, 1);
        }
        if (packet.type == 12) {
            e = new Minecart(this.level, n, n2, n3, 2);
        }
        if (packet.type == 90) {
            e = new FishingHook(this.level, n, n2, n3);
        }
        if (packet.type == 60) {
            e = new Arrow(this.level, n, n2, n3);
        }
        if (packet.type == 61) {
            e = new Snowball(this.level, n, n2, n3);
        }
        if (packet.type == 63) {
            e = new Fireball(this.level, n, n2, n3, packet.xa / 8000.0, packet.ya / 8000.0, packet.za / 8000.0);
            packet.data = 0;
        }
        if (packet.type == 62) {
            e = new ThrownEgg(this.level, n, n2, n3);
        }
        if (packet.type == 1) {
            e = new Boat(this.level, n, n2, n3);
        }
        if (packet.type == 50) {
            e = new PrimedTnt(this.level, n, n2, n3);
        }
        if (packet.type == 70) {
            e = new FallingTile(this.level, n, n2, n3, Tile.sand.id);
        }
        if (packet.type == 71) {
            e = new FallingTile(this.level, n, n2, n3, Tile.gravel.id);
        }
        if (e != null) {
            e.xp = packet.x;
            e.yp = packet.y;
            e.zp = packet.z;
            e.yRot = 0.0f;
            e.xRot = 0.0f;
            e.entityId = packet.id;
            this.level.putEntity(packet.id, e);
            if (packet.data > 0) {
                if (packet.type == 60) {
                    final Entity entity = this.getEntity(packet.data);
                    if (entity instanceof Mob) {
                        ((Arrow)e).owner = (Mob)entity;
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
        if (packet.type == 1) {
            e = new LightningBolt(this.level, x, y, z);
        }
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
        this.level.putEntity(packet.id, new Painting(this.level, packet.x, packet.y, packet.z, packet.dir, packet.motive));
    }
    
    @Override
    public void handleSetEntityMotion(final SetEntityMotionPacket packet) {
        final Entity entity = this.getEntity(packet.id);
        if (entity == null) {
            return;
        }
        entity.lerpMotion(packet.xa / 8000.0, packet.ya / 8000.0, packet.za / 8000.0);
    }
    
    @Override
    public void handleSetEntityData(final SetEntityDataPacket packet) {
        final Entity entity = this.getEntity(packet.id);
        if (entity != null && packet.getUnpackedData() != null) {
            entity.getEntityData().assignValues(packet.getUnpackedData());
        }
    }
    
    @Override
    public void handleAddPlayer(final AddPlayerPacket packet) {
        final double x = packet.x / 32.0;
        final double y = packet.y / 32.0;
        final double z = packet.z / 32.0;
        final float yRot = packet.yRot * 360 / 256.0f;
        final float xRot = packet.xRot * 360 / 256.0f;
        final RemotePlayer remotePlayer3;
        final RemotePlayer remotePlayer2;
        final RemotePlayer remotePlayer;
        final RemotePlayer e = remotePlayer = (remotePlayer2 = (remotePlayer3 = new RemotePlayer(this.minecraft.level, packet.name)));
        final int x2 = packet.x;
        remotePlayer.xp = x2;
        final double n = x2;
        remotePlayer2.xOld = n;
        remotePlayer3.xo = n;
        final RemotePlayer remotePlayer4 = e;
        final RemotePlayer remotePlayer5 = e;
        final RemotePlayer remotePlayer6 = e;
        final int y2 = packet.y;
        remotePlayer6.yp = y2;
        final double n2 = y2;
        remotePlayer5.yOld = n2;
        remotePlayer4.yo = n2;
        final RemotePlayer remotePlayer7 = e;
        final RemotePlayer remotePlayer8 = e;
        final RemotePlayer remotePlayer9 = e;
        final int z2 = packet.z;
        remotePlayer9.zp = z2;
        final double n3 = z2;
        remotePlayer8.zOld = n3;
        remotePlayer7.zo = n3;
        final int carriedItem = packet.carriedItem;
        if (carriedItem == 0) {
            e.inventory.items[e.inventory.selected] = null;
        }
        else {
            e.inventory.items[e.inventory.selected] = new ItemInstance(carriedItem, 1, 0);
        }
        e.absMoveTo(x, y, z, yRot, xRot);
        this.level.putEntity(packet.id, e);
    }
    
    @Override
    public void handleTeleportEntity(final TeleportEntityPacket packet) {
        final Entity entity = this.getEntity(packet.id);
        if (entity == null) {
            return;
        }
        entity.xp = packet.x;
        entity.yp = packet.y;
        entity.zp = packet.z;
        entity.lerpTo(entity.xp / 32.0, entity.yp / 32.0 + 0.015625, entity.zp / 32.0, packet.yRot * 360 / 256.0f, packet.xRot * 360 / 256.0f, 3);
    }
    
    @Override
    public void handleMoveEntity(final MoveEntityPacket packet) {
        final Entity entity = this.getEntity(packet.id);
        if (entity == null) {
            return;
        }
        final Entity entity2 = entity;
        entity2.xp += packet.xa;
        final Entity entity3 = entity;
        entity3.yp += packet.ya;
        final Entity entity4 = entity;
        entity4.zp += packet.za;
        entity.lerpTo(entity.xp / 32.0, entity.yp / 32.0, entity.zp / 32.0, packet.hasRot ? (packet.yRot * 360 / 256.0f) : entity.yRot, packet.hasRot ? (packet.xRot * 360 / 256.0f) : entity.xRot, 3);
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
        final LocalPlayer localPlayer = player;
        final LocalPlayer localPlayer2 = player;
        final LocalPlayer localPlayer3 = player;
        final double xd = 0.0;
        localPlayer3.zd = xd;
        localPlayer2.yd = xd;
        localPlayer.xd = xd;
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
        final LevelChunk chunk = this.level.getChunk(packet.xc, packet.zc);
        final int n = packet.xc * 16;
        final int n2 = packet.zc * 16;
        for (int i = 0; i < packet.count; ++i) {
            final short n3 = packet.positions[i];
            final int tile = packet.blocks[i] & 0xFF;
            final byte data = packet.data[i];
            final int x = n3 >> 12 & 0xF;
            final int z = n3 >> 8 & 0xF;
            final int n4 = n3 & 0xFF;
            chunk.setTileAndData(x, n4, z, tile, data);
            this.level.clearResetRegion(x + n, n4, z + n2, x + n, n4, z + n2);
            this.level.setTilesDirty(x + n, n4, z + n2, x + n, n4, z + n2);
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
        this.connection.close("disconnect.kicked", new Object[0]);
        this.done = true;
        this.minecraft.setLevel(null);
        this.minecraft.setScreen(new DisconnectedScreen("disconnect.disconnected", "disconnect.genericReason", new Object[] { packet.reason }));
    }
    
    @Override
    public void onDisconnect(final String reason, final Object[] reasonObjects) {
        if (this.done) {
            return;
        }
        this.done = true;
        this.minecraft.setLevel(null);
        this.minecraft.setScreen(new DisconnectedScreen("disconnect.lost", reason, reasonObjects));
    }
    
    public void sendAndDisconnect(final Packet ki) {
        if (this.done) {
            return;
        }
        this.connection.send(ki);
        this.connection.sendAndQuit();
    }
    
    public void send(final Packet ki) {
        if (this.done) {
            return;
        }
        this.connection.send(ki);
    }
    
    @Override
    public void handleTakeItemEntity(final TakeItemEntityPacket packet) {
        final Entity entity = this.getEntity(packet.itemId);
        Mob player = (Mob)this.getEntity(packet.playerId);
        if (player == null) {
            player = this.minecraft.player;
        }
        if (entity != null) {
            this.level.playSound(entity, "random.pop", 0.2f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            this.minecraft.particleEngine.add(new TakeAnimationParticle(this.minecraft.level, entity, player, -0.5f));
            this.level.removeEntity(packet.itemId);
        }
    }
    
    @Override
    public void handleChat(final ChatPacket packet) {
        this.minecraft.gui.addMessage(packet.message);
    }
    
    @Override
    public void handleAnimate(final AnimatePacket packet) {
        final Entity entity = this.getEntity(packet.id);
        if (entity == null) {
            return;
        }
        if (packet.action == 1) {
            ((Player)entity).swing();
        }
        else if (packet.action == 2) {
            entity.animateHurt();
        }
        else if (packet.action == 3) {
            ((Player)entity).stopSleepInBed(false, false, false);
        }
        else if (packet.action == 4) {
            ((Player)entity).animateRespawn();
        }
    }
    
    @Override
    public void handleEntityActionAtPosition(final EntityActionAtPositionPacket packet) {
        final Entity entity = this.getEntity(packet.id);
        if (entity == null) {
            return;
        }
        if (packet.action == 0) {
            ((Player)entity).startSleepInBed(packet.x, packet.y, packet.z);
        }
    }
    
    @Override
    public void handlePreLogin(final PreLoginPacket packet) {
        if (packet.userName.equals("-")) {
            this.send(new LoginPacket(this.minecraft.user.name, 14));
        }
        else {
            try {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL("http://www.minecraft.net/game/joinserver.jsp?user=" + this.minecraft.user.name + "&sessionId=" + this.minecraft.user.sessionId + "&serverId=" + packet.userName).openStream()));
                final String line = bufferedReader.readLine();
                bufferedReader.close();
                if (line.equalsIgnoreCase("ok")) {
                    this.send(new LoginPacket(this.minecraft.user.name, 14));
                }
                else {
                    this.connection.close("disconnect.loginFailedInfo", line);
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                this.connection.close("disconnect.genericReason", "Internal client error: " + ex.toString());
            }
        }
    }
    
    public void close() {
        this.done = true;
        this.connection.flush();
        this.connection.close("disconnect.closed", new Object[0]);
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
        final List unpackedData = packet.getUnpackedData();
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
        Entity entity = this.getEntity(packet.riderId);
        final Entity entity2 = this.getEntity(packet.riddenId);
        if (packet.riderId == this.minecraft.player.entityId) {
            entity = this.minecraft.player;
        }
        if (entity == null) {
            return;
        }
        entity.ride(entity2);
    }
    
    @Override
    public void handleEntityEvent(final EntityEventPacket packet) {
        final Entity entity = this.getEntity(packet.entityId);
        if (entity != null) {
            entity.handleEntityEvent(packet.eventId);
        }
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
        final Explosion explosion = new Explosion(this.minecraft.level, null, packet.x, packet.y, packet.z, packet.r);
        explosion.toBlow = packet.toBlow;
        explosion.addParticles(true);
    }
    
    @Override
    public void handleContainerOpen(final ContainerOpenPacket packet) {
        if (packet.type == 0) {
            this.minecraft.player.openContainer(new SimpleContainer(packet.title, packet.size));
            this.minecraft.player.containerMenu.containerId = packet.containerId;
        }
        else if (packet.type == 2) {
            this.minecraft.player.openFurnace(new FurnaceTileEntity());
            this.minecraft.player.containerMenu.containerId = packet.containerId;
        }
        else if (packet.type == 3) {
            this.minecraft.player.openTrap(new DispenserTileEntity());
            this.minecraft.player.containerMenu.containerId = packet.containerId;
        }
        else if (packet.type == 1) {
            final LocalPlayer player = this.minecraft.player;
            this.minecraft.player.startCrafting(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
            this.minecraft.player.containerMenu.containerId = packet.containerId;
        }
    }
    
    @Override
    public void handleContainerSetSlot(final ContainerSetSlotPacket packet) {
        if (packet.containerId == -1) {
            this.minecraft.player.inventory.setCarried(packet.item);
        }
        else if (packet.containerId == 0 && packet.slot >= 36 && packet.slot < 45) {
            final ItemInstance item = this.minecraft.player.inventoryMenu.getSlot(packet.slot).getItem();
            if (packet.item != null && (item == null || item.count < packet.item.count)) {
                packet.item.popTime = 5;
            }
            this.minecraft.player.inventoryMenu.setItem(packet.slot, packet.item);
        }
        else if (packet.containerId == this.minecraft.player.containerMenu.containerId) {
            this.minecraft.player.containerMenu.setItem(packet.slot, packet.item);
        }
    }
    
    @Override
    public void handleContainerAck(final ContainerAckPacket packet) {
        AbstractContainerMenu abstractContainerMenu = null;
        if (packet.containerId == 0) {
            abstractContainerMenu = this.minecraft.player.inventoryMenu;
        }
        else if (packet.containerId == this.minecraft.player.containerMenu.containerId) {
            abstractContainerMenu = this.minecraft.player.containerMenu;
        }
        if (abstractContainerMenu != null) {
            if (packet.accepted) {
                abstractContainerMenu.deleteBackup(packet.uid);
            }
            else {
                abstractContainerMenu.rollbackToBackup(packet.uid);
                this.send(new ContainerAckPacket(packet.containerId, packet.uid, true));
            }
        }
    }
    
    @Override
    public void handleContainerContent(final ContainerSetContentPacket packet) {
        if (packet.containerId == 0) {
            this.minecraft.player.inventoryMenu.setAll(packet.items);
        }
        else if (packet.containerId == this.minecraft.player.containerMenu.containerId) {
            this.minecraft.player.containerMenu.setAll(packet.items);
        }
    }
    
    @Override
    public void handleSignUpdate(final SignUpdatePacket packet) {
        if (this.minecraft.level.hasChunkAt(packet.x, packet.y, packet.z)) {
            final TileEntity tileEntity = this.minecraft.level.getTileEntity(packet.x, packet.y, packet.z);
            if (tileEntity instanceof SignTileEntity) {
                final SignTileEntity signTileEntity = (SignTileEntity)tileEntity;
                for (int i = 0; i < 4; ++i) {
                    signTileEntity.messages[i] = packet.lines[i];
                }
                signTileEntity.setChanged();
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
        final Entity entity = this.getEntity(packet.entity);
        if (entity != null) {
            entity.setEquippedSlot(packet.slot, packet.item, packet.auxValue);
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
    public void handleBedResponse(final BedResponsePacket packet) {
        final int type = packet.type;
        if (type >= 0 && type < BedResponsePacket.BED_RESPONSES.length && BedResponsePacket.BED_RESPONSES[type] != null) {
            this.minecraft.player.displayClientMessage(BedResponsePacket.BED_RESPONSES[type]);
        }
        if (type == 1) {
            this.level.getLevelData().setRaining(true);
            this.level.setRainLevel(1.0f);
        }
        else if (type == 2) {
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
