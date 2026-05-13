// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.network;

import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.SignTileEntity;
import net.minecraft.network.packet.SignUpdatePacket;

import java.util.ArrayList;
import net.minecraft.network.packet.ContainerAckPacket;
import net.minecraft.network.packet.ContainerClickPacket;
import net.minecraft.network.packet.ContainerClosePacket;
import net.minecraft.network.packet.RespawnPacket;
import net.minecraft.network.packet.InteractPacket;
import net.minecraft.network.packet.PlayerCommandPacket;
import net.minecraft.network.packet.AnimatePacket;
import net.minecraft.SharedConstants;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.packet.SetCarriedItemPacket;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.packet.ContainerSetSlotPacket;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.network.packet.UseItemPacket;
import net.minecraft.Pos;
import net.minecraft.network.packet.TileUpdatePacket;
import util.Mth;
import net.minecraft.network.packet.PlayerActionPacket;
import net.minecraft.world.phys.AABB;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.packet.MovePlayerPacket;
import net.minecraft.network.packet.PlayerInputPacket;
import net.minecraft.network.packet.ChatPacket;
import net.minecraft.network.packet.DisconnectPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.KeepAlivePacket;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.Connection;
import java.util.logging.Logger;
import net.minecraft.server.ConsoleInputSource;
import net.minecraft.network.packet.PacketListener;

public class PlayerConnection extends PacketListener implements ConsoleInputSource
{
    public static Logger logger;
    public Connection connection;
    public boolean done;
    private MinecraftServer server;
    private ServerPlayer player;
    private int tickCount;
    private int lastKeepAliveTick;
    private int aboveGroundTickCount;
    private boolean didTick;
    private double xLastOk;
    private double yLastOk;
    private double zLastOk;
    private boolean synched;
    private Map<Integer, Short> expectedAcks;
    
    public PlayerConnection(final MinecraftServer server, final Connection connection, final ServerPlayer player) {
        this.done = false;
        this.synched = true;
        this.expectedAcks = new HashMap<>();
        this.server = server;
        (this.connection = connection).setListener(this);
        this.player = player;
        player.connection = this;
    }
    
    public void tick() {
        this.didTick = false;
        this.connection.tick();
        if (this.tickCount - this.lastKeepAliveTick > 20) {
            this.send(new KeepAlivePacket());
        }
    }
    
    public void disconnect(final String reason) {
        this.player.disconnect();
        this.send(new DisconnectPacket(reason));
        this.connection.sendAndQuit();
        this.server.players.broadcastAll(new ChatPacket("§e" + this.player.name + " left the game."));
        this.server.players.remove(this.player);
        this.done = true;
    }
    
    @Override
    public void handlePlayerInput(final PlayerInputPacket packet) {
        this.player.setPlayerInput(packet.getXa(), packet.getYa(), packet.isJumping(), packet.isSneaking(), packet.getXRot(), packet.getYRot());
    }
    
    @Override
    public void handleMovePlayer(final MovePlayerPacket packet) {
        final ServerLevel level = this.server.getLevel(this.player.dimension);
        this.didTick = true;
        if (!this.synched) {
            final double n = packet.y - this.yLastOk;
            if (packet.x == this.xLastOk && n * n < 0.01 && packet.z == this.zLastOk) {
                this.synched = true;
            }
        }
        if (this.synched) {
            if (this.player.riding != null) {
                float yRot = this.player.yRot;
                float xRot = this.player.xRot;
                this.player.riding.positionRider();
                final double x = this.player.x;
                final double y = this.player.y;
                final double z = this.player.z;
                double x2 = 0.0;
                double z2 = 0.0;
                if (packet.hasRot) {
                    yRot = packet.yRot;
                    xRot = packet.xRot;
                }
                if (packet.hasPos && packet.y == -999.0 && packet.yView == -999.0) {
                    x2 = packet.x;
                    z2 = packet.z;
                }
                this.player.onGround = packet.onGround;
                this.player.doTick(true);
                this.player.move(x2, 0.0, z2);
                this.player.absMoveTo(x, y, z, yRot, xRot);
                this.player.xd = x2;
                this.player.zd = z2;
                if (this.player.riding != null) {
                    level.forceTick(this.player.riding, true);
                }
                if (this.player.riding != null) {
                    this.player.riding.positionRider();
                }
                this.server.players.move(this.player);
                this.xLastOk = this.player.x;
                this.yLastOk = this.player.y;
                this.zLastOk = this.player.z;
                level.tick(this.player);
                return;
            }
            if (this.player.isSleeping()) {
                this.player.doTick(true);
                this.player.absMoveTo(this.xLastOk, this.yLastOk, this.zLastOk, this.player.yRot, this.player.xRot);
                level.tick(this.player);
                return;
            }
            final double y2 = this.player.y;
            this.xLastOk = this.player.x;
            this.yLastOk = this.player.y;
            this.zLastOk = this.player.z;
            double n2 = this.player.x;
            double n3 = this.player.y;
            double n4 = this.player.z;
            float yRot2 = this.player.yRot;
            float xRot2 = this.player.xRot;
            if (packet.hasPos && packet.y == -999.0 && packet.yView == -999.0) {
                packet.hasPos = false;
            }
            if (packet.hasPos) {
                n2 = packet.x;
                n3 = packet.y;
                n4 = packet.z;
                final double d = packet.yView - packet.y;
                if (!this.player.isSleeping() && (d > 1.65 || d < 0.1)) {
                    this.disconnect("Illegal stance");
                    PlayerConnection.logger.warning(this.player.name + " had an illegal stance: " + d);
                    return;
                }
                if (Math.abs(packet.x) > 3.2E7 || Math.abs(packet.z) > 3.2E7) {
                    this.disconnect("Illegal position");
                    return;
                }
            }
            if (packet.hasRot) {
                yRot2 = packet.yRot;
                xRot2 = packet.xRot;
            }
            this.player.doTick(true);
            this.player.ySlideOffset = 0.0f;
            this.player.absMoveTo(this.xLastOk, this.yLastOk, this.zLastOk, yRot2, xRot2);
            if (!this.synched) {
                return;
            }
            final double xa = n2 - this.player.x;
            final double ya = n3 - this.player.y;
            final double za = n4 - this.player.z;
            if (xa * xa + ya * ya + za * za > 100.0) {
                PlayerConnection.logger.warning(this.player.name + " moved too quickly!");
                this.disconnect("You moved too quickly :( (Hacking?)");
                return;
            }
            final float n5 = 0.0625f;
            final boolean b = level.getCubes(this.player, this.player.bb.copy().shrink(n5, n5, n5)).size() == 0;
            this.player.move(xa, ya, za);
            final double n6 = n2 - this.player.x;
            double n7 = n3 - this.player.y;
            if (n7 > -0.5 || n7 < 0.5) {
                n7 = 0.0;
            }
            final double n8 = n4 - this.player.z;
            final double n9 = n6 * n6 + n7 * n7 + n8 * n8;
            boolean b2 = false;
            if (n9 > 0.0625 && !this.player.isSleeping()) {
                b2 = true;
                PlayerConnection.logger.warning(this.player.name + " moved wrongly!");
                System.out.println("Got position " + n2 + ", " + n3 + ", " + n4);
                System.out.println("Expected " + this.player.x + ", " + this.player.y + ", " + this.player.z);
            }
            this.player.absMoveTo(n2, n3, n4, yRot2, xRot2);
            final boolean b3 = level.getCubes(this.player, this.player.bb.copy().shrink(n5, n5, n5)).size() == 0;
            if (b && (b2 || !b3) && !this.player.isSleeping()) {
                this.teleport(this.xLastOk, this.yLastOk, this.zLastOk, yRot2, xRot2);
                return;
            }
            final AABB expand = this.player.bb.copy().grow(n5, n5, n5).expand(0.0, -0.55, 0.0);
            if (!this.server.isFlightAllowed && !level.containsAnyBlocks(expand)) {
                if (n7 >= -0.03125) {
                    ++this.aboveGroundTickCount;
                    if (this.aboveGroundTickCount > 80) {
                        PlayerConnection.logger.warning(this.player.name + " was kicked for floating too long!");
                        this.disconnect("Flying is not enabled on this server");
                        return;
                    }
                }
            }
            else {
                this.aboveGroundTickCount = 0;
            }
            this.player.onGround = packet.onGround;
            this.server.players.move(this.player);
            this.player.doCheckFallDamage(this.player.y - y2, packet.onGround);
        }
    }
    
    public void teleport(final double x, final double y, final double z, final float yRot, final float xRot) {
        this.synched = false;
        this.xLastOk = x;
        this.yLastOk = y;
        this.zLastOk = z;
        this.player.absMoveTo(x, y, z, yRot, xRot);
        this.player.connection.send(new MovePlayerPacket.PosRot(x, y + 1.6200000047683716, y, z, yRot, xRot, false));
    }
    
    @Override
    public void handlePlayerAction(final PlayerActionPacket packet) {
        final ServerLevel level = this.server.getLevel(this.player.dimension);
        if (packet.action == 4) {
            this.player.drop();
            return;
        }
        final ServerLevel serverLevel = level;
        final boolean canEditSpawn = level.dimension.id != 0 || this.server.players.isOp(this.player.name);
        serverLevel.canEditSpawn = canEditSpawn;
        final boolean b = canEditSpawn;
        boolean b2 = false;
        if (packet.action == 0) {
            b2 = true;
        }
        if (packet.action == 2) {
            b2 = true;
        }
        final int x = packet.x;
        final int y = packet.y;
        final int z = packet.z;
        if (b2) {
            final double n = this.player.x - (x + 0.5);
            final double n2 = this.player.y - (y + 0.5);
            final double n3 = this.player.z - (z + 0.5);
            if (n * n + n2 * n2 + n3 * n3 > 36.0) {
                return;
            }
        }
        final Pos sharedSpawnPos = level.getSharedSpawnPos();
        final int n4 = (int)Mth.abs((float)(x - sharedSpawnPos.x));
        int n5 = (int)Mth.abs((float)(z - sharedSpawnPos.z));
        if (n4 > n5) {
            n5 = n4;
        }
        if (packet.action == 0) {
            if (n5 > 16 || b) {
                this.player.gameMode.startDestroyBlock(x, y, z, packet.face);
            }
            else {
                this.player.connection.send(new TileUpdatePacket(x, y, z, level));
            }
        }
        else if (packet.action == 2) {
            this.player.gameMode.stopDestroyBlock(x, y, z);
            if (level.getTile(x, y, z) != 0) {
                this.player.connection.send(new TileUpdatePacket(x, y, z, level));
            }
        }
        else if (packet.action == 3) {
            final double n6 = this.player.x - (x + 0.5);
            final double n7 = this.player.y - (y + 0.5);
            final double n8 = this.player.z - (z + 0.5);
            if (n6 * n6 + n7 * n7 + n8 * n8 < 256.0) {
                this.player.connection.send(new TileUpdatePacket(x, y, z, level));
            }
        }
        level.canEditSpawn = false;
    }
    
    @Override
    public void handleUseItem(final UseItemPacket packet) {
        final ServerLevel level = this.server.getLevel(this.player.dimension);
        final ItemInstance selected = this.player.inventory.getSelected();
        final ServerLevel serverLevel = level;
        final boolean canEditSpawn = level.dimension.id != 0 || this.server.players.isOp(this.player.name);
        serverLevel.canEditSpawn = canEditSpawn;
        final boolean b = canEditSpawn;
        if (packet.face == 255) {
            if (selected == null) {
                return;
            }
            this.player.gameMode.useItem(this.player, level, selected);
        }
        else {
            int x = packet.x;
            int y = packet.y;
            int z = packet.z;
            final int face = packet.face;
            final Pos sharedSpawnPos = level.getSharedSpawnPos();
            final int n = (int)Mth.abs((float)(x - sharedSpawnPos.x));
            int n2 = (int)Mth.abs((float)(z - sharedSpawnPos.z));
            if (n > n2) {
                n2 = n;
            }
            if (this.synched && this.player.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) < 64.0 && (n2 > 16 || b)) {
                this.player.gameMode.useItemOn(this.player, level, selected, x, y, z, face);
            }
            this.player.connection.send(new TileUpdatePacket(x, y, z, level));
            if (face == 0) {
                --y;
            }
            if (face == 1) {
                ++y;
            }
            if (face == 2) {
                --z;
            }
            if (face == 3) {
                ++z;
            }
            if (face == 4) {
                --x;
            }
            if (face == 5) {
                ++x;
            }
            this.player.connection.send(new TileUpdatePacket(x, y, z, level));
        }
        final ItemInstance selected2 = this.player.inventory.getSelected();
        if (selected2 != null && selected2.count == 0) {
            this.player.inventory.items[this.player.inventory.selected] = null;
        }
        this.player.ignoreSlotUpdateHack = true;
        this.player.inventory.items[this.player.inventory.selected] = ItemInstance.clone(this.player.inventory.items[this.player.inventory.selected]);
        final Slot slot = this.player.containerMenu.getSlotFor(this.player.inventory, this.player.inventory.selected);
        this.player.containerMenu.broadcastChanges();
        this.player.ignoreSlotUpdateHack = false;
        if (!ItemInstance.matches(this.player.inventory.getSelected(), packet.item)) {
            this.send(new ContainerSetSlotPacket(this.player.containerMenu.containerId, slot.index, this.player.inventory.getSelected()));
        }
        level.canEditSpawn = false;
    }
    
    @Override
    public void onDisconnect(final String reason, final Object[] reasonObjects) {
        PlayerConnection.logger.info(this.player.name + " lost connection: " + reason);
        this.server.players.broadcastAll(new ChatPacket("§e" + this.player.name + " left the game."));
        this.server.players.remove(this.player);
        this.done = true;
    }
    
    @Override
    public void onUnhandledPacket(final Packet packet) {
        PlayerConnection.logger.warning(this.getClass() + " wasn't prepared to deal with a " + packet.getClass());
        this.disconnect("Protocol error, unexpected packet");
    }
    
    public void send(final Packet packet) {
        this.connection.send(packet);
        this.lastKeepAliveTick = this.tickCount;
    }
    
    @Override
    public void handleSetCarriedItem(final SetCarriedItemPacket packet) {
        if (packet.slot < 0 || packet.slot > Inventory.getSelectionSize()) {
            PlayerConnection.logger.warning(this.player.name + " tried to set an invalid carried item");
            return;
        }
        this.player.inventory.selected = packet.slot;
    }
    
    @Override
    public void handleChat(final ChatPacket packet) {
        final String message = packet.message;
        if (message.length() > 100) {
            this.disconnect("Chat message too long");
            return;
        }
        final String trim = message.trim();
        for (int i = 0; i < trim.length(); ++i) {
            if (SharedConstants.acceptableLetters.indexOf(trim.charAt(i)) < 0) {
                this.disconnect("Illegal characters in chat");
                return;
            }
        }
        if (trim.startsWith("/")) {
            this.handleCommand(trim);
        }
        else {
            final String string = "<" + this.player.name + "> " + trim;
            PlayerConnection.logger.info(string);
            this.server.players.broadcastAll(new ChatPacket(string));
        }
    }
    
    private void handleCommand(String message) {
        if (message.toLowerCase().startsWith("/me ")) {
            message = "* " + this.player.name + " " + message.substring(message.indexOf(" ")).trim();
            PlayerConnection.logger.info(message);
            this.server.players.broadcastAll(new ChatPacket(message));
        }
        else if (message.toLowerCase().startsWith("/kill")) {
            this.player.hurt(null, 1000);
        }
        else if (message.toLowerCase().startsWith("/tell ")) {
            final String[] split = message.split(" ");
            if (split.length >= 3) {
                message = message.substring(message.indexOf(" ")).trim();
                message = message.substring(message.indexOf(" ")).trim();
                message = "§7" + this.player.name + " whispers " + message;
                PlayerConnection.logger.info(message + " to " + split[1]);
                if (!this.server.players.sendTo(split[1], new ChatPacket(message))) {
                    this.send(new ChatPacket("§cThere's no player by that name online."));
                }
            }
        }
        else if (this.server.players.isOp(this.player.name)) {
            final String substring = message.substring(1);
            PlayerConnection.logger.info(this.player.name + " issued server command: " + substring);
            this.server.handleConsoleInput(substring, this);
        }
        else {
            PlayerConnection.logger.info(this.player.name + " tried command: " + message.substring(1));
        }
    }
    
    @Override
    public void handleAnimate(final AnimatePacket packet) {
        if (packet.action == 1) {
            this.player.swing();
        }
    }
    
    @Override
    public void handlePlayerCommand(final PlayerCommandPacket packet) {
        if (packet.action == 1) {
            this.player.setSneaking(true);
        }
        else if (packet.action == 2) {
            this.player.setSneaking(false);
        }
        else if (packet.action == 3) {
            this.player.stopSleepInBed(false, true, true);
            this.synched = false;
        }
    }
    
    @Override
    public void handleDisconnect(final DisconnectPacket packet) {
        this.connection.close("disconnect.quitting", new Object[0]);
    }
    
    public int countDelayedPackets() {
        return this.connection.countDelayedPackets();
    }
    
    public void info(final String string) {
        this.send(new ChatPacket("§7" + string));
    }
    
    public String getConsoleName() {
        return this.player.name;
    }
    
    @Override
    public void handleInteract(final InteractPacket packet) {
        final Entity entity = this.server.getLevel(this.player.dimension).getEntity(packet.target);
        if (entity != null && this.player.canSee(entity) && this.player.distanceToSqr(entity) < 36.0) {
            if (packet.action == 0) {
                this.player.interact(entity);
            }
            else if (packet.action == 1) {
                this.player.attack(entity);
            }
        }
    }
    
    @Override
    public void handleRespawn(final RespawnPacket packet) {
        if (this.player.health > 0) {
            return;
        }
        this.player = this.server.players.respawn(this.player, 0);
    }
    
    @Override
    public void handleContainerClose(final ContainerClosePacket packet) {
        this.player.doCloseContainer();
    }
    
    @Override
    public void handleContainerClick(final ContainerClickPacket packet) {
        if (this.player.containerMenu.containerId == packet.containerId && this.player.containerMenu.isSynched(this.player)) {
            if (ItemInstance.matches(packet.item, this.player.containerMenu.clicked(packet.slotNum, packet.buttonNum, packet.quickKey, this.player))) {
                this.player.connection.send(new ContainerAckPacket(packet.containerId, packet.uid, true));
                this.player.ignoreSlotUpdateHack = true;
                this.player.containerMenu.broadcastChanges();
                this.player.broadcastCarriedItem();
                this.player.ignoreSlotUpdateHack = false;
            }
            else {
                this.expectedAcks.put(this.player.containerMenu.containerId, packet.uid);
                this.player.connection.send(new ContainerAckPacket(packet.containerId, packet.uid, false));
                this.player.containerMenu.setSynched(this.player, false);
                final ArrayList<ItemInstance> items = new ArrayList<>();
                for (int i = 0; i < this.player.containerMenu.slots.size(); ++i) {
                    items.add(this.player.containerMenu.slots.get(i).getItem());
                }
                this.player.refreshContainer(this.player.containerMenu, items);
            }
        }
    }
    
    @Override
    public void handleContainerAck(final ContainerAckPacket packet) {
        final Short n = this.expectedAcks.get(this.player.containerMenu.containerId);
        if (n != null && packet.uid == n && this.player.containerMenu.containerId == packet.containerId && !this.player.containerMenu.isSynched(this.player)) {
            this.player.containerMenu.setSynched(this.player, true);
        }
    }
    
    @Override
    public void handleSignUpdate(final SignUpdatePacket packet) {
        final ServerLevel level = this.server.getLevel(this.player.dimension);
        if (level.hasChunkAt(packet.x, packet.y, packet.z)) {
            final TileEntity tileEntity = level.getTileEntity(packet.x, packet.y, packet.z);
            if (tileEntity instanceof SignTileEntity && !((SignTileEntity)tileEntity).isEditable()) {
                this.server.warn("Player " + this.player.name + " just tried to change non-editable sign");
                return;
            }
            for (int i = 0; i < 4; ++i) {
                boolean b = true;
                if (packet.lines[i].length() > 15) {
                    b = false;
                }
                else {
                    for (int j = 0; j < packet.lines[i].length(); ++j) {
                        if (SharedConstants.acceptableLetters.indexOf(packet.lines[i].charAt(j)) < 0) {
                            b = false;
                        }
                    }
                }
                if (!b) {
                    packet.lines[i] = "!?";
                }
            }
            if (tileEntity instanceof SignTileEntity) {
                final int x = packet.x;
                final int y = packet.y;
                final int z = packet.z;
                final SignTileEntity signTileEntity = (SignTileEntity)tileEntity;
                for (int k = 0; k < 4; ++k) {
                    signTileEntity.messages[k] = packet.lines[k];
                }
                signTileEntity.setEditable(false);
                signTileEntity.setChanged();
                level.sendTileUpdated(x, y, z);
            }
        }
    }
    
    @Override
    public boolean isServerPacketListener() {
        return true;
    }
    
    static {
        PlayerConnection.logger = Logger.getLogger("Minecraft");
    }
}
