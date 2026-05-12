// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server;

public class MinecraftServer_1 extends Thread
{
    final /* synthetic */ MinecraftServer a;
    
    public MinecraftServer_1(final MinecraftServer minecraftServer) {
        this.a = minecraftServer;
        this.setDaemon(true);
        this.start();
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    Thread.sleep(2147483647L);
                }
            }
            catch (final InterruptedException ex) {
                continue;
            }
            break;
        }
    }
}
