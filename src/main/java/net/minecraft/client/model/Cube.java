// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.MemoryTracker;
import util.Mth;

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

        float x1 = x0 + w;
        float y1 = y0 + h;
        float z1 = z0 + d;

        x0 -= g;
        y0 -= g;
        z0 -= g;
        x1 += g;
        y1 += g;
        z1 += g;

        if (this.mirror) {
            final float tmp = x1;
            x1 = x0;
            x0 = tmp;
        }

        final Vertex u0 = new Vertex(x0, y0, z0, 0.0f, 0.0f);
        final Vertex u1 = new Vertex(x1, y0, z0, 0.0f, 8.0f);
        final Vertex u2 = new Vertex(x1, y1, z0, 8.0f, 8.0f);
        final Vertex u3 = new Vertex(x0, y1, z0, 8.0f, 0.0f);

        final Vertex l0 = new Vertex(x0, y0, z1, 0.0f, 0.0f);
        final Vertex l1 = new Vertex(x1, y0, z1, 0.0f, 8.0f);
        final Vertex l2 = new Vertex(x1, y1, z1, 8.0f, 8.0f);
        final Vertex l3 = new Vertex(x0, y1, z1, 8.0f, 0.0f);

        this.vertices[0] = u0;
        this.vertices[1] = u1;
        this.vertices[2] = u2;
        this.vertices[3] = u3;
        this.vertices[4] = l0;
        this.vertices[5] = l1;
        this.vertices[6] = l2;
        this.vertices[7] = l3;

        this.polygons[0] = new Polygon(new Vertex[] { l1, u1, u2, l2 }, this.xTexOffs + d + w, this.yTexOffs + d, this.xTexOffs + d + w + d, this.yTexOffs + d + h); // Right
        this.polygons[1] = new Polygon(new Vertex[] { u0, l0, l3, u3 }, this.xTexOffs + 0, this.yTexOffs + d, this.xTexOffs + d, this.yTexOffs + d + h); // Left
        this.polygons[2] = new Polygon(new Vertex[] { l1, l0, u0, u1 }, this.xTexOffs + d, this.yTexOffs + 0, this.xTexOffs + d + w, this.yTexOffs + d); // Up
        this.polygons[3] = new Polygon(new Vertex[] { u2, u3, l3, l2 }, this.xTexOffs + d + w, this.yTexOffs + 0, this.xTexOffs + d + w + w, this.yTexOffs + d); // Down
        this.polygons[4] = new Polygon(new Vertex[] { u1, u0, u3, u2 }, this.xTexOffs + d, this.yTexOffs + d, this.xTexOffs + d + w, this.yTexOffs + d + h); // Front
        this.polygons[5] = new Polygon(new Vertex[] { l0, l1, l2, l3 }, this.xTexOffs + d + w + d, this.yTexOffs + d, this.xTexOffs + d + w + d + w, this.yTexOffs + d + h); // Back

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
        if (this.neverRender || !this.visible) return;

        if (!this.compiled) this.compile(scale);

        if (this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            glPushMatrix();
            glTranslatef(this.x * scale, this.y * scale, this.z * scale);
            if (this.zRot != 0.0f) glRotatef(this.zRot * Mth.RADDEG, 0.0f, 0.0f, 1.0f);
            if (this.yRot != 0.0f) glRotatef(this.yRot * Mth.RADDEG, 0.0f, 1.0f, 0.0f);
            if (this.xRot != 0.0f) glRotatef(this.xRot * Mth.RADDEG, 1.0f, 0.0f, 0.0f);
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
    
    public void renderRollable(final float scale) {
        if (this.neverRender || !this.visible) return;

        if (!this.compiled) this.compile(scale);

        glPushMatrix();
        glTranslatef(this.x * scale, this.y * scale, this.z * scale);
        if (this.yRot != 0.0f) glRotatef(this.yRot * Mth.RADDEG, 0.0f, 1.0f, 0.0f);
        if (this.xRot != 0.0f) glRotatef(this.xRot * Mth.RADDEG, 1.0f, 0.0f, 0.0f);
        if (this.zRot != 0.0f) glRotatef(this.zRot * Mth.RADDEG, 0.0f, 0.0f, 1.0f);
        glCallList(this.list);
        glPopMatrix();
    }
    
    public void translateTo(final float scale) {
        if (this.neverRender || !this.visible) return;

        if (!this.compiled) this.compile(scale);

        if (this.xRot != 0.0f || this.yRot != 0.0f || this.zRot != 0.0f) {
            glTranslatef(this.x * scale, this.y * scale, this.z * scale);
            if (this.zRot != 0.0f) glRotatef(this.zRot * Mth.RADDEG, 0.0f, 0.0f, 1.0f);
            if (this.yRot != 0.0f) glRotatef(this.yRot * Mth.RADDEG, 0.0f, 1.0f, 0.0f);
            if (this.xRot != 0.0f) glRotatef(this.xRot * Mth.RADDEG, 1.0f, 0.0f, 0.0f);
        }
        else if (this.x != 0.0f || this.y != 0.0f || this.z != 0.0f) {
            glTranslatef(this.x * scale, this.y * scale, this.z * scale);
        }
    }
    
    private void compile(final float scale) {
        this.list = MemoryTracker.genLists(1);
        glNewList(this.list, GL_COMPILE);
        final Tesselator t = Tesselator.instance;

        for (int i = 0; i < this.polygons.length; ++i) {
            this.polygons[i].render(t, scale);
        }

        glEndList();
        this.compiled = true;
    }
}
