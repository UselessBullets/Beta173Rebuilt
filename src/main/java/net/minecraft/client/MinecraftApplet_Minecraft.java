// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;

public class MinecraftApplet_Minecraft extends Minecraft
{
    final /* synthetic */ MinecraftApplet mcApp;
    
    public MinecraftApplet_Minecraft(final MinecraftApplet mcApp, final Component component, final Canvas parent, final MinecraftApplet minecraftApplet, final int width, final int height, final boolean fullscreen) {
        this.mcApp = mcApp;
        super(component, parent, minecraftApplet, width, height, fullscreen);
    }
    
    @Override
    public void onCrash(final CrashReport crashReport) {
        this.mcApp.removeAll();
        this.mcApp.setLayout(new BorderLayout());
        this.mcApp.add(new CrashInfoPanel(crashReport), "Center");
        this.mcApp.validate();
    }
}
