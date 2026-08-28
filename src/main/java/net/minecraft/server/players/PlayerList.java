// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.players;

import net.minecraft.network.packet.SetTimePacket;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.network.packet.ChatPacket;

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
    public static Logger logger = Logger.getLogger("Minecraft");
    public List<ServerPlayer> players = new ArrayList<>();
    private MinecraftServer server;
    private PlayerChunkMap[] chunkMaps = new PlayerChunkMap[2];
    private int maxPlayers;
    private Set<String> bans = new HashSet<>();
    private Set<String> ipBans = new HashSet<>();
    private Set<String> ops = new HashSet<>();
    private Set<String> whitelist = new HashSet<>();
    private File banFile, ipBanFile, opFile, whiteListFile;
    private PlayerIO playerIo;
    private boolean doWhiteList;
    
    public PlayerList(final MinecraftServer server) {
        this.server = server;
        this.banFile = server.getFile("banned-players.txt");
        this.ipBanFile = server.getFile("banned-ips.txt");
        this.opFile = server.getFile("ops.txt");
        this.whiteListFile = server.getFile("white-list.txt");

        final int viewDistance = server.settings.getInt("view-distance", 10);
        this.chunkMaps[0] = new PlayerChunkMap(server, 0, viewDistance);
        this.chunkMaps[1] = new PlayerChunkMap(server, -1, viewDistance);

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

        ServerLevel to = this.server.getLevel(player.dimension);
        to.cache.create((int)player.x >> 4, (int)player.z >> 4);
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

        // Ensure the area the player is spawning in is loaded!
        final ServerLevel level = this.server.getLevel(player.dimension);
        level.cache.create((int)player.x >> 4, (int)player.z >> 4);
        while (!level.getCubes(player, player.bb).isEmpty()) {
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
        ServerLevel level = this.server.getLevel(player.dimension);
        level.removeEntity(player);
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

        String ip = pendingConnection.connection.getRemoteAddress().toString();
        ip = ip.substring(ip.indexOf("/") + 1);
        ip = ip.substring(0, ip.indexOf(":"));
        if (this.ipBans.contains(ip)) {
            pendingConnection.disconnect("Your IP address is banned from this server!");
            return null;
        }

        if (this.players.size() >= this.maxPlayers) {
            pendingConnection.disconnect("The server is full!");
            return null;
        }

        for (int i = 0; i < this.players.size(); ++i) {
            final ServerPlayer player = this.players.get(i);
            if (player.name.equalsIgnoreCase(userName)) {
                player.connection.disconnect("You logged in from another location");
            }
        }

        return new ServerPlayer(this.server, this.server.getLevel(0), userName, new ServerPlayerGameMode(this.server.getLevel(0)));
    }
    
    public ServerPlayer respawn(final ServerPlayer serverPlayer, final int targetDimension) {
        this.server.getTracker(serverPlayer.dimension).clear(serverPlayer);
        this.server.getTracker(serverPlayer.dimension).removeEntity(serverPlayer);
        this.getChunkMap(serverPlayer.dimension).remove(serverPlayer);
        this.players.remove(serverPlayer);
        this.server.getLevel(serverPlayer.dimension).removeEntityImmediately(serverPlayer);

        final Pos bedPosition = serverPlayer.getRespawnPosition();
        serverPlayer.dimension = targetDimension;

        final ServerPlayer player = new ServerPlayer(this.server, this.server.getLevel(serverPlayer.dimension), serverPlayer.name, new ServerPlayerGameMode(this.server.getLevel(serverPlayer.dimension)));
        player.entityId = serverPlayer.entityId;
        player.connection = serverPlayer.connection;

        final ServerLevel level = this.server.getLevel(serverPlayer.dimension);

        if (bedPosition != null) {
            final Pos respawnPosition = Player.checkBedValidRespawnPosition(this.server.getLevel(serverPlayer.dimension), bedPosition);
            if (respawnPosition != null) {
                player.moveTo(respawnPosition.x + 0.5f, respawnPosition.y + 0.1f, respawnPosition.z + 0.5f, 0.0f, 0.0f);
                player.setRespawnPosition(bedPosition);
            }
            else {
                player.connection.send(new GameEventPacket(GameEventPacket.NO_RESPAWN_BED_AVAILABLE));
            }
        }

        // Ensure the area the player is spawning in is loaded!
        level.cache.create((int)player.x >> 4, (int)player.z >> 4);
        while (!level.getCubes(player, player.bb).isEmpty()) {
            player.setPos(player.x, player.y + 1.0, player.z);
        }

        player.connection.send(new RespawnPacket((byte)player.dimension));
        player.connection.teleport(player.x, player.y, player.z, player.yRot, player.xRot);

        this.sendLevelInfo(player, level);

        this.getChunkMap(player.dimension).add(player);
        level.addEntity(player);
        this.players.add(player);

        player.initMenu();

        player.animateRespawn();
        return player;
    }
    
    public void toggleDimension(final ServerPlayer player) {
        final ServerLevel oldLevel = this.server.getLevel(player.dimension);

        int targetDimension;
        if (player.dimension == -1) targetDimension = 0;
        else targetDimension = -1;

        player.dimension = targetDimension;
        final ServerLevel newLevel = this.server.getLevel(player.dimension);
        player.connection.send(new RespawnPacket((byte)player.dimension));

        oldLevel.removeEntityImmediately(player);
        player.removed = false;

        double xt = player.x;
        double zt = player.z;
        double scale = 8.0;
        if (player.dimension == -1) {
            xt /= scale;
            zt /= scale;
            player.moveTo(xt, player.y, zt, player.yRot, player.xRot);
            if (player.isAlive()) {
                oldLevel.tick(player, false);
            }
        }
        else {
            xt *= scale;
            zt *= scale;
            player.moveTo(xt, player.y, zt, player.yRot, player.xRot);
            if (player.isAlive()) {
                oldLevel.tick(player, false);
            }
        }

        if (player.isAlive()) {
            newLevel.addEntity(player);
            player.moveTo(xt, player.y, zt, player.yRot, player.xRot);
            newLevel.tick(player, false);
            newLevel.cache.autoCreate = true;
            new PortalForcer().force(newLevel, player);
            newLevel.cache.autoCreate = false;
        }

        this.changeDimension(player);
        player.connection.teleport(player.x, player.y, player.z, player.yRot, player.xRot);
        player.setLevel(newLevel);

        this.sendLevelInfo(player, newLevel);
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
            ServerPlayer player = this.players.get(i);
            player.connection.send(packet);
        }
    }
    
    public void broadcastAll(final Packet packet, final int dimension) {
        for (int i = 0; i < this.players.size(); ++i) {
            final ServerPlayer player = this.players.get(i);
            if (player.dimension == dimension) player.connection.send(packet);
        }
    }
    
    public String getPlayerNames() {
        String msg = "";
        for (int i = 0; i < this.players.size(); ++i) {
            if (i > 0) msg += ", ";
            msg += this.players.get(i).name;
        }
        return msg;
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
            final BufferedReader br = new BufferedReader(new FileReader(this.banFile));
            String line;
            while ((line = br.readLine()) != null) {
                this.bans.add(line.trim().toLowerCase());
            }
            br.close();
        }
        catch (final Exception e) {
            PlayerList.logger.warning("Failed to load ban list: " + e);
        }
    }
    
    private void saveBans() {
        try {
            final PrintWriter pw = new PrintWriter(new FileWriter(this.banFile, false));
            for (String ban : this.bans) {
                pw.println(ban);
            }
            pw.close();
        }
        catch (final Exception e) {
            PlayerList.logger.warning("Failed to save ban list: " + e);
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
            final BufferedReader br = new BufferedReader(new FileReader(this.ipBanFile));
            String line;
            while ((line = br.readLine()) != null) {
                this.ipBans.add(line.trim().toLowerCase());
            }
            br.close();
        }
        catch (final Exception e) {
            PlayerList.logger.warning("Failed to load ip ban list: " + e);
        }
    }
    
    private void saveIpBans() {
        try {
            final PrintWriter pw = new PrintWriter(new FileWriter(this.ipBanFile, false));
            for (String ipBan : this.ipBans) {
                pw.println(ipBan);
            }
            pw.close();
        }
        catch (final Exception e) {
            PlayerList.logger.warning("Failed to save ip ban list: " + e);
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
            final BufferedReader br = new BufferedReader(new FileReader(this.opFile));
            String line;
            while ((line = br.readLine()) != null) {
                this.ops.add(line.trim().toLowerCase());
            }
            br.close();
        }
        catch (final Exception e) {
            PlayerList.logger.warning("Failed to load ip ban list: " + e);
        }
    }
    
    private void saveOps() {
        try {
            final PrintWriter pw = new PrintWriter(new FileWriter(this.opFile, false));
            for (String op : this.ops) {
                pw.println(op);
            }
            pw.close();
        }
        catch (final Exception e) {
            PlayerList.logger.warning("Failed to save ip ban list: " + e);
        }
    }
    
    private void loadWhitelist() {
        try {
            this.whitelist.clear();
            final BufferedReader br = new BufferedReader(new FileReader(this.whiteListFile));
            String line;
            while ((line = br.readLine()) != null) {
                this.whitelist.add(line.trim().toLowerCase());
            }
            br.close();
        }
        catch (final Exception e) {
            PlayerList.logger.warning("Failed to load white-list: " + e);
        }
    }
    
    private void saveWhitelist() {
        try {
            final PrintWriter pw = new PrintWriter(new FileWriter(this.whiteListFile, false));
            for (String string : this.whitelist) {
                pw.println(string);
            }
            pw.close();
        }
        catch (final Exception e) {
            PlayerList.logger.warning("Failed to save white-list: " + e);
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
            final ServerPlayer p = this.players.get(i);
            if (p.name.equalsIgnoreCase(name)) {
                return p;
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
            final ServerPlayer p = this.players.get(i);
            if (p == except) continue;
            if (p.dimension != dimension) continue;

            final double xd = x - p.x;
            final double yd = y - p.y;
            final double zd = z - p.z;
            if (xd * xd + yd * yd + zd * zd < range * range) {
                p.connection.send(packet);
            }
        }
    }
    
    public void broadcastToAllOps(final String message) {
        final ChatPacket chatPacket = new ChatPacket(message);
        for (int i = 0; i < this.players.size(); ++i) {
            final ServerPlayer p = this.players.get(i);
            if (this.isOp(p.name)) {
                p.connection.send(chatPacket);
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
            this.playerIo.save(this.players.get(i));
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

}
