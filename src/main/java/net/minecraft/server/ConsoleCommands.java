// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import java.util.Iterator;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.network.packet.ChatPacket;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerPlayer;

import java.util.logging.Logger;

public class ConsoleCommands
{
    private static Logger logger;
    private MinecraftServer server;
    
    public ConsoleCommands(final MinecraftServer server) {
        this.server = server;
    }
    
    public void handleCommand(final ConsoleInput input) {
        final String msg = input.msg;
        final ConsoleInputSource source = input.source;
        final String consoleName = source.getConsoleName();
        final PlayerList players = this.server.players;
        if (msg.toLowerCase().startsWith("help") || msg.toLowerCase().startsWith("?")) {
            this.displayHelp(source);
        }
        else if (msg.toLowerCase().startsWith("list")) {
            source.info("Connected players: " + players.getPlayerNames());
        }
        else if (msg.toLowerCase().startsWith("stop")) {
            this.commandResponse(consoleName, "Stopping the server..");
            this.server.halt();
        }
        else if (msg.toLowerCase().startsWith("save-all")) {
            this.commandResponse(consoleName, "Forcing save..");
            if (players != null) {
                players.saveAll();
            }
            for (int i = 0; i < this.server.levels.length; ++i) {
                this.server.levels[i].save(true, null);
            }
            this.commandResponse(consoleName, "Save complete.");
        }
        else if (msg.toLowerCase().startsWith("save-off")) {
            this.commandResponse(consoleName, "Disabling level saving..");
            for (int j = 0; j < this.server.levels.length; ++j) {
                this.server.levels[j].noSave = true;
            }
        }
        else if (msg.toLowerCase().startsWith("save-on")) {
            this.commandResponse(consoleName, "Enabling level saving..");
            for (int k = 0; k < this.server.levels.length; ++k) {
                this.server.levels[k].noSave = false;
            }
        }
        else if (msg.toLowerCase().startsWith("op ")) {
            final String trim = msg.substring(msg.indexOf(" ")).trim();
            players.op(trim);
            this.commandResponse(consoleName, "Opping " + trim);
            players.sendMessage(trim, "§eYou are now op!");
        }
        else if (msg.toLowerCase().startsWith("deop ")) {
            final String trim2 = msg.substring(msg.indexOf(" ")).trim();
            players.deop(trim2);
            players.sendMessage(trim2, "§eYou are no longer op!");
            this.commandResponse(consoleName, "De-opping " + trim2);
        }
        else if (msg.toLowerCase().startsWith("ban-ip ")) {
            final String trim3 = msg.substring(msg.indexOf(" ")).trim();
            players.ipBan(trim3);
            this.commandResponse(consoleName, "Banning ip " + trim3);
        }
        else if (msg.toLowerCase().startsWith("pardon-ip ")) {
            final String trim4 = msg.substring(msg.indexOf(" ")).trim();
            players.ipParden(trim4);
            this.commandResponse(consoleName, "Pardoning ip " + trim4);
        }
        else if (msg.toLowerCase().startsWith("ban ")) {
            final String trim5 = msg.substring(msg.indexOf(" ")).trim();
            players.ban(trim5);
            this.commandResponse(consoleName, "Banning " + trim5);
            final ServerPlayer player = players.getPlayer(trim5);
            if (player != null) {
                player.connection.disconnect("Banned by admin");
            }
        }
        else if (msg.toLowerCase().startsWith("pardon ")) {
            final String trim6 = msg.substring(msg.indexOf(" ")).trim();
            players.pardon(trim6);
            this.commandResponse(consoleName, "Pardoning " + trim6);
        }
        else if (msg.toLowerCase().startsWith("kick ")) {
            final String trim7 = msg.substring(msg.indexOf(" ")).trim();
            ServerPlayer serverPlayer = null;
            for (int l = 0; l < players.players.size(); ++l) {
                final ServerPlayer serverPlayer2 = players.players.get(l);
                if (serverPlayer2.name.equalsIgnoreCase(trim7)) {
                    serverPlayer = serverPlayer2;
                }
            }
            if (serverPlayer != null) {
                serverPlayer.connection.disconnect("Kicked by admin");
                this.commandResponse(consoleName, "Kicking " + serverPlayer.name);
            }
            else {
                source.info("Can't find user " + trim7 + ". No kick.");
            }
        }
        else if (msg.toLowerCase().startsWith("tp ")) {
            final String[] split = msg.split(" ");
            if (split.length == 3) {
                final ServerPlayer player2 = players.getPlayer(split[1]);
                final ServerPlayer player3 = players.getPlayer(split[2]);
                if (player2 == null) {
                    source.info("Can't find user " + split[1] + ". No tp.");
                }
                else if (player3 == null) {
                    source.info("Can't find user " + split[2] + ". No tp.");
                }
                else if (player2.dimension != player3.dimension) {
                    source.info("User " + split[1] + " and " + split[2] + " are in different dimensions. No tp.");
                }
                else {
                    player2.connection.teleport(player3.x, player3.y, player3.z, player3.yRot, player3.xRot);
                    this.commandResponse(consoleName, "Teleporting " + split[1] + " to " + split[2] + ".");
                }
            }
            else {
                source.info("Syntax error, please provice a source and a target.");
            }
        }
        else if (msg.toLowerCase().startsWith("give ")) {
            final String[] split2 = msg.split(" ");
            if (split2.length != 3 && split2.length != 4) {
                return;
            }
            final String s = split2[1];
            final ServerPlayer player4 = players.getPlayer(s);
            if (player4 != null) {
                try {
                    final int int1 = Integer.parseInt(split2[2]);
                    if (Item.items[int1] != null) {
                        this.commandResponse(consoleName, "Giving " + player4.name + " some " + int1);
                        int int2 = 1;
                        if (split2.length > 3) {
                            int2 = this.parseInt(split2[3], 1);
                        }
                        if (int2 < 1) {
                            int2 = 1;
                        }
                        if (int2 > 64) {
                            int2 = 64;
                        }
                        player4.drop(new ItemInstance(int1, int2, 0));
                    }
                    else {
                        source.info("There's no item with id " + int1);
                    }
                }
                catch (final NumberFormatException ex) {
                    source.info("There's no item with id " + split2[2]);
                }
            }
            else {
                source.info("Can't find user " + s);
            }
        }
        else if (msg.toLowerCase().startsWith("time ")) {
            final String[] split3 = msg.split(" ");
            if (split3.length != 3) {
                return;
            }
            final String s2 = split3[1];
            try {
                final int int3 = Integer.parseInt(split3[2]);
                if ("add".equalsIgnoreCase(s2)) {
                    for (int n = 0; n < this.server.levels.length; ++n) {
                        final ServerLevel serverLevel = this.server.levels[n];
                        serverLevel.setTimeAndAdjustTileTicks(serverLevel.getTime() + int3);
                    }
                    this.commandResponse(consoleName, "Added " + int3 + " to time");
                }
                else if ("set".equalsIgnoreCase(s2)) {
                    for (int n2 = 0; n2 < this.server.levels.length; ++n2) {
                        this.server.levels[n2].setTimeAndAdjustTileTicks(int3);
                    }
                    this.commandResponse(consoleName, "Set time to " + int3);
                }
                else {
                    source.info("Unknown method, use either \"add\" or \"set\"");
                }
            }
            catch (final NumberFormatException ex2) {
                source.info("Unable to convert time value, " + split3[2]);
            }
        }
        else if (msg.toLowerCase().startsWith("say ")) {
            final String trim8 = msg.substring(msg.indexOf(" ")).trim();
            ConsoleCommands.logger.info("[" + consoleName + "] " + trim8);
            players.broadcastAll(new ChatPacket("§d[Server] " + trim8));
        }
        else if (msg.toLowerCase().startsWith("tell ")) {
            final String[] split4 = msg.split(" ");
            if (split4.length >= 3) {
                final String trim9 = msg.substring(msg.indexOf(" ")).trim();
                final String trim10 = trim9.substring(trim9.indexOf(" ")).trim();
                ConsoleCommands.logger.info("[" + consoleName + "->" + split4[1] + "] " + trim10);
                final String string = "§7" + consoleName + " whispers " + trim10;
                ConsoleCommands.logger.info(string);
                if (!players.sendTo(split4[1], new ChatPacket(string))) {
                    source.info("There's no player by that name online.");
                }
            }
        }
        else if (msg.toLowerCase().startsWith("whitelist ")) {
            this.whitelistCommand(consoleName, msg, source);
        }
        else {
            ConsoleCommands.logger.info("Unknown console command. Type \"help\" for help.");
        }
    }
    
    private void whitelistCommand(final String name, final String command, final ConsoleInputSource source) {
        final String[] split = command.split(" ");
        if (split.length < 2) {
            return;
        }
        final String lowerCase = split[1].toLowerCase();
        if ("on".equals(lowerCase)) {
            this.commandResponse(name, "Turned on white-listing");
            this.server.settings.setBooleanAndSave("white-list", true);
        }
        else if ("off".equals(lowerCase)) {
            this.commandResponse(name, "Turned off white-listing");
            this.server.settings.setBooleanAndSave("white-list", false);
        }
        else if ("list".equals(lowerCase)) {
            final Set whiteList = this.server.players.getWhiteList();
            String string = "";
            final Iterator iterator = whiteList.iterator();
            while (iterator.hasNext()) {
                string = string + (String)iterator.next() + " ";
            }
            source.info("White-listed players: " + string);
        }
        else if ("add".equals(lowerCase) && split.length == 3) {
            final String lowerCase2 = split[2].toLowerCase();
            this.server.players.whitelist(lowerCase2);
            this.commandResponse(name, "Added " + lowerCase2 + " to white-list");
        }
        else if ("remove".equals(lowerCase) && split.length == 3) {
            final String lowerCase3 = split[2].toLowerCase();
            this.server.players.blackList(lowerCase3);
            this.commandResponse(name, "Removed " + lowerCase3 + " from white-list");
        }
        else if ("reload".equals(lowerCase)) {
            this.server.players.reloadWhitelist();
            this.commandResponse(name, "Reloaded white-list from file");
        }
    }
    
    private void displayHelp(final ConsoleInputSource source) {
        source.info("To run the server without a gui, start it like this:");
        source.info("   java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui");
        source.info("Console commands:");
        source.info("   help  or  ?               shows this message");
        source.info("   kick <player>             removes a player from the server");
        source.info("   ban <player>              bans a player from the server");
        source.info("   pardon <player>           pardons a banned player so that they can connect again");
        source.info("   ban-ip <ip>               bans an IP address from the server");
        source.info("   pardon-ip <ip>            pardons a banned IP address so that they can connect again");
        source.info("   op <player>               turns a player into an op");
        source.info("   deop <player>             removes op status from a player");
        source.info("   tp <player1> <player2>    moves one player to the same location as another player");
        source.info("   give <player> <id> [num]  gives a player a resource");
        source.info("   tell <player> <message>   sends a private message to a player");
        source.info("   stop                      gracefully stops the server");
        source.info("   save-all                  forces a server-wide level save");
        source.info("   save-off                  disables terrain saving (useful for backup scripts)");
        source.info("   save-on                   re-enables terrain saving");
        source.info("   list                      lists all currently connected players");
        source.info("   say <message>             broadcasts a message to all players");
        source.info("   time <add|set> <amount>   adds to or sets the world time (0-24000)");
    }
    
    private void commandResponse(final String name, final String message) {
        final String string = name + ": " + message;
        this.server.players.broadcastToAllOps("§7(" + string + ")");
        ConsoleCommands.logger.info(string);
    }
    
    private int parseInt(final String str, final int def) {
        try {
            return Integer.parseInt(str);
        }
        catch (final NumberFormatException ex) {
            return def;
        }
    }
    
    static {
        ConsoleCommands.logger = Logger.getLogger("Minecraft");
    }
}
