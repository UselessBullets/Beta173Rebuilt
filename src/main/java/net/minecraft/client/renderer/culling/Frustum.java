// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.culling;

import util.Mth;
import net.minecraft.client.MemoryTracker;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
// TODO Useless - alot of this class looks like it was copied from elsewhere might be fun to track down the original source Notch copied this from
public class Frustum extends FrustrumData
{
    private static final Frustum frustum = new Frustum();
    private final FloatBuffer _proj = MemoryTracker.createFloatBuffer(16);
    private final FloatBuffer _modl = MemoryTracker.createFloatBuffer(16);
    private final FloatBuffer _clip = MemoryTracker.createFloatBuffer(16);

    public static FrustrumData getFrustum() {
        Frustum.frustum.calculateFrustum();
        return Frustum.frustum;
    }

    ///////////////////////////////// NORMALIZE PLANE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////	This normalizes a plane (A side) from a given frustum.
    /////
    ///////////////////////////////// NORMALIZE PLANE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    private void normalizePlane(final float[][] frustum, final int side) {
        final float magnitude = Mth.sqrt(frustum[side][0] * frustum[side][0] + frustum[side][1] * frustum[side][1] + frustum[side][2] * frustum[side][2]);
        // Then we divide the plane's values by it's magnitude.
        // This makes it easier to work with.
        frustum[side][FrustrumData.A] /= magnitude;
        frustum[side][FrustrumData.B] /= magnitude;
        frustum[side][FrustrumData.C] /= magnitude;
        frustum[side][FrustrumData.D] /= magnitude;
    }
    
    private void calculateFrustum() {
        this._proj.clear();
        this._modl.clear();
        this._clip.clear();

        // glGetFloatv() is used to extract information about our OpenGL world.
        // Below, we pass in GL_PROJECTION_MATRIX to abstract our projection matrix.
        // It then stores the matrix into an array of [16].
        glGetFloat(GL_PROJECTION_MATRIX, this._proj);

        // By passing in GL_MODELVIEW_MATRIX, we can abstract our model view matrix.
        // This also stores it in an array of [16].
        glGetFloat(GL_MODELVIEW_MATRIX, this._modl);

        this._proj.flip().limit(16);
        this._proj.get(this.proj);
        this._modl.flip().limit(16);
        this._modl.get(this.modl);

        // Now that we have our modelview and projection matrix, if we combine these 2 matrices,
        // it will give us our clipping planes.  To combine 2 matrices, we multiply them.

        this.clip[0] = this.modl[0] * this.proj[0] + this.modl[1] * this.proj[4] + this.modl[2] * this.proj[8] + this.modl[3] * this.proj[12];
        this.clip[1] = this.modl[0] * this.proj[1] + this.modl[1] * this.proj[5] + this.modl[2] * this.proj[9] + this.modl[3] * this.proj[13];
        this.clip[2] = this.modl[0] * this.proj[2] + this.modl[1] * this.proj[6] + this.modl[2] * this.proj[10] + this.modl[3] * this.proj[14];
        this.clip[3] = this.modl[0] * this.proj[3] + this.modl[1] * this.proj[7] + this.modl[2] * this.proj[11] + this.modl[3] * this.proj[15];

        this.clip[4] = this.modl[4] * this.proj[0] + this.modl[5] * this.proj[4] + this.modl[6] * this.proj[8] + this.modl[7] * this.proj[12];
        this.clip[5] = this.modl[4] * this.proj[1] + this.modl[5] * this.proj[5] + this.modl[6] * this.proj[9] + this.modl[7] * this.proj[13];
        this.clip[6] = this.modl[4] * this.proj[2] + this.modl[5] * this.proj[6] + this.modl[6] * this.proj[10] + this.modl[7] * this.proj[14];
        this.clip[7] = this.modl[4] * this.proj[3] + this.modl[5] * this.proj[7] + this.modl[6] * this.proj[11] + this.modl[7] * this.proj[15];

        this.clip[8] = this.modl[8] * this.proj[0] + this.modl[9] * this.proj[4] + this.modl[10] * this.proj[8] + this.modl[11] * this.proj[12];
        this.clip[9] = this.modl[8] * this.proj[1] + this.modl[9] * this.proj[5] + this.modl[10] * this.proj[9] + this.modl[11] * this.proj[13];
        this.clip[10] = this.modl[8] * this.proj[2] + this.modl[9] * this.proj[6] + this.modl[10] * this.proj[10] + this.modl[11] * this.proj[14];
        this.clip[11] = this.modl[8] * this.proj[3] + this.modl[9] * this.proj[7] + this.modl[10] * this.proj[11] + this.modl[11] * this.proj[15];

        this.clip[12] = this.modl[12] * this.proj[0] + this.modl[13] * this.proj[4] + this.modl[14] * this.proj[8] + this.modl[15] * this.proj[12];
        this.clip[13] = this.modl[12] * this.proj[1] + this.modl[13] * this.proj[5] + this.modl[14] * this.proj[9] + this.modl[15] * this.proj[13];
        this.clip[14] = this.modl[12] * this.proj[2] + this.modl[13] * this.proj[6] + this.modl[14] * this.proj[10] + this.modl[15] * this.proj[14];
        this.clip[15] = this.modl[12] * this.proj[3] + this.modl[13] * this.proj[7] + this.modl[14] * this.proj[11] + this.modl[15] * this.proj[15];

        // Now we actually want to get the sides of the frustum.  To do this we take
        // the clipping planes we received above and extract the sides from them.

        // This will extract the RIGHT side of the frustum
        this.m_Frustum[RIGHT][A] = this.clip[3] - this.clip[0];
        this.m_Frustum[RIGHT][B] = this.clip[7] - this.clip[4];
        this.m_Frustum[RIGHT][C] = this.clip[11] - this.clip[8];
        this.m_Frustum[RIGHT][D] = this.clip[15] - this.clip[12];

        // Now that we have a normal (A,B,C) and a distance (D) to the plane,
        // we want to normalize that normal and distance.

        // Normalize the RIGHT side
        normalizePlane(this.m_Frustum, RIGHT);

        // This will extract the LEFT side of the frustum
        this.m_Frustum[LEFT][A] = this.clip[3] + this.clip[0];
        this.m_Frustum[LEFT][B] = this.clip[7] + this.clip[4];
        this.m_Frustum[LEFT][C] = this.clip[11] + this.clip[8];
        this.m_Frustum[LEFT][D] = this.clip[15] + this.clip[12];

        // Normalize the LEFT side
        normalizePlane(this.m_Frustum, LEFT);

        // This will extract the BOTTOM side of the frustum
        this.m_Frustum[BOTTOM][A] = this.clip[3] + this.clip[1];
        this.m_Frustum[BOTTOM][B] = this.clip[7] + this.clip[5];
        this.m_Frustum[BOTTOM][C] = this.clip[11] + this.clip[9];
        this.m_Frustum[BOTTOM][D] = this.clip[15] + this.clip[13];

        // Normalize the BOTTOM side
        normalizePlane(this.m_Frustum, BOTTOM);

        // This will extract the TOP side of the frustum
        this.m_Frustum[TOP][A] = this.clip[3] - this.clip[1];
        this.m_Frustum[TOP][B] = this.clip[7] - this.clip[5];
        this.m_Frustum[TOP][C] = this.clip[11] - this.clip[9];
        this.m_Frustum[TOP][D] = this.clip[15] - this.clip[13];

        // Normalize the TOP side
        normalizePlane(this.m_Frustum, TOP);

        // This will extract the BACK side of the frustum
        this.m_Frustum[BACK][A] = this.clip[3] - this.clip[2];
        this.m_Frustum[BACK][B] = this.clip[7] - this.clip[6];
        this.m_Frustum[BACK][C] = this.clip[11] - this.clip[10];
        this.m_Frustum[BACK][D] = this.clip[15] - this.clip[14];

        // Normalize the BACK side
        normalizePlane(this.m_Frustum, BACK);

        // This will extract the FRONT side of the frustum
        this.m_Frustum[FRONT][A] = this.clip[3] + this.clip[2];
        this.m_Frustum[FRONT][B] = this.clip[7] + this.clip[6];
        this.m_Frustum[FRONT][C] = this.clip[11] + this.clip[10];
        this.m_Frustum[FRONT][D] = this.clip[15] + this.clip[14];

        // Normalize the FRONT side
        normalizePlane(this.m_Frustum, FRONT);
    }

}
