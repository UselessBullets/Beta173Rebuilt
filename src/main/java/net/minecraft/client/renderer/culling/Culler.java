// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AABB;

public interface Culler
{
    boolean isVisible(final AABB bb);

    boolean cubeInFrustum(double x0, double y0, double z0, double x1, double y1, double z1); // Useless - In b1.2 & LCE leak

    boolean cubeFullyInFrustum(double x0, double y0, double z0, double x1, double y1, double z1); // Useless - In b1.2 & LCE leak
    
    void prepare(final double xOff, final double yOff, final double zOff);
}
