// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

public class SlimeModel extends Model
{
    Cube cube;
    Cube eye0;
    Cube eye1;
    Cube mouth;
    
    public SlimeModel(final int vOffs) {
        (this.cube = new Cube(0, vOffs)).addBox(-4.0f, 16.0f, -4.0f, 8, 8, 8);
        if (vOffs > 0) {
            (this.cube = new Cube(0, vOffs)).addBox(-3.0f, 17.0f, -3.0f, 6, 6, 6);
            (this.eye0 = new Cube(32, 0)).addBox(-3.25f, 18.0f, -3.5f, 2, 2, 2);
            (this.eye1 = new Cube(32, 4)).addBox(1.25f, 18.0f, -3.5f, 2, 2, 2);
            (this.mouth = new Cube(32, 8)).addBox(0.0f, 21.0f, -3.5f, 1, 1, 1);
        }
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.setupAnim(time, r, bob, yRot, xRot, scale);
        this.cube.render(scale);
        if (this.eye0 != null) {
            this.eye0.render(scale);
            this.eye1.render(scale);
            this.mouth.render(scale);
        }
    }
}
