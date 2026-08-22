// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client;

import org.lwjgl.input.Mouse;
import java.nio.IntBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import java.awt.Component;

public class MouseHandler
{
    public static final boolean USE_LWJGL_MOUSE = true;
    private Component parent;
    private Cursor invisibleCursor;
    public int xd;
    public int yd;
    private int toSkip = 10;
    
    public MouseHandler(final Component parent) {
        this.parent = parent;
        final IntBuffer delays = MemoryTracker.createIntBuffer(1);
        delays.put(0);
        delays.flip();
        final IntBuffer images = MemoryTracker.createIntBuffer(1024);

        try {
            this.invisibleCursor = new Cursor(32, 32, 16, 16, 1, images, delays);
        }
        catch (final LWJGLException e) {
            e.printStackTrace();
        }
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
