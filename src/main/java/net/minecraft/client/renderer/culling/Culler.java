// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AABB;

public interface Culler
{
    boolean isVisible(final AABB bb);
    
    void prepare(final double xOff, final double yOff, final double zOff);
}
