// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public final class Minecraft_WindowAdapter extends WindowAdapter
{
    final /* synthetic */ Minecraft mc;
    final /* synthetic */ Thread thread;
    
    public Minecraft_WindowAdapter(final Minecraft mc, final Thread thread) {
        this.mc = mc;
        this.thread = thread;
    }
    
    @Override
    public void windowClosing(final WindowEvent windowEvent) {
        this.mc.stop();
        try {
            this.thread.join();
        }
        catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
}
