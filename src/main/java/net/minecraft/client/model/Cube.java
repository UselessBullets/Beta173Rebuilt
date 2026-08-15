// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.MemoryTracker;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class Cube
{
    private Vertex[] vertices;
    private Polygon[] polygons;
    private int xTexOffs;
    private int yTexOffs;
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    private boolean compiled;
    private int list;
    public boolean mirror;
    public boolean visible;
    public boolean neverRender;
    
    public Cube(final int xTexOffs, final int yTexOffs) {
        this.compiled = false;
        this.list = 0;
        this.mirror = false;
        this.visible = true;
        this.neverRender = false;
        this.xTexOffs = xTexOffs;
        this.yTexOffs = yTexOffs;
    }
    
    public void addBox(final float x0, final float y0, final float z0, final int w, final int h, final int d) {
        this.addBox(x0, y0, z0, w, h, d, 0.0f);
    }
    
    public void addBox(float x0, float y0, float z0, final int w, final int h, final int d, final float g) {
        this.vertices = new Vertex[8];
        this.polygons = new Polygon[6];
        final float n = x0 + w;
        final float n2 = y0 + h;
        final float n3 = z0 + d;
        x0 -= g;
        y0 -= g;
        z0 -= g;
        float n4 = n + g;
        final float n5 = n2 + g;
        final float n6 = n3 + g;
        if (this.mirror) {
            final float n7 = n4;
            n4 = x0;
            x0 = n7;
        }
        final Vertex vertex = new Vertex(x0, y0, z0, 0.0f, 0.0f);
        final Vertex vertex2 = new Vertex(n4, y0, z0, 0.0f, 8.0f);
        final Vertex vertex3 = new Vertex(n4, n5, z0, 8.0f, 8.0f);
        final Vertex vertex4 = new Vertex(x0, n5, z0, 8.0f, 0.0f);
        final Vertex vertex5 = new Vertex(x0, y0, n6, 0.0f, 0.0f);
        final Vertex vertex6 = new Vertex(n4, y0, n6, 0.0f, 8.0f);
        final Vertex vertex7 = new Vertex(n4, n5, n6, 8.0f, 8.0f);
        final Vertex vertex8 = new Vertex(x0, n5, n6, 8.0f, 0.0f);
        this.vertices[0] = vertex;
        this.vertices[1] = vertex2;
        this.vertices[2] = vertex3;
        this.vertices[3] = vertex4;
        this.vertices[4] = vertex5;
        this.vertices[5] = vertex6;
        this.vertices[6] = vertex7;
        this.vertices[7] = vertex8;
        this.polygons[0] = new Polygon(new Vertex[] { vertex6, vertex2, vertex3, vertex7 }, this.xTexOffs + d + w, this.yTexOffs + d, this.xTexOffs + d + w + d, this.yTexOffs + d + h);
        this.polygons[1] = new Polygon(new Vertex[] { vertex, vertex5, vertex8, vertex4 }, this.xTexOffs + 0, this.yTexOffs + d, this.xTexOffs + d, this.yTexOffs + d + h);
        this.polygons[2] = new Polygon(new Vertex[] { vertex6, vertex5, vertex, vertex2 }, this.xTexOffs + d, this.yTexOffs + 0, this.xTexOffs + d + w, this.yTexOffs + d);
        this.polygons[3] = new Polygon(new Vertex[] { vertex3, vertex4, vertex8, vertex7 }, this.xTexOffs + d + w, this.yTexOffs + 0, this.xTexOffs + d + w + w, this.yTexOffs + d);
        this.polygons[4] = new Polygon(new Vertex[] { vertex2, vertex, vertex4, vertex3 }, this.xTexOffs + d, this.yTexOffs + d, this.xTexOffs + d + w, this.yTexOffs + d + h);
        this.polygons[5] = new Polygon(new Vertex[] { vertex5, vertex6, vertex7, vertex8 }, this.xTexOffs + d + w + d, this.yTexOffs + d, this.xTexOffs + d + w + d + w, this.yTexOffs + d + h);
        if (this.mirror) {
            for (int i = 0; i < this.polygons.length; ++i) {
                this.polygons[i].mirror();
            }
        }
    }
    
    public void setPos(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void render(final float scale) {
        if (this.neverRender) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(scale);
        }
        if (this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            glPushMatrix();
            glTranslatef(this.x * scale, this.y * scale, this.z * scale);
            if (this.zRot != 0.0f) {
                glRotatef(this.zRot * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            if (this.yRot != 0.0f) {
                glRotatef(this.yRot * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (this.xRot != 0.0f) {
                glRotatef(this.xRot * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
            glCallList(this.list);
            glPopMatrix();
        }
        else if (this.x != 0.0f || this.y != 0.0f || this.z != 0.0f) {
            glTranslatef(this.x * scale, this.y * scale, this.z * scale);
            glCallList(this.list);
            glTranslatef(-this.x * scale, -this.y * scale, -this.z * scale);
        }
        else {
            glCallList(this.list);
        }
    }
    
    public void render2(final float scale) {
        if (this.neverRender) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(scale);
        }
        glPushMatrix();
        glTranslatef(this.x * scale, this.y * scale, this.z * scale);
        if (this.yRot != 0.0f) {
            glRotatef(this.yRot * 57.295776f, 0.0f, 1.0f, 0.0f);
        }
        if (this.xRot != 0.0f) {
            glRotatef(this.xRot * 57.295776f, 1.0f, 0.0f, 0.0f);
        }
        if (this.zRot != 0.0f) {
            glRotatef(this.zRot * 57.295776f, 0.0f, 0.0f, 1.0f);
        }
        glCallList(this.list);
        glPopMatrix();
    }
    
    public void translateTo(final float scale) {
        if (this.neverRender) {
            return;
        }
        if (!this.visible) {
            return;
        }
        if (!this.compiled) {
            this.compile(scale);
        }
        if (this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            glTranslatef(this.x * scale, this.y * scale, this.z * scale);
            if (this.zRot != 0.0f) {
                glRotatef(this.zRot * 57.295776f, 0.0f, 0.0f, 1.0f);
            }
            if (this.yRot != 0.0f) {
                glRotatef(this.yRot * 57.295776f, 0.0f, 1.0f, 0.0f);
            }
            if (this.xRot != 0.0f) {
                glRotatef(this.xRot * 57.295776f, 1.0f, 0.0f, 0.0f);
            }
        }
        else if (this.x != 0.0f || this.y != 0.0f || this.z != 0.0f) {
            glTranslatef(this.x * scale, this.y * scale, this.z * scale);
        }
    }
    
    private void compile(final float scale) {
        glNewList(this.list = MemoryTracker.genLists(1), GL_COMPILE);
        final Tesselator instance = Tesselator.instance;
        for (int i = 0; i < this.polygons.length; ++i) {
            this.polygons[i].render(instance, scale);
        }
        glEndList();
        this.compiled = true;
    }
}
