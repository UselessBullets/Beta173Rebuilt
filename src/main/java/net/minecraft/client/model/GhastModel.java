// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;
import java.util.Random;

public class GhastModel extends Model
{
    Cube body;
    Cube[] tentacles = new Cube[9];
    
    public GhastModel() {
        final int yOffs = -16;
        this.body = new Cube(0, 0);
        this.body.addBox(-8.0f, -8.0f, -8.0f, 16, 16, 16);
        this.body.y += 24 + yOffs;

        final Random random = new Random(1660L);
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = new Cube(0, 0);

            final float xo = (((i % 3 - (i / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2 - 1) * 5);
            final float yo = (((i / 3) / 2.0f * 2 - 1) * 5);
            int len = random.nextInt(7) + 8;
            this.tentacles[i].addBox(-1.0f, 0.0f, -1.0f, 2, len, 2);

            this.tentacles[i].x = xo;
            this.tentacles[i].z = yo;
            this.tentacles[i].y = (float)(31 + yOffs);
        }
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i].xRot = 0.2f * Mth.sin(bob * 0.3f + i) + 0.4f;
        }
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.setupAnim(time, r, bob, yRot, xRot, scale);

        this.body.render(scale);
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i].render(scale);
        }
    }
}
