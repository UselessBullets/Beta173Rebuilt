// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import java.util.Comparator;

public class DirtyChunkSorter implements Comparator
{
    private Mob player;
    
    public DirtyChunkSorter(final Mob player) {
        this.player = player;
    }
    
    public int compare(final Chunk c0, final Chunk c1) {
        final boolean visible = c0.visible;
        final boolean visible2 = c1.visible;
        if (visible && !visible2) {
            return 1;
        }
        if (visible2 && !visible) {
            return -1;
        }
        final double n = c0.distanceToSqr(this.player);
        final double n2 = c1.distanceToSqr(this.player);
        if (n < n2) {
            return 1;
        }
        if (n > n2) {
            return -1;
        }
        return (c0.id < c1.id) ? 1 : -1;
    }
}
