// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.Tesselator;

public class Polygon
{
    public Vertex[] vertices;
    public int vertexCount;
    private boolean flipNormal;
    
    public Polygon(final Vertex[] vertices) {
        this.vertexCount = 0;
        this.flipNormal = false;
        this.vertices = vertices;
        this.vertexCount = vertices.length;
    }
    
    public Polygon(final Vertex[] vertices, final int u0, final int v0, final int u1, final int v1) {
        this(vertices);
        final float n = 0.0015625f;
        final float n2 = 0.003125f;
        vertices[0] = vertices[0].remap(u1 / 64.0f - n, v0 / 32.0f + n2);
        vertices[1] = vertices[1].remap(u0 / 64.0f + n, v0 / 32.0f + n2);
        vertices[2] = vertices[2].remap(u0 / 64.0f + n, v1 / 32.0f - n2);
        vertices[3] = vertices[3].remap(u1 / 64.0f - n, v1 / 32.0f - n2);
    }
    
    public void mirror() {
        final Vertex[] vertices = new Vertex[this.vertices.length];
        for (int i = 0; i < this.vertices.length; ++i) {
            vertices[i] = this.vertices[this.vertices.length - i - 1];
        }
        this.vertices = vertices;
    }
    
    public void render(final Tesselator t, final float scale) {
        final Vec3 normalize = this.vertices[1].pos.vectorTo(this.vertices[2].pos).cross(this.vertices[1].pos.vectorTo(this.vertices[0].pos)).normalize();
        t.begin();
        if (this.flipNormal) {
            t.normal(-(float)normalize.x, -(float)normalize.y, -(float)normalize.z);
        }
        else {
            t.normal((float)normalize.x, (float)normalize.y, (float)normalize.z);
        }
        for (int i = 0; i < 4; ++i) {
            final Vertex vertex = this.vertices[i];
            t.vertexUV((float)vertex.pos.x * scale, (float)vertex.pos.y * scale, (float)vertex.pos.z * scale, vertex.u, vertex.v);
        }
        t.end();
    }
}
