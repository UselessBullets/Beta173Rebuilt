// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Frame;

public final class Minecraft_Minecraft extends Minecraft
{
    final /* synthetic */ Frame frame;
    
    public Minecraft_Minecraft(final Component component, final Canvas parent, final MinecraftApplet minecraftApplet, final int width, final int height, final boolean fullscreen, final Frame frame) {
        this.frame = frame;
        super(component, parent, minecraftApplet, width, height, fullscreen);
    }
    
    @Override
    public void onCrash(final CrashReport crashReport) {
        this.frame.removeAll();
        this.frame.add(new CrashInfoPanel(crashReport), "Center");
        this.frame.validate();
    }
}
