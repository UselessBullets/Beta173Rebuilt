// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import util.Mth;
import net.minecraft.client.renderer.Tesselator;
import org.lwjgl.opengl.GL11;
import net.minecraft.world.entity.Painting;
import java.util.Random;

import static org.lwjgl.opengl.GL12.*;

public class PaintingRenderer extends EntityRenderer<Painting>
{
    private Random random;
    
    public PaintingRenderer() {
        this.random = new Random();
    }
    
    public void render(final Painting entity, final double x, final double y, final double z, final float rot, final float a) {
        this.random.setSeed(187L);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(rot, 0.0f, 1.0f, 0.0f);
        GL11.glEnable(GL_RESCALE_NORMAL);
        this.bindTexture("/art/kz.png");
        final Painting.Motive motive = entity.motive;
        final float n = 0.0625f;
        GL11.glScalef(n, n, n);
        this.renderPainting(entity, motive.w, motive.h, motive.uo, motive.vo);
        GL11.glDisable(GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
    
    private void renderPainting(final Painting painting, final int w, final int h, final int uo, final int vo) {
        final float n = -w / 2.0f;
        final float n2 = -h / 2.0f;
        final float n3 = -0.5f;
        final float n4 = 0.5f;
        for (int i = 0; i < w / 16; ++i) {
            for (int j = 0; j < h / 16; ++j) {
                final float n5 = n + (i + 1) * 16;
                final float n6 = n + i * 16;
                final float n7 = n2 + (j + 1) * 16;
                final float n8 = n2 + j * 16;
                this.setBrightness(painting, (n5 + n6) / 2.0f, (n7 + n8) / 2.0f);
                final float n9 = (uo + w - i * 16) / 256.0f;
                final float n10 = (uo + w - (i + 1) * 16) / 256.0f;
                final float n11 = (vo + h - j * 16) / 256.0f;
                final float n12 = (vo + h - (j + 1) * 16) / 256.0f;
                final float n13 = 0.75f;
                final float n14 = 0.8125f;
                final float n15 = 0.0f;
                final float n16 = 0.0625f;
                final float n17 = 0.75f;
                final float n18 = 0.8125f;
                final float n19 = 0.001953125f;
                final float n20 = 0.001953125f;
                final float n21 = 0.7519531f;
                final float n22 = 0.7519531f;
                final float n23 = 0.0f;
                final float n24 = 0.0625f;
                final Tesselator instance = Tesselator.instance;
                instance.begin();
                instance.normal(0.0f, 0.0f, -1.0f);
                instance.vertexUV(n5, n8, n3, n10, n11);
                instance.vertexUV(n6, n8, n3, n9, n11);
                instance.vertexUV(n6, n7, n3, n9, n12);
                instance.vertexUV(n5, n7, n3, n10, n12);
                instance.normal(0.0f, 0.0f, 1.0f);
                instance.vertexUV(n5, n7, n4, n13, n15);
                instance.vertexUV(n6, n7, n4, n14, n15);
                instance.vertexUV(n6, n8, n4, n14, n16);
                instance.vertexUV(n5, n8, n4, n13, n16);
                instance.normal(0.0f, -1.0f, 0.0f);
                instance.vertexUV(n5, n7, n3, n17, n19);
                instance.vertexUV(n6, n7, n3, n18, n19);
                instance.vertexUV(n6, n7, n4, n18, n20);
                instance.vertexUV(n5, n7, n4, n17, n20);
                instance.normal(0.0f, 1.0f, 0.0f);
                instance.vertexUV(n5, n8, n4, n17, n19);
                instance.vertexUV(n6, n8, n4, n18, n19);
                instance.vertexUV(n6, n8, n3, n18, n20);
                instance.vertexUV(n5, n8, n3, n17, n20);
                instance.normal(-1.0f, 0.0f, 0.0f);
                instance.vertexUV(n5, n7, n4, n22, n23);
                instance.vertexUV(n5, n8, n4, n22, n24);
                instance.vertexUV(n5, n8, n3, n21, n24);
                instance.vertexUV(n5, n7, n3, n21, n23);
                instance.normal(1.0f, 0.0f, 0.0f);
                instance.vertexUV(n6, n7, n3, n22, n23);
                instance.vertexUV(n6, n8, n3, n22, n24);
                instance.vertexUV(n6, n8, n4, n21, n24);
                instance.vertexUV(n6, n7, n4, n21, n23);
                instance.end();
            }
        }
    }
    
    private void setBrightness(final Painting painting, final float ss, final float ya) {
        int x = Mth.floor(painting.x);
        final int floor = Mth.floor(painting.y + ya / 16.0f);
        int z = Mth.floor(painting.z);
        if (painting.dir == 0) {
            x = Mth.floor(painting.x + ss / 16.0f);
        }
        if (painting.dir == 1) {
            z = Mth.floor(painting.z - ss / 16.0f);
        }
        if (painting.dir == 2) {
            x = Mth.floor(painting.x - ss / 16.0f);
        }
        if (painting.dir == 3) {
            z = Mth.floor(painting.z + ss / 16.0f);
        }
        final float brightness = this.entityRenderDispatcher.level.getBrightness(x, floor, z);
        GL11.glColor3f(brightness, brightness, brightness);
    }
}
