// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;

public class BoatModel extends Model
{
    public Cube[] cubes;
    
    public BoatModel() {
        (this.cubes = new Cube[5])[0] = new Cube(0, 8);
        this.cubes[1] = new Cube(0, 0);
        this.cubes[2] = new Cube(0, 0);
        this.cubes[3] = new Cube(0, 0);
        this.cubes[4] = new Cube(0, 0);
        final int w = 24;
        final int n = 6;
        final int n2 = 20;
        final int n3 = 4;
        this.cubes[0].addBox((float)(-w / 2), (float)(-n2 / 2 + 2), -3.0f, w, n2 - 4, 4, 0.0f);
        this.cubes[0].setPos(0.0f, (float)(0 + n3), 0.0f);
        this.cubes[1].addBox((float)(-w / 2 + 2), (float)(-n - 1), -1.0f, w - 4, n, 2, 0.0f);
        this.cubes[1].setPos((float)(-w / 2 + 1), (float)(0 + n3), 0.0f);
        this.cubes[2].addBox((float)(-w / 2 + 2), (float)(-n - 1), -1.0f, w - 4, n, 2, 0.0f);
        this.cubes[2].setPos((float)(w / 2 - 1), (float)(0 + n3), 0.0f);
        this.cubes[3].addBox((float)(-w / 2 + 2), (float)(-n - 1), -1.0f, w - 4, n, 2, 0.0f);
        this.cubes[3].setPos(0.0f, (float)(0 + n3), (float)(-n2 / 2 + 1));
        this.cubes[4].addBox((float)(-w / 2 + 2), (float)(-n - 1), -1.0f, w - 4, n, 2, 0.0f);
        this.cubes[4].setPos(0.0f, (float)(0 + n3), (float)(n2 / 2 - 1));
        this.cubes[0].xRot = 1.5707964f;
        this.cubes[1].yRot = 4.712389f;
        this.cubes[2].yRot = 1.5707964f;
        this.cubes[3].yRot = Mth.PI;
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        for (int i = 0; i < 5; ++i) {
            this.cubes[i].render(scale);
        }
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
    }
}
