// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import util.Mth;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.entity.Painting;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class PaintingRenderer extends EntityRenderer<Painting>
{
    private Random random = new Random();
    
    public void render(final Painting entity, final double x, final double y, final double z, final float rot, final float a) {
        this.random.setSeed(187L);

        glPushMatrix();
        glTranslatef((float)x, (float)y, (float)z);
        glRotatef(rot, 0.0f, 1.0f, 0.0f);
        glEnable(GL_RESCALE_NORMAL);
        this.bindTexture("/art/kz.png");

        final Painting.Motive motive = entity.motive;
        final float s = 1/ 16.0f;
        glScalef(s, s, s);
        this.renderPainting(entity, motive.w, motive.h, motive.uo, motive.vo);
        glDisable(GL_RESCALE_NORMAL);
        glPopMatrix();
    }
    
    private void renderPainting(final Painting painting, final int w, final int h, final int uo, final int vo) {
        final float xx0 = -w / 2.0f;
        final float yy0 = -h / 2.0f;

        final float z0 = -0.5f;
        final float z1 = 0.5f;

        for (int xs = 0; xs < w / 16; ++xs) {
            for (int ys = 0; ys < h / 16; ++ys) {
                final float x0 = xx0 + (xs + 1) * 16;
                final float x1 = xx0 + xs * 16;
                final float y0 = yy0 + (ys + 1) * 16;
                final float y1 = yy0 + ys * 16;

                this.setBrightness(painting, (x0 + x1) / 2.0f, (y0 + y1) / 2.0f);

                final float fu0 = (uo + w - xs * 16) / 256.0f;
                final float fu1 = (uo + w - (xs + 1) * 16) / 256.0f;
                final float fv0 = (vo + h - ys * 16) / 256.0f;
                final float fv1 = (vo + h - (ys + 1) * 16) / 256.0f;

                final float bu0 = (12 * 16) / 256.0f;
                final float bu1 = (12 * 16 + 16) / 256.0f;
                final float bv0 = (0) / 256.0f;
                final float bv1 = (0 + 16) / 256.0f;

                final float uu0 = (12 * 16) / 256.0f;
                final float uu1 = (12 * 16 + 16) / 256.0f;
                final float uv0 = (0.5f) / 256.0f;
                final float uv1 = (0.5f) / 256.0f;

                final float su0 = (12 * 16 + 0.5f) / 256.0f;
                final float su1 = (12 * 16 + 0.5f) / 256.0f;
                final float sv0 = (0) / 256.0f;
                final float sv1 = (0 + 16) / 256.0f;

                final Tesselator t = Tesselator.instance;
                t.begin();
                t.normal(0.0f, 0.0f, -1.0f);
                t.vertexUV(x0, y1, z0, fu1, fv0);
                t.vertexUV(x1, y1, z0, fu0, fv0);
                t.vertexUV(x1, y0, z0, fu0, fv1);
                t.vertexUV(x0, y0, z0, fu1, fv1);

                t.normal(0.0f, 0.0f, 1.0f);
                t.vertexUV(x0, y0, z1, bu0, bv0);
                t.vertexUV(x1, y0, z1, bu1, bv0);
                t.vertexUV(x1, y1, z1, bu1, bv1);
                t.vertexUV(x0, y1, z1, bu0, bv1);

                t.normal(0.0f, -1.0f, 0.0f);
                t.vertexUV(x0, y0, z0, uu0, uv0);
                t.vertexUV(x1, y0, z0, uu1, uv0);
                t.vertexUV(x1, y0, z1, uu1, uv1);
                t.vertexUV(x0, y0, z1, uu0, uv1);

                t.normal(0.0f, 1.0f, 0.0f);
                t.vertexUV(x0, y1, z1, uu0, uv0);
                t.vertexUV(x1, y1, z1, uu1, uv0);
                t.vertexUV(x1, y1, z0, uu1, uv1);
                t.vertexUV(x0, y1, z0, uu0, uv1);

                t.normal(-1.0f, 0.0f, 0.0f);
                t.vertexUV(x0, y0, z1, su1, sv0);
                t.vertexUV(x0, y1, z1, su1, sv1);
                t.vertexUV(x0, y1, z0, su0, sv1);
                t.vertexUV(x0, y0, z0, su0, sv0);

                t.normal(1.0f, 0.0f, 0.0f);
                t.vertexUV(x1, y0, z0, su1, sv0);
                t.vertexUV(x1, y1, z0, su1, sv1);
                t.vertexUV(x1, y1, z1, su0, sv1);
                t.vertexUV(x1, y0, z1, su0, sv0);
                t.end();
            }
        }
    }
    
    private void setBrightness(final Painting painting, final float ss, final float ya) {
        int x = Mth.floor(painting.x);
        int y = Mth.floor(painting.y + ya / 16.0f);
        int z = Mth.floor(painting.z);
        if (painting.dir == 0) x = Mth.floor(painting.x + ss / 16.0f);
        if (painting.dir == 1) z = Mth.floor(painting.z - ss / 16.0f);
        if (painting.dir == 2) x = Mth.floor(painting.x - ss / 16.0f);
        if (painting.dir == 3) z = Mth.floor(painting.z + ss / 16.0f);

        final float br = this.entityRenderDispatcher.level.getBrightness(x, y, z);
        glColor3f(br, br, br);
    }
}
