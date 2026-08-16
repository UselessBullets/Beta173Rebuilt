// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

public class SquidModel extends Model
{
    Cube body;
    Cube[] tentacles;
    
    public SquidModel() {
        this.tentacles = new Cube[8];
        final int n = -16;
        (this.body = new Cube(0, 0)).addBox(-6.0f, -8.0f, -6.0f, 12, 16, 12);
        final Cube body = this.body;
        body.y += 24 + n;
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = new Cube(48, 0);
            final double n2 = i * Math.PI * 2.0 / this.tentacles.length;
            final float x = (float)Math.cos(n2) * 5.0f;
            final float z = (float)Math.sin(n2) * 5.0f;
            this.tentacles[i].addBox(-1.0f, 0.0f, -1.0f, 2, 18, 2);
            this.tentacles[i].x = x;
            this.tentacles[i].z = z;
            this.tentacles[i].y = (float)(31 + n);
            this.tentacles[i].yRot = (float)(i * Math.PI * -2.0 / this.tentacles.length + 1.5707963267948966);
        }
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i].xRot = bob;
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
