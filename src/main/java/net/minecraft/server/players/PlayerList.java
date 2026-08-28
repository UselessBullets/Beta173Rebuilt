// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.players;

import net.minecraft.network.packet.SetTimePacket;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.network.packet.ChatPacket;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import net.minecraft.world.level.PortalForcer;
import net.minecraft.Pos;
import net.minecraft.network.packet.RespawnPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.GameEventPacket;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.PendingConnection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import java.util.HashSet;
import java.util.ArrayList;
import net.minecraft.world.level.storage.PlayerIO;
import java.io.File;
import java.util.Set;
import net.minecraft.server.MinecraftServer;
import java.util.List;
import java.util.logging.Logger;

public class PlayerList
{
    public static Logger logger;
    public List<ServerPlayer> players;
    private MinecraftServer server;
    private PlayerChunkMap[] chunkMaps;
    private int maxPlayers;
    private Set<String> bans;
    private Set<String> ipBans;
    private Set<String> ops;
    private Set<String> whitelist;
    private File banFile;
    private File ipBanFile;
    private File opFile;
    private File whiteListFile;
    private PlayerIO playerIo;
    private boolean doWhiteList;
    
    public PlayerList(final MinecraftServer server) {
        this.players = new ArrayList<>();
        this.bans = new HashSet<>();
        this.ipBans = new HashSet<>();
        this.ops = new HashSet<>();
        this.whitelist = new HashSet<>();
        this.chunkMaps = new PlayerChunkMap[2];
        this.server = server;
        this.banFile = server.getFile("banned-players.txt");
        this.ipBanFile = server.getFile("banned-ips.txt");
        this.opFile = server.getFile("ops.txt");
        this.whiteListFile = server.getFile("white-list.txt");
        final int int1 = server.settings.getInt("view-distance", 10);
        this.chunkMaps[0] = new PlayerChunkMap(server, 0, int1);
        this.chunkMaps[1] = new PlayerChunkMap(server, -1, int1);
        this.maxPlayers = server.settings.getInt("max-players", 20);
        this.doWhiteList = server.settings.getBoolean("white-list", false);
        this.loadBans();
        this.loadIpBans();
        this.loadOps();
        this.loadWhitelist();
        this.saveBans();
        this.saveIpBans();
        this.saveOps();
        this.saveWhitelist();
    }
    
    public void setLevel(final ServerLevel[] levels) {
        this.playerIo = levels[0].getLevelStorage().getPlayerIO();
    }
    
    public void changeDimension(final ServerPlayer player) {
        this.chunkMaps[0].remove(player);
        this.chunkMaps[1].remove(player);
        this.getChunkMap(player.dimension).add(player);
        this.server.getLevel(player.dimension).cache.create((int)player.x >> 4, (int)player.z >> 4);
    }
    
    public int getMaxRange() {
        return this.chunkMaps[0].getMaxRange();
    }
    
    private PlayerChunkMap getChunkMap(final int dimension) {
        return (dimension == -1) ? this.chunkMaps[1] : this.chunkMaps[0];
    }
    
    public void load(final ServerPlayer player) {
        this.playerIo.load(player);
    }
    
    public void add(final ServerPlayer player) {
        this.players.add(player);
        final ServerLevel level = this.server.getLevel(player.dimension);
        level.cache.create((int)player.x >> 4, (int)player.z >> 4);
        while (level.getCubes(player, player.bb).size() != 0) {
            player.setPos(player.x, player.y + 1.0, player.z);
        }
        level.addEntity(player);
        this.getChunkMap(player.dimension).add(player);
    }
    
    public void move(final ServerPlayer player) {
        this.getChunkMap(player.dimension).move(player);
    }
    
    public void remove(final ServerPlayer player) {
        this.playerIo.save(player);
        this.server.getLevel(player.dimension).removeEntity(player);
        this.players.remove(player);
        this.getChunkMap(player.dimension).remove(player);
    }
    
    public ServerPlayer getPlayerForLogin(final PendingConnection pendingConnection, final String userName) {
        if (this.bans.contains(userName.trim().toLowerCase())) {
            pendingConnection.disconnect("You are banned from this server!");
            return null;
        }
        if (!this.isWhiteListed(userName)) {
            pendingConnection.disconnect("You are not white-listed on this server!");
            return null;
        }
        final String string = pendingConnection.connection.getRemoteAddress().toString();
        final String substring = string.substring(string.indexOf("/") + 1);
        if (this.ipBans.contains(substring.substring(0, substring.indexOf(":")))) {
            pendingConnection.disconnect("Your IP address is banned from this server!");
            return null;
        }
        if (this.players.size() >= this.maxPlayers) {
            pendingConnection.disconnect("The server is full!");
            return null;
        }
        for (int i = 0; i < this.players.size(); ++i) {
            final ServerPlayer serverPlayer = this.players.get(i);
            if (serverPlayer.name.equalsIgnoreCase(userName)) {
                serverPlayer.connection.disconnect("You logged in from another location");
            }
        }
        return new ServerPlayer(this.server, this.server.getLevel(0), userName, new ServerPlayerGameMode(this.server.getLevel(0)));
    }
    
    public ServerPlayer respawn(final ServerPlayer serverPlayer, final int targetDimension) {
        this.server.getTracker(serverPlayer.dimension).clear(serverPlayer);
        this.server.getTracker(serverPlayer.dimension).removePlayer(serverPlayer);
        this.getChunkMap(serverPlayer.dimension).remove(serverPlayer);
        this.players.remove(serverPlayer);
        this.server.getLevel(serverPlayer.dimension).removeEntityImmediately(serverPlayer);
        final Pos respawnPosition = serverPlayer.getRespawnPosition();
        serverPlayer.dimension = targetDimension;
        final ServerPlayer serverPlayer2 = new ServerPlayer(this.server, this.server.getLevel(serverPlayer.dimension), serverPlayer.name, new ServerPlayerGameMode(this.server.getLevel(serverPlayer.dimension)));
        serverPlayer2.entityId = serverPlayer.entityId;
        serverPlayer2.connection = serverPlayer.connection;
        final ServerLevel level = this.server.getLevel(serverPlayer.dimension);
        if (respawnPosition != null) {
            final Pos checkBedValidRespawnPosition = Player.checkBedValidRespawnPosition(this.server.getLevel(serverPlayer.dimension), respawnPosition);
            if (checkBedValidRespawnPosition != null) {
                serverPlayer2.moveTo(checkBedValidRespawnPosition.x + 0.5f, checkBedValidRespawnPosition.y + 0.1f, checkBedValidRespawnPosition.z + 0.5f, 0.0f, 0.0f);
                serverPlayer2.setRespawnPosition(respawnPosition);
            }
            else {
                serverPlayer2.connection.send(new GameEventPacket(GameEventPacket.NO_RESPAWN_BED_AVAILABLE));
            }
        }
        level.cache.create((int)serverPlayer2.x >> 4, (int)serverPlayer2.z >> 4);
        while (level.getCubes(serverPlayer2, serverPlayer2.bb).size() != 0) {
            serverPlayer2.setPos(serverPlayer2.x, serverPlayer2.y + 1.0, serverPlayer2.z);
        }
        serverPlayer2.connection.send(new RespawnPacket((byte)serverPlayer2.dimension));
        serverPlayer2.connection.teleport(serverPlayer2.x, serverPlayer2.y, serverPlayer2.z, serverPlayer2.yRot, serverPlayer2.xRot);
        this.sendLevelInfo(serverPlayer2, level);
        this.getChunkMap(serverPlayer2.dimension).add(serverPlayer2);
        level.addEntity(serverPlayer2);
        this.players.add(serverPlayer2);
        serverPlayer2.initMenu();
        serverPlayer2.animateRespawn();
        return serverPlayer2;
    }
    
    public void toggleDimension(final ServerPlayer player) {
        final ServerLevel level = this.server.getLevel(player.dimension);
        int dimension;
        if (player.dimension == -1) {
            dimension = 0;
        }
        else {
            dimension = -1;
        }
        player.dimension = dimension;
        final ServerLevel level2 = this.server.getLevel(player.dimension);
        player.connection.send(new RespawnPacket((byte)player.dimension));
        level.removeEntityImmediately(player);
        player.removed = false;
        final double x = player.x;
        final double z = player.z;
        final double n = 8.0;
        double x2;
        double z2;
        if (player.dimension == -1) {
            x2 = x / n;
            z2 = z / n;
            player.moveTo(x2, player.y, z2, player.yRot, player.xRot);
            if (player.isAlive()) {
                level.tick(player, false);
            }
        }
        else {
            x2 = x * n;
            z2 = z * n;
            player.moveTo(x2, player.y, z2, player.yRot, player.xRot);
            if (player.isAlive()) {
                level.tick(player, false);
            }
        }
        if (player.isAlive()) {
            level2.addEntity(player);
            player.moveTo(x2, player.y, z2, player.yRot, player.xRot);
            level2.tick(player, false);
            level2.cache.autoCreate = true;
            new PortalForcer().force(level2, player);
            level2.cache.autoCreate = false;
        }
        this.changeDimension(player);
        player.connection.teleport(player.x, player.y, player.z, player.yRot, player.xRot);
        player.setLevel(level2);
        this.sendLevelInfo(player, level2);
        this.sendAllPlayerInfo(player);
    }
    
    public void tick() {
        for (int i = 0; i < this.chunkMaps.length; ++i) {
            this.chunkMaps[i].tick();
        }
    }
    
    public void isTrackingTile(final int x, final int y, final int z, final int dimension) {
        this.getChunkMap(dimension).isTrackingTile(x, y, z);
    }
    
    public void broadcastAll(final Packet packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get(i).connection.send(packet);
        }
    }
    
    public void broadcastAll(final Packet packet, final int dimension) {
        for (int i = 0; i < this.players.size(); ++i) {
            final ServerPlayer serverPlayer = this.players.get(i);
            if (serverPlayer.dimension == dimension) {
                serverPlayer.connection.send(packet);
            }
        }
    }
    
    public String getPlayerNames() {
        String s = "";
        for (int i = 0; i < this.players.size(); ++i) {
            if (i > 0) {
                s += ", ";
            }
            s += this.players.get(i).name;
        }
        return s;
    }
    
    public void ban(final String name) {
        this.bans.add(name.toLowerCase());
        this.saveBans();
    }
    
    public void pardon(final String name) {
        this.bans.remove(name.toLowerCase());
        this.saveBans();
    }
    
    private void loadBans() {
        try {
            this.bans.clear();
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(this.banFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.bans.add(line.trim().toLowerCase());
            }
            bufferedReader.close();
        }
        catch (final Exception obj) {
            PlayerList.logger.warning("Failed to load ban list: " + obj);
        }
    }
    
    private void saveBans() {
        try {
            final PrintWriter printWriter = new PrintWriter(new FileWriter(this.banFile, false));
            final Iterator<String> iterator = this.bans.iterator();
            while (iterator.hasNext()) {
                printWriter.println(iterator.next());
            }
            printWriter.close();
        }
        catch (final Exception obj) {
            PlayerList.logger.warning("Failed to save ban list: " + obj);
        }
    }
    
    public void ipBan(final String ip) {
        this.ipBans.add(ip.toLowerCase());
        this.saveIpBans();
    }
    
    public void ipParden(final String ip) {
        this.ipBans.remove(ip.toLowerCase());
        this.saveIpBans();
    }
    
    private void loadIpBans() {
        try {
            this.ipBans.clear();
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(this.ipBanFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.ipBans.add(line.trim().toLowerCase());
            }
            bufferedReader.close();
        }
        catch (final Exception obj) {
            PlayerList.logger.warning("Failed to load ip ban list: " + obj);
        }
    }
    
    private void saveIpBans() {
        try {
            final PrintWriter printWriter = new PrintWriter(new FileWriter(this.ipBanFile, false));
            final Iterator<String> iterator = this.ipBans.iterator();
            while (iterator.hasNext()) {
                printWriter.println(iterator.next());
            }
            printWriter.close();
        }
        catch (final Exception obj) {
            PlayerList.logger.warning("Failed to save ip ban list: " + obj);
        }
    }
    
    public void op(final String name) {
        this.ops.add(name.toLowerCase());
        this.saveOps();
    }
    
    public void deop(final String name) {
        this.ops.remove(name.toLowerCase());
        this.saveOps();
    }
    
    private void loadOps() {
        try {
            this.ops.clear();
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(this.opFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.ops.add(line.trim().toLowerCase());
            }
            bufferedReader.close();
        }
        catch (final Exception obj) {
            PlayerList.logger.warning("Failed to load ip ban list: " + obj);
        }
    }
    
    private void saveOps() {
        try {
            final PrintWriter printWriter = new PrintWriter(new FileWriter(this.opFile, false));
            final Iterator<String> iterator = this.ops.iterator();
            while (iterator.hasNext()) {
                printWriter.println(iterator.next());
            }
            printWriter.close();
        }
        catch (final Exception obj) {
            PlayerList.logger.warning("Failed to save ip ban list: " + obj);
        }
    }
    
    private void loadWhitelist() {
        try {
            this.whitelist.clear();
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(this.whiteListFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.whitelist.add(line.trim().toLowerCase());
            }
            bufferedReader.close();
        }
        catch (final Exception obj) {
            PlayerList.logger.warning("Failed to load white-list: " + obj);
        }
    }
    
    private void saveWhitelist() {
        try {
            final PrintWriter printWriter = new PrintWriter(new FileWriter(this.whiteListFile, false));
            final Iterator<String> iterator = this.whitelist.iterator();
            while (iterator.hasNext()) {
                printWriter.println(iterator.next());
            }
            printWriter.close();
        }
        catch (final Exception obj) {
            PlayerList.logger.warning("Failed to save white-list: " + obj);
        }
    }
    
    public boolean isWhiteListed(String name) {
        name = name.trim().toLowerCase();
        return !this.doWhiteList || this.ops.contains(name) || this.whitelist.contains(name);
    }
    
    public boolean isOp(final String name) {
        return this.ops.contains(name.trim().toLowerCase());
    }
    
    public ServerPlayer getPlayer(final String name) {
        for (int i = 0; i < this.players.size(); ++i) {
            final ServerPlayer serverPlayer = this.players.get(i);
            if (serverPlayer.name.equalsIgnoreCase(name)) {
                return serverPlayer;
            }
        }
        return null;
    }
    
    public void sendMessage(final String name, final String message) {
        final ServerPlayer player = this.getPlayer(name);
        if (player != null) {
            player.connection.send(new ChatPacket(message));
        }
    }
    
    public void broadcast(final double x, final double y, final double z, final double range, final int dimension, final Packet packet) {
        this.broadcast(null, x, y, z, range, dimension, packet);
    }
    
    public void broadcast(final Player except, final double x, final double y, final double z, final double range, final int dimension, final Packet packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            final ServerPlayer serverPlayer = this.players.get(i);
            if (serverPlayer != except) {
                if (serverPlayer.dimension == dimension) {
                    final double n = x - serverPlayer.x;
                    final double n2 = y - serverPlayer.y;
                    final double n3 = z - serverPlayer.z;
                    if (n * n + n2 * n2 + n3 * n3 < range * range) {
                        serverPlayer.connection.send(packet);
                    }
                }
            }
        }
    }
    
    public void broadcastToAllOps(final String message) {
        final ChatPacket packet = new ChatPacket(message);
        for (int i = 0; i < this.players.size(); ++i) {
            final ServerPlayer serverPlayer = this.players.get(i);
            if (this.isOp(serverPlayer.name)) {
                serverPlayer.connection.send(packet);
            }
        }
    }
    
    public boolean sendTo(final String name, final Packet packet) {
        final ServerPlayer player = this.getPlayer(name);
        if (player != null) {
            player.connection.send(packet);
            return true;
        }
        return false;
    }
    
    public void saveAll() {
        for (int i = 0; i < this.players.size(); ++i) {
            this.playerIo.save((Player)this.players.get(i));
        }
    }
    
    public void isTrackingTileEntity(final int x, final int y, final int z, final TileEntity te) {
    }
    
    public void whitelist(final String name) {
        this.whitelist.add(name);
        this.saveWhitelist();
    }
    
    public void blackList(final String name) {
        this.whitelist.remove(name);
        this.saveWhitelist();
    }
    
    public Set<String> getWhiteList() {
        return this.whitelist;
    }
    
    public void reloadWhitelist() {
        this.loadWhitelist();
    }
    
    public void sendLevelInfo(final ServerPlayer player, final ServerLevel level) {
        player.connection.send(new SetTimePacket(level.getTime()));
        if (level.isRaining()) {
            player.connection.send(new GameEventPacket(GameEventPacket.START_RAINING));
        }
    }
    
    public void sendAllPlayerInfo(final ServerPlayer player) {
        player.refreshContainer(player.inventoryMenu);
        player.resetSentInfo();
    }
    
    static {
        PlayerList.logger = Logger.getLogger("Minecraft");
    }
}
