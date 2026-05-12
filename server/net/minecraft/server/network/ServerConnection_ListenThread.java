// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.network;

import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.util.HashMap;
import net.minecraft.server.MinecraftServer;

class ServerConnection_ListenThread extends Thread
{
    final /* synthetic */ MinecraftServer a;
    final /* synthetic */ ServerConnection b;
    
    ServerConnection_ListenThread(final ServerConnection cu, final String string, final MinecraftServer minecraftServer) {
        this.b = cu;
        this.a = minecraftServer;
        super(string);
    }
    
    @Override
    public void run() {
        final HashMap hashMap = new HashMap();
        while (this.b.running) {
            try {
                final Socket accept = this.b.serverSocket.accept();
                if (accept == null) {
                    continue;
                }
                final InetAddress inetAddress = accept.getInetAddress();
                if (hashMap.containsKey(inetAddress) && !"127.0.0.1".equals(inetAddress.getHostAddress()) && System.currentTimeMillis() - (long)hashMap.get(inetAddress) < 5000L) {
                    hashMap.put(inetAddress, System.currentTimeMillis());
                    accept.close();
                }
                else {
                    hashMap.put(inetAddress, System.currentTimeMillis());
                    this.b.handleConnection(new PendingConnection(this.a, accept, "Connection #" + this.b.connectionCounter++));
                }
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
