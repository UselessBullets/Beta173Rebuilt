// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.pathfinder;

import util.Mth;

public class Node
{
    public final int x, y, z;
    private final int hash;
    int heapIdx = -1;
    float g, h, f;
    Node cameFrom;
    public boolean closed = false;
    
    public Node(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.hash = createHash(x, y, z);
    }
    
    public static int createHash(final int x, final int y, final int z) {
        return (y & 0xFF) | (x & 0x7FFF) << 8 | (z & 0x7FFF) << 24 | ((x < 0) ? Integer.MIN_VALUE : 0) | ((z < 0) ? 32768 : 0);
    }
    
    public float distanceTo(final Node node) {
        final float xd = (float)(node.x - this.x);
        final float yd = (float)(node.y - this.y);
        final float zd = (float)(node.z - this.z);
        return Mth.sqrt(xd * xd + yd * yd + zd * zd);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Node) {
            final Node node = (Node)o;
            return this.hash == node.hash && this.x == node.x && this.y == node.y && this.z == node.z;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.hash;
    }
    
    public boolean isOpenSet() {
        return this.heapIdx >= 0;
    }
    
    @Override
    public String toString() {
        return this.x + ", " + this.y + ", " + this.z;
    }
}
