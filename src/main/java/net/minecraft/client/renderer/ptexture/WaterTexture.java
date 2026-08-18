// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import net.minecraft.world.level.tile.Tile;

public class WaterTexture extends DynamicTexture
{
    protected float[] current = new float[16 * 16];
    protected float[] next = new float[16 * 16];
    protected float[] heat = new float[16 * 16];
    protected float[] heata = new float[16 * 16];
    private int tickCount = 0;
    
    public WaterTexture() {
        super(Tile.water.tex);
    }
    
    @Override
    public void tick() {
        ++this.tickCount;

        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                float pow = 0.0f;

                for (int xx = x - 1; xx <= x + 1; ++xx) {
                    int xi = xx & 0xF;
                    int yi = y & 0xF;
                    pow += this.current[xi + yi * 16];
                }

                this.next[x + y * 16] = pow / 3.3f + this.heat[x + y * 16] * 0.8f;
            }
        }
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                this.heat[x + y * 16] += this.heata[x + y * 16] * 0.05f;
                if (this.heat[x + y * 16] < 0.0f) this.heat[x + y * 16] = 0.0f;

                this.heata[x + y * 16] -= 0.1f;
                if (Math.random() < 0.05) this.heata[x + y * 16] = 0.5f;
            }
        }

        final float[] tmp = this.next;
        this.next = this.current;
        this.current = tmp;

        for (int i = 0; i < (16 * 16); ++i) {
            float pow = this.current[i];
            if (pow > 1.0f) pow = 1.0f;
            if (pow < 0.0f) pow = 0.0f;

            final float pp = pow * pow;
            int r = (int)(32.0f + pp * 32.0f);
            int g = (int)(50.0f + pp * 64.0f);
            int b = 255;
            final int a = (int)(146.0f + pp * 50.0f);
            if (this.anaglyph3d) {
                final int rr = (r * 30 + g * 59 + b * 11) / 100;
                final int gg = (r * 30 + g * 70) / 100;
                final int bb = (r * 30 + b * 70) / 100;
                r = rr;
                g = gg;
                b = bb;
            }

            this.pixels[i * 4 + 0] = (byte)r;
            this.pixels[i * 4 + 1] = (byte)g;
            this.pixels[i * 4 + 2] = (byte)b;
            this.pixels[i * 4 + 3] = (byte)a;
        }
    }
}
