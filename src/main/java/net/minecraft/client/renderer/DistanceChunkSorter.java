// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.world.entity.Entity;
import java.util.Comparator;

public class DistanceChunkSorter implements Comparator<Chunk>
{
    private double ix;
    private double iy;
    private double iz;
    
    public DistanceChunkSorter(final Entity player) {
        this.ix = -player.x;
        this.iy = -player.y;
        this.iz = -player.z;
    }
    
    public int compare(final Chunk a, final Chunk b) {
        final double xd0 = a.xm + this.ix;
        final double yd0 = a.ym + this.iy;
        final double zd0 = a.zm + this.iz;

        final double xd1 = b.xm + this.ix;
        final double yd1 = b.ym + this.iy;
        final double zd1 = b.zm + this.iz;

        return (int)((xd0 * xd0 + yd0 * yd0 + zd0 * zd0 - (xd1 * xd1 + yd1 * yd1 + zd1 * zd1)) * 1024.0);
    }
}
