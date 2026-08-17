// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AABB;

public class FrustumCuller implements Culler
{
    private final FrustrumData frustum = Frustum.getFrustum();
    private double xOff, yOff, zOff;
    
    public void prepare(final double xOff, final double yOff, final double zOff) {
        this.xOff = xOff;
        this.yOff = yOff;
        this.zOff = zOff;
    }
    
    public boolean cubeInFrustum(final double x0, final double y0, final double z0, final double x1, final double y1, final double z1) {
        return this.frustum.cubeInFrustrum(x0 - this.xOff, y0 - this.yOff, z0 - this.zOff, x1 - this.xOff, y1 - this.yOff, z1 - this.zOff);
    }

    public boolean cubeFullyInFrustum(double x0, double y0, double z0, double x1, double y1, double z1) { // Useless - In b1.2 leak & LCE Leak
        return this.frustum.cubeFullyInFrustum(x0 - this.xOff, y0 - this.yOff, z0 - this.zOff, x1 - this.xOff, y1 - this.yOff, z1 - this.zOff);
    }

    public boolean isVisible(final AABB bb) {
        return this.cubeInFrustum(bb.x0, bb.y0, bb.z0, bb.x1, bb.y1, bb.z1);
    }
}
