// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

public final class MinecraftServer_ServerThread extends Thread
{
    final /* synthetic */ MinecraftServer server;
    
    public MinecraftServer_ServerThread(final String name, final MinecraftServer server) {
        this.server = server;
        super(name);
    }
    
    @Override
    public void run() {
        this.server.run();
    }
}
