// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import java.util.Random;

import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.entity.global.LightningBolt;

import static org.lwjgl.opengl.GL11.*;

public class LightningBoltRenderer extends EntityRenderer<LightningBolt>
{
    public void render(final LightningBolt lightningBolt, final double x, final double y, final double z, final float rot, final float a) {
        final Tesselator t = Tesselator.instance;

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        final double[] xOffs = new double[8];
        final double[] zOffs = new double[8];
        double xOff = 0.0;
        double zOff = 0.0;
        {
            final Random random = new Random(lightningBolt.seed);
            for (int i = 7; i >= 0; --i) {
                xOffs[i] = xOff;
                zOffs[i] = zOff;
                xOff += random.nextInt(11) - 5;
                zOff += random.nextInt(11) - 5;
            }
        }

        for (int r = 0; r < 4; ++r) {
            final Random random = new Random(lightningBolt.seed);
            for (int p = 0; p < 3; ++p) {
                int hs = 7;
                int ht = 0;
                if (p > 0) hs = 7 - p;
                if (p > 0) ht = hs - 2;
                double xo0 = xOffs[hs] - xOff;
                double zo0 = zOffs[hs] - zOff;
                for (int h = hs; h >= ht; --h) {
                    final double xo1 = xo0;
                    final double zo1 = zo0;
                    if (p == 0) {
                        xo0 += random.nextInt(11) - 5;
                        zo0 += random.nextInt(11) - 5;
                    }
                    else {
                        xo0 += random.nextInt(31) - 15;
                        zo0 += random.nextInt(31) - 15;
                    }

                    t.begin(GL_TRIANGLE_STRIP);
                    final float br = 0.5f;
                    t.color(0.9f * br, 0.9f * br, 1.0f * br, 0.3f);

                    double rr1 = 0.1 + r * 0.2;
                    if (p == 0) rr1 *= h * 0.1 + 1.0;

                    double rr2 = 0.1 + r * 0.2;
                    if (p == 0) rr2 *= (h - 1) * 0.1 + 1.0;

                    for (int i = 0; i < 5; ++i) {
                        double xx1 = x + 0.5 - rr1;
                        double zz1 = z + 0.5 - rr1;
                        if (i == 1 || i == 2) xx1 += rr1 * 2.0;
                        if (i == 2 || i == 3) zz1 += rr1 * 2.0;

                        double xx2 = x + 0.5 - rr2;
                        double zz2 = z + 0.5 - rr2;
                        if (i == 1 || i == 2) xx2 += rr2 * 2.0;
                        if (i == 2 || i == 3) zz2 += rr2 * 2.0;

                        t.vertex(xx2 + xo0, y + h * 16, zz2 + zo0);
                        t.vertex(xx1 + xo1, y + (h + 1) * 16, zz1 + zo1);
                    }
                    t.end();
                }
            }
        }

        glDisable(GL_BLEND);
        glEnable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
    }
}
