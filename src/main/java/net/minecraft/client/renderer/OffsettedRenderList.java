// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.MemoryTracker;
import java.nio.IntBuffer;

public class OffsettedRenderList
{
    private int x;
    private int y;
    private int z;
    private float xOff;
    private float yOff;
    private float zOff;
    private IntBuffer lists;
    private boolean inited;
    private boolean rendered;
    
    public OffsettedRenderList() {
        this.lists = MemoryTracker.createIntBuffer(65536);
        this.inited = false;
        this.rendered = false;
    }
    
    public void init(final int x, final int y, final int z, final double xOff, final double yOff, final double zOff) {
        this.inited = true;
        this.lists.clear();
        this.x = x;
        this.y = y;
        this.z = z;
        this.xOff = (float)xOff;
        this.yOff = (float)yOff;
        this.zOff = (float)zOff;
    }
    
    public boolean isAt(final int x, final int y, final int z) {
        return this.inited && x == this.x && y == this.y && z == this.z;
    }
    
    public void add(final int list) {
        this.lists.put(list);
        if (this.lists.remaining() == 0) {
            this.render();
        }
    }
    
    public void render() {
        if (!this.inited) {
            return;
        }
        if (!this.rendered) {
            this.lists.flip();
            this.rendered = true;
        }
        if (this.lists.remaining() > 0) {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.x - this.xOff, this.y - this.yOff, this.z - this.zOff);
            GL11.glCallLists(this.lists);
            GL11.glPopMatrix();
        }
    }
    
    public void clear() {
        this.inited = false;
        this.rendered = false;
    }
}
