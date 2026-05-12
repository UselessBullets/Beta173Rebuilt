// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft;

public class Pos implements Comparable<Pos>
{
    public int x;
    public int y;
    public int z;
    
    public Pos() {
    }
    
    public Pos(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Pos(final Pos pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Pos)) {
            return false;
        }
        final Pos pos = (Pos)o;
        return this.x == pos.x && this.y == pos.y && this.z == pos.z;
    }
    
    @Override
    public int hashCode() {
        return this.x + this.z << 8 + this.y << 16;
    }
    
    public int compareTo(final Pos pos) {
        if (this.y != pos.y) {
            return this.y - pos.y;
        }
        if (this.z == pos.z) {
            return this.x - pos.x;
        }
        return this.z - pos.z;
    }
    
    public double dist(final int x, final int y, final int z) {
        final int n = this.x - x;
        final int n2 = this.y - y;
        final int n3 = this.z - z;
        return Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }
}
