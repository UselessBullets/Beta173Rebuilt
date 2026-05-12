// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.network;

import net.minecraft.Pos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.packet.SetTimePacket;
import net.minecraft.network.packet.ChatPacket;
import net.minecraft.network.packet.SetSpawnPositionPacket;
import net.minecraft.world.level.Level;
import net.minecraft.network.packet.PreLoginPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.DisconnectPacket;
import java.net.Socket;
import net.minecraft.network.packet.LoginPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.Connection;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.network.packet.PacketListener;

public class PendingConnection extends PacketListener
{
    public static Logger logger;
    private static Random random;
    public Connection connection;
    public boolean done;
    private MinecraftServer server;
    private int tick;
    private String name;
    private LoginPacket acceptedLogin;
    private String loginKey;
    
    public PendingConnection(final MinecraftServer server, final Socket socket, final String id) {
        this.done = false;
        this.tick = 0;
        this.name = null;
        this.acceptedLogin = null;
        this.loginKey = "";
        this.server = server;
        this.connection = new Connection(socket, id, this);
        this.connection.fakeLag = 0;
    }
    
    public void tick() {
        if (this.acceptedLogin != null) {
            this.handleAcceptedLogin(this.acceptedLogin);
            this.acceptedLogin = null;
        }
        if (this.tick++ == 600) {
            this.disconnect("Took too long to log in");
        }
        else {
            this.connection.tick();
        }
    }
    
    public void disconnect(final String reason) {
        try {
            PendingConnection.logger.info("Disconnecting " + this.getName() + ": " + reason);
            this.connection.send(new DisconnectPacket(reason));
            this.connection.sendAndQuit();
            this.done = true;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void handlePreLogin(final PreLoginPacket packet) {
        if (this.server.onlineMode) {
            this.loginKey = Long.toHexString(PendingConnection.random.nextLong());
            this.connection.send(new PreLoginPacket(this.loginKey));
        }
        else {
            this.connection.send(new PreLoginPacket("-"));
        }
    }
    
    @Override
    public void handleLogin(final LoginPacket packet) {
        this.name = packet.userName;
        if (packet.clientVersion != 14) {
            if (packet.clientVersion > 14) {
                this.disconnect("Outdated server!");
            }
            else {
                this.disconnect("Outdated client!");
            }
            return;
        }
        if (!this.server.onlineMode) {
            this.handleAcceptedLogin(packet);
        }
        else {
            new PendingConnection_VerifyUserThread(this, packet).start();
        }
    }
    
    public void handleAcceptedLogin(final LoginPacket packet) {
        final ServerPlayer playerForLogin = this.server.players.getPlayerForLogin(this, packet.userName);
        if (playerForLogin != null) {
            this.server.players.load(playerForLogin);
            playerForLogin.setLevel(this.server.getLevel(playerForLogin.dimension));
            PendingConnection.logger.info(this.getName() + " logged in with entity id " + playerForLogin.entityId + " at (" + playerForLogin.x + ", " + playerForLogin.y + ", " + playerForLogin.z + ")");
            final ServerLevel level = this.server.getLevel(playerForLogin.dimension);
            final Pos sharedSpawnPos = level.getSharedSpawnPos();
            final PlayerConnection uc = new PlayerConnection(this.server, this.connection, playerForLogin);
            uc.send(new LoginPacket("", playerForLogin.entityId, level.getSeed(), (byte)level.dimension.id));
            uc.send(new SetSpawnPositionPacket(sharedSpawnPos.x, sharedSpawnPos.y, sharedSpawnPos.z));
            this.server.players.sendLevelInfo(playerForLogin, level);
            this.server.players.broadcastAll(new ChatPacket("§e" + playerForLogin.name + " joined the game."));
            this.server.players.add(playerForLogin);
            uc.teleport(playerForLogin.x, playerForLogin.y, playerForLogin.z, playerForLogin.yRot, playerForLogin.xRot);
            this.server.connection.addPlayerConnection(uc);
            uc.send(new SetTimePacket(level.getTime()));
            playerForLogin.initMenu();
        }
        this.done = true;
    }
    
    @Override
    public void onDisconnect(final String reason, final Object[] reasonObjects) {
        PendingConnection.logger.info(this.getName() + " lost connection");
        this.done = true;
    }
    
    @Override
    public void onUnhandledPacket(final Packet packet) {
        this.disconnect("Protocol error");
    }
    
    public String getName() {
        if (this.name != null) {
            return this.name + " [" + this.connection.getRemoteAddress().toString() + "]";
        }
        return this.connection.getRemoteAddress().toString();
    }
    
    @Override
    public boolean isServerPacketListener() {
        return true;
    }
    
    static {
        PendingConnection.logger = Logger.getLogger("Minecraft");
        PendingConnection.random = new Random();
    }
}
