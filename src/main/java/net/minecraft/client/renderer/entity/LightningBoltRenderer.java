// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.world.entity.Entity;
import java.util.Random;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.world.entity.global.LightningBolt;

import static org.lwjgl.opengl.GL11.*;

public class LightningBoltRenderer extends EntityRenderer<LightningBolt>
{
    public void render(final LightningBolt entity, final double x, final double y, final double z, final float rot, final float partialTick) {
        final Tesselator instance = Tesselator.instance;
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glDisable(GL_LIGHTING);
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(770, 1);
        final double[] array = new double[8];
        final double[] array2 = new double[8];
        double n = 0.0;
        double n2 = 0.0;
        final Random random = new Random(entity.seed);
        for (int i = 7; i >= 0; --i) {
            array[i] = n;
            array2[i] = n2;
            n += random.nextInt(11) - 5;
            n2 += random.nextInt(11) - 5;
        }
        for (int j = 0; j < 4; ++j) {
            final Random random2 = new Random(entity.seed);
            for (int k = 0; k < 3; ++k) {
                int n3 = 7;
                int n4 = 0;
                if (k > 0) {
                    n3 = 7 - k;
                }
                if (k > 0) {
                    n4 = n3 - 2;
                }
                double n5 = array[n3] - n;
                double n6 = array2[n3] - n2;
                for (int l = n3; l >= n4; --l) {
                    final double n7 = n5;
                    final double n8 = n6;
                    if (k == 0) {
                        n5 += random2.nextInt(11) - 5;
                        n6 += random2.nextInt(11) - 5;
                    }
                    else {
                        n5 += random2.nextInt(31) - 15;
                        n6 += random2.nextInt(31) - 15;
                    }
                    instance.begin(5);
                    final float n9 = 0.5f;
                    instance.color(0.9f * n9, 0.9f * n9, 1.0f * n9, 0.3f);
                    double n10 = 0.1 + j * 0.2;
                    if (k == 0) {
                        n10 *= l * 0.1 + 1.0;
                    }
                    double n11 = 0.1 + j * 0.2;
                    if (k == 0) {
                        n11 *= (l - 1) * 0.1 + 1.0;
                    }
                    for (int n12 = 0; n12 < 5; ++n12) {
                        double n13 = x + 0.5 - n10;
                        double n14 = z + 0.5 - n10;
                        if (n12 == 1 || n12 == 2) {
                            n13 += n10 * 2.0;
                        }
                        if (n12 == 2 || n12 == 3) {
                            n14 += n10 * 2.0;
                        }
                        double n15 = x + 0.5 - n11;
                        double n16 = z + 0.5 - n11;
                        if (n12 == 1 || n12 == 2) {
                            n15 += n11 * 2.0;
                        }
                        if (n12 == 2 || n12 == 3) {
                            n16 += n11 * 2.0;
                        }
                        instance.vertex(n15 + n5, y + l * 16, n16 + n6);
                        instance.vertex(n13 + n7, y + (l + 1) * 16, n14 + n8);
                    }
                    instance.end();
                }
            }
        }
        GL11.glDisable(3042);
        GL11.glDisable(GL_LIGHTING);
        GL11.glEnable(GL_TEXTURE_2D);
    }
}
