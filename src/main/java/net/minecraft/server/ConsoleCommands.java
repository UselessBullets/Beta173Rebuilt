// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.network.packet.ChatPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerPlayer;

import java.util.logging.Logger;

// Useless - Class does not have great internal information, method variable names and structure are guesses
public class ConsoleCommands
{
    private static Logger logger = Logger.getLogger("Minecraft");
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
            if (players != null) players.saveAll();
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
            final String username = msg.substring(msg.indexOf(" ")).trim();
            players.op(username);
            this.commandResponse(consoleName, "Opping " + username);
            players.sendMessage(username, "§eYou are now op!");
        }
        else if (msg.toLowerCase().startsWith("deop ")) {
            final String username = msg.substring(msg.indexOf(" ")).trim();
            players.deop(username);
            players.sendMessage(username, "§eYou are no longer op!");
            this.commandResponse(consoleName, "De-opping " + username);
        }
        else if (msg.toLowerCase().startsWith("ban-ip ")) {
            final String ip = msg.substring(msg.indexOf(" ")).trim();
            players.ipBan(ip);
            this.commandResponse(consoleName, "Banning ip " + ip);
        }
        else if (msg.toLowerCase().startsWith("pardon-ip ")) {
            final String ip = msg.substring(msg.indexOf(" ")).trim();
            players.ipParden(ip);
            this.commandResponse(consoleName, "Pardoning ip " + ip);
        }
        else if (msg.toLowerCase().startsWith("ban ")) {
            final String username = msg.substring(msg.indexOf(" ")).trim();
            players.ban(username);
            this.commandResponse(consoleName, "Banning " + username);
            final ServerPlayer player = players.getPlayer(username);
            if (player != null) {
                player.connection.disconnect("Banned by admin");
            }
        }
        else if (msg.toLowerCase().startsWith("pardon ")) {
            final String username = msg.substring(msg.indexOf(" ")).trim();
            players.pardon(username);
            this.commandResponse(consoleName, "Pardoning " + username);
        }
        else if (msg.toLowerCase().startsWith("kick ")) {
            final String username = msg.substring(msg.indexOf(" ")).trim();
            ServerPlayer player = null;
            for (int i = 0; i < players.players.size(); ++i) {
                final ServerPlayer p = players.players.get(i);
                if (p.name.equalsIgnoreCase(username)) {
                    player = p;
                }
            }

            if (player != null) {
                player.connection.disconnect("Kicked by admin");
                this.commandResponse(consoleName, "Kicking " + player.name);
            }
            else {
                source.info("Can't find user " + username + ". No kick.");
            }
        }
        else if (msg.toLowerCase().startsWith("tp ")) {
            final String[] parts = msg.split(" ");
            if (parts.length == 3) {
                final ServerPlayer from = players.getPlayer(parts[1]);
                final ServerPlayer to = players.getPlayer(parts[2]);
                if (from == null) {
                    source.info("Can't find user " + parts[1] + ". No tp.");
                }
                else if (to == null) {
                    source.info("Can't find user " + parts[2] + ". No tp.");
                }
                else if (from.dimension != to.dimension) {
                    source.info("User " + parts[1] + " and " + parts[2] + " are in different dimensions. No tp.");
                }
                else {
                    from.connection.teleport(to.x, to.y, to.z, to.yRot, to.xRot);
                    this.commandResponse(consoleName, "Teleporting " + parts[1] + " to " + parts[2] + ".");
                }
            }
            else {
                source.info("Syntax error, please provice a source and a target.");
            }
        }
        else if (msg.toLowerCase().startsWith("give ")) {
            final String[] parts = msg.split(" ");
            if (parts.length != 3 && parts.length != 4) return;

            final String username = parts[1];
            final ServerPlayer player = players.getPlayer(username);
            if (player != null) {
                try {
                    final int id = Integer.parseInt(parts[2]);
                    if (Item.items[id] != null) {
                        this.commandResponse(consoleName, "Giving " + player.name + " some " + id);

                        int count = 1;
                        if (parts.length > 3) count = this.parseInt(parts[3], 1);
                        if (count < 1) count = 1;
                        if (count > Inventory.MAX_INVENTORY_STACK_SIZE) count = Inventory.MAX_INVENTORY_STACK_SIZE;

                        player.drop(new ItemInstance(id, count, 0));
                    }
                    else {
                        source.info("There's no item with id " + id);
                    }
                }
                catch (final NumberFormatException e) {
                    source.info("There's no item with id " + parts[2]);
                }
            }
            else {
                source.info("Can't find user " + username);
            }
        }
        else if (msg.toLowerCase().startsWith("time ")) {
            final String[] parts = msg.split(" ");
            if (parts.length != 3) return;
            final String subCommand = parts[1];
            try {
                final int timeValue = Integer.parseInt(parts[2]);
                if ("add".equalsIgnoreCase(subCommand)) {
                    for (int i = 0; i < this.server.levels.length; ++i) {
                        final ServerLevel level = this.server.levels[i];
                        level.setTimeAndAdjustTileTicks(level.getTime() + timeValue);
                    }
                    this.commandResponse(consoleName, "Added " + timeValue + " to time");
                }
                else if ("set".equalsIgnoreCase(subCommand)) {
                    for (int i = 0; i < this.server.levels.length; ++i) {
                        this.server.levels[i].setTimeAndAdjustTileTicks(timeValue);
                    }
                    this.commandResponse(consoleName, "Set time to " + timeValue);
                }
                else {
                    source.info("Unknown method, use either \"add\" or \"set\"");
                }
            }
            catch (final NumberFormatException e) {
                source.info("Unable to convert time value, " + parts[2]);
            }
        }
        else if (msg.toLowerCase().startsWith("say ")) {
            final String message = msg.substring(msg.indexOf(" ")).trim();
            ConsoleCommands.logger.info("[" + consoleName + "] " + message);
            players.broadcastAll(new ChatPacket("§d[Server] " + message));
        }
        else if (msg.toLowerCase().startsWith("tell ")) {
            final String[] parts = msg.split(" ");
            if (parts.length >= 3) {
                String message = msg.substring(msg.indexOf(" ")).trim();
                message = message.substring(message.indexOf(" ")).trim();

                ConsoleCommands.logger.info("[" + consoleName + "->" + parts[1] + "] " + message);
                final String opMessage = "§7" + consoleName + " whispers " + message;
                ConsoleCommands.logger.info(opMessage);
                if (!players.sendTo(parts[1], new ChatPacket(opMessage))) {
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
        final String[] parts = command.split(" ");
        if (parts.length < 2) return;

        final String subCommand = parts[1].toLowerCase();
        if ("on".equals(subCommand)) {
            this.commandResponse(name, "Turned on white-listing");
            this.server.settings.setBooleanAndSave("white-list", true);
        }
        else if ("off".equals(subCommand)) {
            this.commandResponse(name, "Turned off white-listing");
            this.server.settings.setBooleanAndSave("white-list", false);
        }
        else if ("list".equals(subCommand)) {
            final Set<String> whiteList = this.server.players.getWhiteList();
            String message = "";
            for (String o : whiteList) {
                message = message + o + " ";
            }
            source.info("White-listed players: " + message);
        }
        else if ("add".equals(subCommand) && parts.length == 3) {
            final String username = parts[2].toLowerCase();
            this.server.players.whitelist(username);
            this.commandResponse(name, "Added " + username + " to white-list");
        }
        else if ("remove".equals(subCommand) && parts.length == 3) {
            final String username = parts[2].toLowerCase();
            this.server.players.blackList(username);
            this.commandResponse(name, "Removed " + username + " from white-list");
        }
        else if ("reload".equals(subCommand)) {
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
        final String msg = name + ": " + message;
        this.server.players.broadcastToAllOps("§7(" + msg + ")");
        ConsoleCommands.logger.info(msg);
    }
    
    private int parseInt(final String str, final int def) {
        try {
            return Integer.parseInt(str);
        }
        catch (final NumberFormatException e) {
            return def;
        }
    }

}
