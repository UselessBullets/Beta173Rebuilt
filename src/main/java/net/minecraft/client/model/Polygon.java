// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.Tesselator;

public class Polygon
{
    private static final float X_TEX_SIZE = 64.0f; // Useless - doesn't definitely seem to exist in b1.7.3, however LCE made this a variable
    private static final float Y_TEX_SIZE = 32.0f; // Useless - doesn't definitely seem to exist in b1.7.3, however LCE made this a variable
    public Vertex[] vertices;
    public int vertexCount;
    private final boolean flipNormal;
    
    public Polygon(final Vertex[] vertices) {
        this.vertexCount = 0;
        this.flipNormal = false;
        this.vertices = vertices;
        this.vertexCount = vertices.length;
    }
    
    public Polygon(final Vertex[] vertices, final int u0, final int v0, final int u1, final int v1) {
        this(vertices);

        final float us = 0.1f / X_TEX_SIZE;
        final float vs = 0.1f / Y_TEX_SIZE;

        vertices[0] = vertices[0].remap(u1 / X_TEX_SIZE - us, v0 / Y_TEX_SIZE + vs);
        vertices[1] = vertices[1].remap(u0 / X_TEX_SIZE + us, v0 / Y_TEX_SIZE + vs);
        vertices[2] = vertices[2].remap(u0 / X_TEX_SIZE + us, v1 / Y_TEX_SIZE - vs);
        vertices[3] = vertices[3].remap(u1 / X_TEX_SIZE - us, v1 / Y_TEX_SIZE - vs);
    }
    
    public void mirror() {
        final Vertex[] newVertices = new Vertex[this.vertices.length];
        for (int i = 0; i < this.vertices.length; ++i) {
            newVertices[i] = this.vertices[this.vertices.length - i - 1];
        }
        this.vertices = newVertices;
    }
    
    public void render(final Tesselator t, final float scale) {
        final Vec3 v0 = this.vertices[1].pos.vectorTo(this.vertices[0].pos);
        final Vec3 v1 = this.vertices[1].pos.vectorTo(this.vertices[2].pos);
        final Vec3 n = v1.cross(v0).normalize();

        t.begin();
        if (this.flipNormal) {
            t.normal(-(float)n.x, -(float)n.y, -(float)n.z);
        }
        else {
            t.normal((float)n.x, (float)n.y, (float)n.z);
        }

        for (int i = 0; i < 4; ++i) {
            final Vertex v = this.vertices[i];
            t.vertexUV((float)v.pos.x * scale, (float)v.pos.y * scale, (float)v.pos.z * scale, v.u, v.v);
        }
        t.end();
    }
}
