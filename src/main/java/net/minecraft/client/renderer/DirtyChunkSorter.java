// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.world.entity.Mob;
import java.util.Comparator;

public class DirtyChunkSorter implements Comparator<Chunk>
{
    private Mob player;
    
    public DirtyChunkSorter(final Mob player) {
        this.player = player;
    }
    
    public int compare(final Chunk c0, final Chunk c1) {
        final boolean i0 = c0.visible;
        final boolean i1 = c1.visible;
        if (i0 && !i1) return 1;
        if (i1 && !i0) return -1;

        final double d0 = c0.distanceToSqr(this.player);
        final double d1 = c1.distanceToSqr(this.player);

        if (d0 < d1) return 1;
        if (d0 > d1) return -1;

        return (c0.id < c1.id) ? 1 : -1;
    }
}
