// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.awt.Canvas;

public class MinecraftApplet_Canvas extends Canvas
{
    final /* synthetic */ MinecraftApplet mcApp;
    
    public MinecraftApplet_Canvas(final MinecraftApplet mcApp) {
        this.mcApp = mcApp;
    }
    
    @Override
    public synchronized void addNotify() {
        super.addNotify();
        this.mcApp.startGameThread();
    }
    
    @Override
    public synchronized void removeNotify() {
        this.mcApp.stopGameThread();
        super.removeNotify();
    }
}
