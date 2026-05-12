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
        final double n = a.xm + this.ix;
        final double n2 = a.ym + this.iy;
        final double n3 = a.zm + this.iz;
        final double n4 = b.xm + this.ix;
        final double n5 = b.ym + this.iy;
        final double n6 = b.zm + this.iz;
        return (int)((n * n + n2 * n2 + n3 * n3 - (n4 * n4 + n5 * n5 + n6 * n6)) * 1024.0);
    }
}
