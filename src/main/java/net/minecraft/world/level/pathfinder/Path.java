// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.pathfinder;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;

public class Path
{
    private final Node[] nodes;
    public final int length;
    private int pos;
    
    public Path(final Node[] nodes) {
        this.nodes = nodes;
        this.length = nodes.length;
    }
    
    public void next() {
        ++this.pos;
    }
    
    public boolean isDone() {
        return this.pos >= this.nodes.length;
    }
    
    public Node last() {
        if (this.length > 0) {
            return this.nodes[this.length - 1];
        }
        return null;
    }
    
    public Vec3 current(final Entity e) {
        return Vec3.newTemp(this.nodes[this.pos].x + (int)(e.bbWidth + 1.0f) * 0.5, this.nodes[this.pos].y, this.nodes[this.pos].z + (int)(e.bbWidth + 1.0f) * 0.5);
    }
}
