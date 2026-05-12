// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

public class Minecraft_TimerHackThread extends Thread
{
    final /* synthetic */ Minecraft minecraft;
    
    public Minecraft_TimerHackThread(final Minecraft minecraft, final String name) {
        this.minecraft = minecraft;
        super(name);
        this.setDaemon(true);
        this.start();
    }
    
    @Override
    public void run() {
        while (this.minecraft.running) {
            try {
                Thread.sleep(2147483647L);
            }
            catch (final InterruptedException ex) {}
        }
    }
}
