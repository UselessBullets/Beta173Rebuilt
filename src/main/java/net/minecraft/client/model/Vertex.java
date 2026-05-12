// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import net.minecraft.world.phys.Vec3;

public class Vertex
{
    public Vec3 pos;
    public float u;
    public float v;
    
    public Vertex(final float x, final float y, final float z, final float u, final float v) {
        this(Vec3.newPermanent(x, y, z), u, v);
    }
    
    public Vertex remap(final float u, final float v) {
        return new Vertex(this, u, v);
    }
    
    public Vertex(final Vertex vertex, final float u, final float v) {
        this.pos = vertex.pos;
        this.u = u;
        this.v = v;
    }
    
    public Vertex(final Vec3 pos, final float u, final float v) {
        this.pos = pos;
        this.u = u;
        this.v = v;
    }
}
