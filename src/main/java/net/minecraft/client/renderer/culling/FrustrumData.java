// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.culling;

public class FrustrumData
{
    //enum FrustumSide
    public static final int RIGHT = 0; // The RIGHT side of the frustum
    public static final int LEFT = 1; // The LEFT    side of the frustum
    public static final int BOTTOM = 2; // The BOTTOM side of the frustum
    public static final int TOP = 3; // The TOP side of the frustum
    public static final int BACK = 4; // The BACK   side of the frustum
    public static final int FRONT = 5; // The FRONT side of the frustum

    // Like above, instead of saying a number for the ABC and D of the plane, we
    // want to be more descriptive.
    public static final int A = 0; // The X value of the plane's normal
    public static final int B = 1; // The Y value of the plane's normal
    public static final int C = 2; // The Z value of the plane's normal
    public static final int D = 3; // The distance the plane is from the origin

    public float[][] m_Frustum = new float[16][16];
    public float[] proj = new float[16];
    public float[] modl = new float[16];
    public float[] clip = new float[16];

    boolean pointInFrustum(float x, float y, float z) // Useless - In b1.2 leak & LCE Leak
    {
        for (int i = 0; i < 6; i++)
        {
            if (this.m_Frustum[i][A] * x + this.m_Frustum[i][B] * y + this.m_Frustum[i][C] * z + this.m_Frustum[i][D] <= 0)
            {
                return false;
            }
        }

        return true;
    }

    boolean sphereInFrustum(float x, float y, float z, float radius) // Useless - In b1.2 leak & LCE Leak
    {
        for (int i = 0; i < 6; i++)
        {
            if (this.m_Frustum[i][A] * x + this.m_Frustum[i][B] * y + this.m_Frustum[i][C] * z + this.m_Frustum[i][D] <= -radius)
            {
                return false;
            }
        }

        return true;
    }

    boolean cubeFullyInFrustum(double x1, double y1, double z1, double x2, double y2, double z2) // Useless - In b1.2 leak & LCE Leak
    {
        for (int i = 0; i < 6; i++)
        {
            if (!(this.m_Frustum[i][A] * (x1) + this.m_Frustum[i][B] * (y1) + this.m_Frustum[i][C] * (z1) + this.m_Frustum[i][D] > 0)) return false;
            if (!(this.m_Frustum[i][A] * (x2) + this.m_Frustum[i][B] * (y1) + this.m_Frustum[i][C] * (z1) + this.m_Frustum[i][D] > 0)) return false;
            if (!(this.m_Frustum[i][A] * (x1) + this.m_Frustum[i][B] * (y2) + this.m_Frustum[i][C] * (z1) + this.m_Frustum[i][D] > 0)) return false;
            if (!(this.m_Frustum[i][A] * (x2) + this.m_Frustum[i][B] * (y2) + this.m_Frustum[i][C] * (z1) + this.m_Frustum[i][D] > 0)) return false;
            if (!(this.m_Frustum[i][A] * (x1) + this.m_Frustum[i][B] * (y1) + this.m_Frustum[i][C] * (z2) + this.m_Frustum[i][D] > 0)) return false;
            if (!(this.m_Frustum[i][A] * (x2) + this.m_Frustum[i][B] * (y1) + this.m_Frustum[i][C] * (z2) + this.m_Frustum[i][D] > 0)) return false;
            if (!(this.m_Frustum[i][A] * (x1) + this.m_Frustum[i][B] * (y2) + this.m_Frustum[i][C] * (z2) + this.m_Frustum[i][D] > 0)) return false;
            if (!(this.m_Frustum[i][A] * (x2) + this.m_Frustum[i][B] * (y2) + this.m_Frustum[i][C] * (z2) + this.m_Frustum[i][D] > 0)) return false;
        }

        return true;
    }
    
    public boolean cubeInFrustrum(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        for (int i = 0; i < 6; i++)
        {
            if (this.m_Frustum[i][A] * (x1) + this.m_Frustum[i][B] * (y1) + this.m_Frustum[i][C] * (z1) + this.m_Frustum[i][D] > 0) continue;
            if (this.m_Frustum[i][A] * (x2) + this.m_Frustum[i][B] * (y1) + this.m_Frustum[i][C] * (z1) + this.m_Frustum[i][D] > 0) continue;
            if (this.m_Frustum[i][A] * (x1) + this.m_Frustum[i][B] * (y2) + this.m_Frustum[i][C] * (z1) + this.m_Frustum[i][D] > 0) continue;
            if (this.m_Frustum[i][A] * (x2) + this.m_Frustum[i][B] * (y2) + this.m_Frustum[i][C] * (z1) + this.m_Frustum[i][D] > 0) continue;
            if (this.m_Frustum[i][A] * (x1) + this.m_Frustum[i][B] * (y1) + this.m_Frustum[i][C] * (z2) + this.m_Frustum[i][D] > 0) continue;
            if (this.m_Frustum[i][A] * (x2) + this.m_Frustum[i][B] * (y1) + this.m_Frustum[i][C] * (z2) + this.m_Frustum[i][D] > 0) continue;
            if (this.m_Frustum[i][A] * (x1) + this.m_Frustum[i][B] * (y2) + this.m_Frustum[i][C] * (z2) + this.m_Frustum[i][D] > 0) continue;
            if (this.m_Frustum[i][A] * (x2) + this.m_Frustum[i][B] * (y2) + this.m_Frustum[i][C] * (z2) + this.m_Frustum[i][D] > 0) continue;

            return false;
        }

        return true;
    }
}
