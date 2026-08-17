// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

public class SquidModel extends Model
{
    Cube body;
    Cube[] tentacles = new Cube[8];
    
    public SquidModel() {
        final int yOffs = -16;

        this.body = new Cube(0, 0);
        this.body.addBox(-6.0f, -8.0f, -6.0f, 12, 16, 12);
        this.body.y += 24 + yOffs;

        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = new Cube(48, 0);
            final double angle = i * Math.PI * 2.0 / this.tentacles.length;
            final float x = (float)Math.cos(angle) * 5.0f;
            final float z = (float)Math.sin(angle) * 5.0f;
            this.tentacles[i].addBox(-1.0f, 0.0f, -1.0f, 2, 18, 2);

            this.tentacles[i].x = x;
            this.tentacles[i].z = z;
            this.tentacles[i].y = (float)(31 + yOffs);

            this.tentacles[i].yRot = (float)(i * Math.PI * -2.0 / this.tentacles.length + Math.PI * 0.5);
        }
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        for (int i = 0; i < this.tentacles.length; ++i) {
            // tentacle angle is calculated in SquidRenderer
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
