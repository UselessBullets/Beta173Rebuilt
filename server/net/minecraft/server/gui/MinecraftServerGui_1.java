// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.server.gui;

import java.awt.event.WindowEvent;
import net.minecraft.server.MinecraftServer;
import java.awt.event.WindowAdapter;

final class MinecraftServerGui_1 extends WindowAdapter
{
    final /* synthetic */ MinecraftServer a;
    
    MinecraftServerGui_1(final MinecraftServer minecraftServer) {
        this.a = minecraftServer;
    }
    
    @Override
    public void windowClosing(final WindowEvent windowEvent) {
        this.a.halt();
        while (!this.a.stopped) {
            try {
                Thread.sleep(100L);
            }
            catch (final InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        System.exit(0);
    }
}
