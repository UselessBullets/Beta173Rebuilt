// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.network;

import net.minecraft.world.level.Level;
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
    public static Logger logger = Logger.getLogger("Minecraft");
    public Connection connection;
    public boolean done = false;
    private MinecraftServer server;
    private ServerPlayer player;
    private int tickCount;
    private int lastKeepAliveTick;
    private int aboveGroundTickCount;
    private boolean didTick;
    private double xLastOk, yLastOk, zLastOk;
    private boolean synched = true;
    private Map<Integer, Short> expectedAcks = new HashMap<>();
    
    public PlayerConnection(final MinecraftServer server, final Connection connection, final ServerPlayer player) {
        this.server = server;
        this.connection = connection;
        this.connection.setListener(this);
        this.player = player;
        player.connection = this;
    }
    
    public void tick() {
        this.didTick = false;
        this.connection.tick();
        if (this.tickCount - this.lastKeepAliveTick > SharedConstants.TICKS_PER_SECOND * 1) {
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
            final double yDiff = packet.y - this.yLastOk;
            if (packet.x == this.xLastOk && yDiff * yDiff < 0.01 && packet.z == this.zLastOk) {
                this.synched = true;
            }
        }

        if (this.synched) {
            if (this.player.riding != null) {
                float yRotT = this.player.yRot;
                float xRotT = this.player.xRot;
                this.player.riding.positionRider();
                final double xt = this.player.x;
                final double yt = this.player.y;
                final double zt = this.player.z;
                double xxa = 0.0;
                double zza = 0.0;
                if (packet.hasRot) {
                    yRotT = packet.yRot;
                    xRotT = packet.xRot;
                }
                if (packet.hasPos && packet.y == -999.0 && packet.yView == -999.0) {
                    xxa = packet.x;
                    zza = packet.z;
                }

                this.player.onGround = packet.onGround;

                this.player.doTick(true);
                this.player.move(xxa, 0.0, zza);
                this.player.absMoveTo(xt, yt, zt, yRotT, xRotT);
                this.player.xd = xxa;
                this.player.zd = zza;
                if (this.player.riding != null) level.forceTick(this.player.riding, true);
                if (this.player.riding != null) this.player.riding.positionRider();
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

            final double startY = this.player.y;
            this.xLastOk = this.player.x;
            this.yLastOk = this.player.y;
            this.zLastOk = this.player.z;

            double xt = this.player.x;
            double yt = this.player.y;
            double zt = this.player.z;

            float yRotT = this.player.yRot;
            float xRotT = this.player.xRot;

            if (packet.hasPos && packet.y == -999.0 && packet.yView == -999.0) {
                packet.hasPos = false;
            }

            if (packet.hasPos) {
                xt = packet.x;
                yt = packet.y;
                zt = packet.z;
                final double yd = packet.yView - packet.y;
                if (!this.player.isSleeping() && (yd > 1.65 || yd < 0.1)) {
                    this.disconnect("Illegal stance");
                    PlayerConnection.logger.warning(this.player.name + " had an illegal stance: " + yd);
                    return;
                }
                if (Math.abs(packet.x) > Level.MAX_LEVEL_SIZE || Math.abs(packet.z) > Level.MAX_LEVEL_SIZE) {
                    this.disconnect("Illegal position");
                    return;
                }
            }
            if (packet.hasRot) {
                yRotT = packet.yRot;
                xRotT = packet.xRot;
            }

            this.player.doTick(true);
            this.player.ySlideOffset = 0.0f;
            this.player.absMoveTo(this.xLastOk, this.yLastOk, this.zLastOk, yRotT, xRotT);

            if (!this.synched) return;

            double xDist = xt - this.player.x;
            double yDist = yt - this.player.y;
            double zDist = zt - this.player.z;
            double dist = xDist * xDist + yDist * yDist + zDist * zDist;
            if (dist > 100.0) {
                PlayerConnection.logger.warning(this.player.name + " moved too quickly!");
                this.disconnect("You moved too quickly :( (Hacking?)");
                return;
            }

            final float r = 1 / 16.0f;
            final boolean oldOk = level.getCubes(this.player, this.player.bb.copy().shrink(r, r, r)).isEmpty();
            this.player.move(xDist, yDist, zDist);

            xDist = xt - this.player.x;
            yDist = yt - this.player.y;
            if (yDist > -0.5 || yDist < 0.5) {
                yDist = 0.0;
            }
            zDist = zt - this.player.z;

            dist = xDist * xDist + yDist * yDist + zDist * zDist;
            boolean fail = false;
            if (dist > (0.25 * 0.25) && !this.player.isSleeping()) {
                fail = true;
                PlayerConnection.logger.warning(this.player.name + " moved wrongly!");
                System.out.println("Got position " + xt + ", " + yt + ", " + zt);
                System.out.println("Expected " + this.player.x + ", " + this.player.y + ", " + this.player.z);
            }
            this.player.absMoveTo(xt, yt, zt, yRotT, xRotT);

            final boolean newOk = level.getCubes(this.player, this.player.bb.copy().shrink(r, r, r)).isEmpty();
            if (oldOk && (fail || !newOk) && !this.player.isSleeping()) {
                this.teleport(this.xLastOk, this.yLastOk, this.zLastOk, yRotT, xRotT);
                return;
            }

            final AABB testBox = this.player.bb.copy().grow(r, r, r).expand(0.0, -0.55, 0.0);
            if (!this.server.isFlightAllowed && !level.containsAnyBlocks(testBox)) {
                if (yDist >= -0.5f / 16.0f) {
                    this.aboveGroundTickCount++;
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
            this.player.doCheckFallDamage(this.player.y - startY, packet.onGround);
        }
    }
    
    public void teleport(final double x, final double y, final double z, final float yRot, final float xRot) {
        this.synched = false;
        this.xLastOk = x;
        this.yLastOk = y;
        this.zLastOk = z;
        this.player.absMoveTo(x, y, z, yRot, xRot);
        this.player.connection.send(new MovePlayerPacket.PosRot(x, y + 1.62f, y, z, yRot, xRot, false));
    }
    
    @Override
    public void handlePlayerAction(final PlayerActionPacket packet) {
        final ServerLevel level = this.server.getLevel(this.player.dimension);

        if (packet.action == PlayerActionPacket.DROP_ITEM) {
            this.player.drop();
            return;
        }

        final boolean canEditSpawn = level.dimension.id != 0 || this.server.players.isOp(this.player.name);
        level.canEditSpawn = canEditSpawn;
        boolean shouldVerifyLocation = false;
        if (packet.action == PlayerActionPacket.START_DESTROY_BLOCK) shouldVerifyLocation = true;
        if (packet.action == PlayerActionPacket.STOP_DESTROY_BLOCK) shouldVerifyLocation = true;

        final int x = packet.x;
        final int y = packet.y;
        final int z = packet.z;
        if (shouldVerifyLocation) {
            final double xDist = this.player.x - (x + 0.5);
            final double yDist = this.player.y - (y + 0.5);
            final double zDist = this.player.z - (z + 0.5);
            if (xDist * xDist + yDist * yDist + zDist * zDist > 6 * 6) return;
        }

        final Pos spawnPos = level.getSharedSpawnPos();
        int xd = (int)Mth.abs((float)(x - spawnPos.x));
        int zd = (int)Mth.abs((float)(z - spawnPos.z));
        if (xd > zd) zd = xd;

        if (packet.action == PlayerActionPacket.START_DESTROY_BLOCK) {
            if (zd > 16 || canEditSpawn) this.player.gameMode.startDestroyBlock(x, y, z, packet.face);
            else this.player.connection.send(new TileUpdatePacket(x, y, z, level));
        }
        else if (packet.action == PlayerActionPacket.STOP_DESTROY_BLOCK) {
            this.player.gameMode.stopDestroyBlock(x, y, z);
            if (level.getTile(x, y, z) != 0) this.player.connection.send(new TileUpdatePacket(x, y, z, level));
        }
        else if (packet.action == PlayerActionPacket.GET_UPDATED_BLOCK) {
            final double xDist = this.player.x - (x + 0.5);
            final double yDist = this.player.y - (y + 0.5);
            final double zDist = this.player.z - (z + 0.5);
            if (xDist * xDist + yDist * yDist + zDist * zDist < 16 * 16) {
                this.player.connection.send(new TileUpdatePacket(x, y, z, level));
            }
        }

        level.canEditSpawn = false;
    }
    
    @Override
    public void handleUseItem(final UseItemPacket packet) {
        ServerLevel level = this.server.getLevel(this.player.dimension);
        ItemInstance item = this.player.inventory.getSelected();

        final boolean canEditSpawn = level.canEditSpawn = level.dimension.id != 0 || this.server.players.isOp(this.player.name);
        if (packet.face == 255) {
            if (item == null) return;
            this.player.gameMode.useItem(this.player, level, item);
        }
        else {
            int x = packet.x;
            int y = packet.y;
            int z = packet.z;
            final int face = packet.face;

            final Pos spawnPos = level.getSharedSpawnPos();
            int xd = (int)Mth.abs((float)(x - spawnPos.x));
            int zd = (int)Mth.abs((float)(z - spawnPos.z));
            if (xd > zd) zd = xd;

            if (this.synched && this.player.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) < 8 * 8) {
                if (zd > 16 || canEditSpawn) {
                    this.player.gameMode.useItemOn(this.player, level, item, x, y, z, face);
                }
            }

            this.player.connection.send(new TileUpdatePacket(x, y, z, level));

            if (face == 0) y--;
            if (face == 1) y++;
            if (face == 2) z--;
            if (face == 3) z++;
            if (face == 4) x--;
            if (face == 5) x++;

            this.player.connection.send(new TileUpdatePacket(x, y, z, level));
        }

        item = this.player.inventory.getSelected();
        if (item != null && item.count == 0) {
            this.player.inventory.items[this.player.inventory.selected] = null;
        }

        this.player.ignoreSlotUpdateHack = true;
        this.player.inventory.items[this.player.inventory.selected] = ItemInstance.clone(this.player.inventory.items[this.player.inventory.selected]);
        final Slot s = this.player.containerMenu.getSlotFor(this.player.inventory, this.player.inventory.selected);
        this.player.containerMenu.broadcastChanges();
        this.player.ignoreSlotUpdateHack = false;

        if (!ItemInstance.matches(this.player.inventory.getSelected(), packet.item)) {
            this.send(new ContainerSetSlotPacket(this.player.containerMenu.containerId, s.index, this.player.inventory.getSelected()));
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
        String message = packet.message;
        if (message.length() > SharedConstants.maxChatLength) {
            this.disconnect("Chat message too long");
            return;
        }
        message = message.trim();
        for (int i = 0; i < message.length(); ++i) {
            if (SharedConstants.acceptableLetters.indexOf(message.charAt(i)) < 0) {
                this.disconnect("Illegal characters in chat");
                return;
            }
        }

        if (message.startsWith("/")) {
            this.handleCommand(message);
        }
        else {
            final String string = "<" + this.player.name + "> " + message;
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
            final String command = message.substring(1);
            PlayerConnection.logger.info(this.player.name + " issued server command: " + command);
            this.server.handleConsoleInput(command, this);
        }
        else {
            PlayerConnection.logger.info(this.player.name + " tried command: " + message.substring(1));
        }
    }
    
    @Override
    public void handleAnimate(final AnimatePacket packet) {
        if (packet.action == AnimatePacket.SWING) {
            this.player.swing();
        }
    }
    
    @Override
    public void handlePlayerCommand(final PlayerCommandPacket packet) {
        if (packet.action == PlayerCommandPacket.START_SNEAKING) {
            this.player.setSneaking(true);
        }
        else if (packet.action == PlayerCommandPacket.STOP_SNEAKING) {
            this.player.setSneaking(false);
        }
        else if (packet.action == PlayerCommandPacket.STOP_SLEEPING) {
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

    @Override
    // Useless - In LCE
    public void warn(String string) {
        this.send(new ChatPacket("§9" + string));
    }

    public String getConsoleName() {
        return this.player.name;
    }
    
    @Override
    public void handleInteract(final InteractPacket packet) {
        ServerLevel level = this.server.getLevel(this.player.dimension);
        final Entity target = level.getEntity(packet.target);

        if (target != null && this.player.canSee(target) && this.player.distanceToSqr(target) < 36.0) {
            if (packet.action == InteractPacket.INTERACT) {
                this.player.interact(target);
            }
            else if (packet.action == InteractPacket.ATTACK) {
                this.player.attack(target);
            }
        }
    }
    
    @Override
    public void handleRespawn(final RespawnPacket packet) {
        if (this.player.health > 0) return;
        this.player = this.server.players.respawn(this.player, 0);
    }
    
    @Override
    public void handleContainerClose(final ContainerClosePacket packet) {
        this.player.doCloseContainer();
    }
    
    @Override
    public void handleContainerClick(final ContainerClickPacket packet) {
        if (this.player.containerMenu.containerId == packet.containerId && this.player.containerMenu.isSynched(this.player)) {
            ItemInstance clicked = this.player.containerMenu.clicked(packet.slotNum, packet.buttonNum, packet.quickKey, this.player);

            if (ItemInstance.matches(packet.item, clicked)) {
                // Yep, you sure did click what you claimed to click!
                this.player.connection.send(new ContainerAckPacket(packet.containerId, packet.uid, true));
                this.player.ignoreSlotUpdateHack = true;
                this.player.containerMenu.broadcastChanges();
                this.player.broadcastCarriedItem();
                this.player.ignoreSlotUpdateHack = false;
            }
            else {
                // No, you clicked the wrong thing!
                this.expectedAcks.put(this.player.containerMenu.containerId, packet.uid);
                this.player.connection.send(new ContainerAckPacket(packet.containerId, packet.uid, false));
                this.player.containerMenu.setSynched(this.player, false);
                final ArrayList<ItemInstance> items = new ArrayList<>();
                for (int i = 0; i < this.player.containerMenu.slots.size(); ++i) {
                    items.add(this.player.containerMenu.slots.get(i).getItem());
                }
                this.player.refreshContainer(this.player.containerMenu, items);

//                this.player.containerMenu.broadcastChanges();
            }
        }
    }
    
    @Override
    public void handleContainerAck(final ContainerAckPacket packet) {
        final Short ack = this.expectedAcks.get(this.player.containerMenu.containerId);
        if (ack != null && packet.uid == ack && this.player.containerMenu.containerId == packet.containerId && !this.player.containerMenu.isSynched(this.player)) {
            this.player.containerMenu.setSynched(this.player, true);
        }
    }
    
    @Override
    public void handleSignUpdate(final SignUpdatePacket packet) {
        final ServerLevel level = this.server.getLevel(this.player.dimension);
        if (level.hasChunkAt(packet.x, packet.y, packet.z)) {
            final TileEntity te = level.getTileEntity(packet.x, packet.y, packet.z);
            if (te instanceof SignTileEntity) {
                SignTileEntity ste = (SignTileEntity) te;
                if (!ste.isEditable()) {
                    this.server.warn("Player " + this.player.name + " just tried to change non-editable sign");
                    return;
                }
            }

            for (int i = 0; i < 4; ++i) {
                boolean validLine = true;
                if (packet.lines[i].length() > SignTileEntity.MAX_LINE_LENGTH) {
                    validLine = false;
                }
                else {
                    for (int c = 0; c < packet.lines[i].length(); ++c) {
                        char ch = packet.lines[i].charAt(c);
                        if (SharedConstants.acceptableLetters.indexOf(ch) < 0) validLine = false;
                    }
                }
                if (!validLine) packet.lines[i] = "!?";
            }

            if (te instanceof SignTileEntity) {
                final int x = packet.x;
                final int y = packet.y;
                final int z = packet.z;
                final SignTileEntity ste = (SignTileEntity)te;
                for (int i = 0; i < SignTileEntity.MAX_SIGN_LINES; ++i) {
                    ste.messages[i] = packet.lines[i];
                }
                ste.setEditable(false);
                ste.setChanged();
                level.sendTileUpdated(x, y, z);
            }
        }
    }
    
    @Override
    public boolean isServerPacketListener() {
        return true;
    }

}
