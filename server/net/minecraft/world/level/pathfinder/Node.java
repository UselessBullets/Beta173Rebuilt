// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.pathfinder;

import util.Mth;

public class Node
{
    public final int x;
    public final int y;
    public final int z;
    private final int hash;
    int heapIdx;
    float g;
    float h;
    float f;
    Node cameFrom;
    public boolean closed;
    
    public Node(final int x, final int y, final int z) {
        this.heapIdx = -1;
        this.closed = false;
        this.x = x;
        this.y = y;
        this.z = z;
        this.hash = createHash(x, y, z);
    }
    
    public static int createHash(final int x, final int y, final int z) {
        return (y & 0xFF) | (x & 0x7FFF) << 8 | (z & 0x7FFF) << 24 | ((x < 0) ? Integer.MIN_VALUE : 0) | ((z < 0) ? 32768 : 0);
    }
    
    public float distanceTo(final Node node) {
        final float n = (float)(node.x - this.x);
        final float n2 = (float)(node.y - this.y);
        final float n3 = (float)(node.z - this.z);
        return Mth.sqrt(n * n + n2 * n2 + n3 * n3);
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
