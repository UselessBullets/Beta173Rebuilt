// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.culling;

public class FrustrumData
{
    public float[][] m_Frustum;
    public float[] proj;
    public float[] modl;
    public float[] clip;
    
    public FrustrumData() {
        this.m_Frustum = new float[16][16];
        this.proj = new float[16];
        this.modl = new float[16];
        this.clip = new float[16];
    }
    
    public boolean cubeInFrustrum(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        for (int i = 0; i < 6; ++i) {
            if (this.m_Frustum[i][0] * x1 + this.m_Frustum[i][1] * y1 + this.m_Frustum[i][2] * z1 + this.m_Frustum[i][3] <= 0.0) {
                if (this.m_Frustum[i][0] * x2 + this.m_Frustum[i][1] * y1 + this.m_Frustum[i][2] * z1 + this.m_Frustum[i][3] <= 0.0) {
                    if (this.m_Frustum[i][0] * x1 + this.m_Frustum[i][1] * y2 + this.m_Frustum[i][2] * z1 + this.m_Frustum[i][3] <= 0.0) {
                        if (this.m_Frustum[i][0] * x2 + this.m_Frustum[i][1] * y2 + this.m_Frustum[i][2] * z1 + this.m_Frustum[i][3] <= 0.0) {
                            if (this.m_Frustum[i][0] * x1 + this.m_Frustum[i][1] * y1 + this.m_Frustum[i][2] * z2 + this.m_Frustum[i][3] <= 0.0) {
                                if (this.m_Frustum[i][0] * x2 + this.m_Frustum[i][1] * y1 + this.m_Frustum[i][2] * z2 + this.m_Frustum[i][3] <= 0.0) {
                                    if (this.m_Frustum[i][0] * x1 + this.m_Frustum[i][1] * y2 + this.m_Frustum[i][2] * z2 + this.m_Frustum[i][3] <= 0.0) {
                                        if (this.m_Frustum[i][0] * x2 + this.m_Frustum[i][1] * y2 + this.m_Frustum[i][2] * z2 + this.m_Frustum[i][3] <= 0.0) {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
