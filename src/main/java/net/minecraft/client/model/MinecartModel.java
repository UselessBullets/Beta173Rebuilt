// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.model;

import util.Mth;

public class MinecartModel extends Model
{
    public Cube[] cubes;
    
    public MinecartModel() {
        (this.cubes = new Cube[7])[0] = new Cube(0, 10);
        this.cubes[1] = new Cube(0, 0);
        this.cubes[2] = new Cube(0, 0);
        this.cubes[3] = new Cube(0, 0);
        this.cubes[4] = new Cube(0, 0);
        this.cubes[5] = new Cube(44, 10);
        final int w = 20;
        final int n = 8;
        final int h = 16;
        final int n2 = 4;
        this.cubes[0].addBox((float)(-w / 2), (float)(-h / 2), -1.0f, w, h, 2, 0.0f);
        this.cubes[0].setPos(0.0f, (float)(0 + n2), 0.0f);
        this.cubes[5].addBox((float)(-w / 2 + 1), (float)(-h / 2 + 1), -1.0f, w - 2, h - 2, 1, 0.0f);
        this.cubes[5].setPos(0.0f, (float)(0 + n2), 0.0f);
        this.cubes[1].addBox((float)(-w / 2 + 2), (float)(-n - 1), -1.0f, w - 4, n, 2, 0.0f);
        this.cubes[1].setPos((float)(-w / 2 + 1), (float)(0 + n2), 0.0f);
        this.cubes[2].addBox((float)(-w / 2 + 2), (float)(-n - 1), -1.0f, w - 4, n, 2, 0.0f);
        this.cubes[2].setPos((float)(w / 2 - 1), (float)(0 + n2), 0.0f);
        this.cubes[3].addBox((float)(-w / 2 + 2), (float)(-n - 1), -1.0f, w - 4, n, 2, 0.0f);
        this.cubes[3].setPos(0.0f, (float)(0 + n2), (float)(-h / 2 + 1));
        this.cubes[4].addBox((float)(-w / 2 + 2), (float)(-n - 1), -1.0f, w - 4, n, 2, 0.0f);
        this.cubes[4].setPos(0.0f, (float)(0 + n2), (float)(h / 2 - 1));
        this.cubes[0].xRot = 1.5707964f;
        this.cubes[1].yRot = 4.712389f;
        this.cubes[2].yRot = 1.5707964f;
        this.cubes[3].yRot = Mth.PI;
        this.cubes[5].xRot = -1.5707964f;
    }
    
    @Override
    public void render(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
        this.cubes[5].y = 4.0f - bob;
        for (int i = 0; i < 6; ++i) {
            this.cubes[i].render(scale);
        }
    }
    
    @Override
    public void setupAnim(final float time, final float r, final float bob, final float yRot, final float xRot, final float scale) {
    }
}
