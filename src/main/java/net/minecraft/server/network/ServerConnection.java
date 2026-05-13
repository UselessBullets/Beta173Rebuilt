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
    public static Logger logger;
    private ServerSocket serverSocket;
    private Thread listenThread;
    public volatile boolean running;
    private int connectionCounter;
    private ArrayList<PendingConnection> pending;
    private ArrayList<PlayerConnection> players;
    public MinecraftServer server;
    
    public ServerConnection(final MinecraftServer server, final InetAddress address, final int port) throws IOException {
        this.running = false;
        this.connectionCounter = 0;
        this.pending = new ArrayList<>();
        this.players = new ArrayList<>();
        this.server = server;
        (this.serverSocket = new ServerSocket(port, 0, address)).setPerformancePreferences(0, 2, 1);
        this.running = true;
        (this.listenThread = new Thread(() -> {
            final HashMap<InetAddress, Long> hashMap = new HashMap<>();
            while (running) {
                try {
                    final Socket accept = serverSocket.accept();
                    if (accept == null) {
                        continue;
                    }
                    final InetAddress inetAddress = accept.getInetAddress();
                    if (hashMap.containsKey(inetAddress) && !"127.0.0.1".equals(inetAddress.getHostAddress()) && System.currentTimeMillis() - hashMap.get(inetAddress) < 5000L) {
                        hashMap.put(inetAddress, System.currentTimeMillis());
                        accept.close();
                    }
                    else {
                        hashMap.put(inetAddress, System.currentTimeMillis());
                        handleConnection(new PendingConnection(server, accept, "Connection #" + connectionCounter++));
                    }
                }
                catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        })).start();
    }
    
    public void addPlayerConnection(final PlayerConnection uc) {
        this.players.add(uc);
    }
    
    private void handleConnection(final PendingConnection uc) {
        if (uc == null) {
            throw new IllegalArgumentException("Got null pendingconnection!");
        }
        this.pending.add(uc);
    }
    
    public void tick() {
        for (int i = 0; i < this.pending.size(); ++i) {
            final PendingConnection pendingConnection = this.pending.get(i);
            try {
                pendingConnection.tick();
            }
            catch (final Exception ex) {
                pendingConnection.disconnect("Internal server error");
                ServerConnection.logger.log(Level.WARNING, "Failed to handle packet: " + ex, ex);
            }
            if (pendingConnection.done) {
                this.pending.remove(i--);
            }
            pendingConnection.connection.flush();
        }
        for (int j = 0; j < this.players.size(); ++j) {
            final PlayerConnection playerConnection = this.players.get(j);
            try {
                playerConnection.tick();
            }
            catch (final Exception ex2) {
                ServerConnection.logger.log(Level.WARNING, "Failed to handle packet: " + ex2, ex2);
                playerConnection.disconnect("Internal server error");
            }
            if (playerConnection.done) {
                this.players.remove(j--);
            }
            playerConnection.connection.flush();
        }
    }
    
    static {
        ServerConnection.logger = Logger.getLogger("Minecraft");
    }
}
