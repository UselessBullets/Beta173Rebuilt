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
    private Component parent;
    private Cursor invisibleCursor;
    public int xd;
    public int yd;
    private int toSkip;
    
    public MouseHandler(final Component parent) {
        this.toSkip = 10;
        this.parent = parent;
        final IntBuffer intBuffer = MemoryTracker.createIntBuffer(1);
        intBuffer.put(0);
        intBuffer.flip();
        final IntBuffer intBuffer2 = MemoryTracker.createIntBuffer(1024);
        try {
            this.invisibleCursor = new Cursor(32, 32, 16, 16, 1, intBuffer2, intBuffer);
        }
        catch (final LWJGLException ex) {
            ex.printStackTrace();
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
