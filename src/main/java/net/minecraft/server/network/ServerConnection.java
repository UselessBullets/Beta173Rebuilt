// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.network;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.net.InetAddress;
import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.util.logging.Logger;

public class ServerConnection
{
    public static Logger logger = Logger.getLogger("Minecraft");
    private ServerSocket serverSocket;
    private Thread listenThread;
    public volatile boolean running = false;
    private int connectionCounter = 0;
    private ArrayList<PendingConnection> pending = new ArrayList<>();
    private ArrayList<PlayerConnection> players = new ArrayList<>();
    public MinecraftServer server;
    
    public ServerConnection(final MinecraftServer server, final InetAddress address, final int port) throws IOException {
        this.server = server;
        this.serverSocket = new ServerSocket(port, 0, address);
        this.serverSocket.setPerformancePreferences(0, 2, 1);
        this.running = true;
        (this.listenThread = new Thread(() -> {
            final HashMap<InetAddress, Long> connections = new HashMap<>();
            while (this.running) {
                try {
                    final Socket accept = this.serverSocket.accept();
                    if (accept == null) continue;
                    final InetAddress inetAddress = accept.getInetAddress();
                    if (connections.containsKey(inetAddress) && !"127.0.0.1".equals(inetAddress.getHostAddress()) && System.currentTimeMillis() - connections.get(inetAddress) < 5000L) {
                        connections.put(inetAddress, System.currentTimeMillis());
                        accept.close();
                    }
                    else {
                        connections.put(inetAddress, System.currentTimeMillis());
                        PendingConnection unconnectedClient = new PendingConnection(server, accept, "Connection #" + this.connectionCounter++);
                        handleConnection(unconnectedClient);
                    }
                }
                catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }
    
    public void addPlayerConnection(final PlayerConnection uc) {
        this.players.add(uc);
    }
    
    private void handleConnection(final PendingConnection uc) {
        if (uc == null) throw new IllegalArgumentException("Got null pendingconnection!");
        this.pending.add(uc);
    }
    
    public void tick() {
        for (int i = 0; i < this.pending.size(); ++i) {
            final PendingConnection uc = this.pending.get(i);
            try {
                uc.tick();
            }
            catch (final Exception e) {
                uc.disconnect("Internal server error");
                ServerConnection.logger.log(Level.WARNING, "Failed to handle packet: " + e, e);
            }
            if (uc.done) this.pending.remove(i--);
            uc.connection.flush();
        }

        for (int i = 0; i < this.players.size(); ++i) {
            final PlayerConnection player = this.players.get(i);
            try {
                player.tick();
            }
            catch (final Exception e) {
                ServerConnection.logger.log(Level.WARNING, "Failed to handle packet: " + e, e);
                player.disconnect("Internal server error");
            }
            if (player.done) this.players.remove(i--);
            player.connection.flush();
        }
    }

}
