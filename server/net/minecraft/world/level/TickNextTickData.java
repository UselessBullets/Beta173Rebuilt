// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level;

public class TickNextTickData implements Comparable
{
    private static long C;
    public int x;
    public int y;
    public int z;
    public int tileId;
    public long delay;
    private long c;
    
    public TickNextTickData(final int x, final int y, final int z, final int tileId) {
        this.c = TickNextTickData.C++;
        this.x = x;
        this.y = y;
        this.z = z;
        this.tileId = tileId;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof TickNextTickData) {
            final TickNextTickData tickNextTickData = (TickNextTickData)o;
            return this.x == tickNextTickData.x && this.y == tickNextTickData.y && this.z == tickNextTickData.z && this.tileId == tickNextTickData.tileId;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (this.x * 128 * 1024 + this.z * 128 + this.y) * 256 + this.tileId;
    }
    
    public TickNextTickData delay(final long l) {
        this.delay = l;
        return this;
    }
    
    public int compareTo(final TickNextTickData tnd) {
        if (this.delay < tnd.delay) {
            return -1;
        }
        if (this.delay > tnd.delay) {
            return 1;
        }
        if (this.c < tnd.c) {
            return -1;
        }
        if (this.c > tnd.c) {
            return 1;
        }
        return 0;
    }
    
    static {
        TickNextTickData.C = 0L;
    }
}
