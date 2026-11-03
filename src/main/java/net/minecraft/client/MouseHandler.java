// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import org.lwjgl.input.Mouse;
import org.useless.SDL3Window;

public class MouseHandler
{
    private SDL3Window parent;
    public int xd;
    public int yd;
    public MouseHandler(final SDL3Window parent) {
        this.parent = parent;
    }
    
    public void grab() {
        Mouse.setGrabbed(true);
        this.xd = 0;
        this.yd = 0;
    }
    
    public void release() {
        Mouse.setCursorPosition(this.parent.getWidth() / 2, this.parent.getHeight() / 2);
        Mouse.setGrabbed(false);
    }
    
    public void poll() {
        this.xd = Mouse.getDX();
        this.yd = Mouse.getDY();
    }
}
