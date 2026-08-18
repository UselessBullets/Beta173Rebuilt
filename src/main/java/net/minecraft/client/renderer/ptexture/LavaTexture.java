// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import util.Mth;
import net.minecraft.world.level.tile.Tile;

public class LavaTexture extends DynamicTexture
{
    protected float[] current = new float[16 * 16];
    protected float[] next = new float[16 * 16];
    protected float[] heat = new float[16 * 16];
    protected float[] heata = new float[16 * 16];
    
    public LavaTexture() {
        super(Tile.lava.tex);
    }
    
    @Override
    public void tick() {
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                float pow = 0.0f;
                final int xxo = (int)(Mth.sin(y * Mth.PI * 2.0f / 16.0f) * 1.2f);
                final int yyo = (int)(Mth.sin(x * Mth.PI * 2.0f / 16.0f) * 1.2f);

                for (int xx = x - 1; xx <= x + 1; ++xx) {
                    for (int yy = y - 1; yy <= y + 1; ++yy) {
                        int xi = xx + xxo & 0xF;
                        int yi = yy + yyo & 0xF;
                        pow += this.current[xi + yi * 16];
                    }
                }
                this.next[x + y * 16] = pow / 10.0f +
                        (
                                + this.heat[(x + 0 & 0xF) + (y + 0 & 0xF) * 16]
                                + this.heat[(x + 1 & 0xF) + (y + 0 & 0xF) * 16]
                                + this.heat[(x + 1 & 0xF) + (y + 1 & 0xF) * 16]
                                + this.heat[(x + 0 & 0xF) + (y + 1 & 0xF) * 16]
                        )
                                / 4.0f
                                * 0.8f;
                this.heat[x + y * 16] += this.heata[x + y * 16] * 0.01f;
                if (this.heat[x + y * 16] < 0.0f) this.heat[x + y * 16] = 0.0f;

                this.heata[x + y * 16] -= 0.06f;
                if (Math.random() < 0.005) this.heata[x + y * 16] = 1.5f;
            }
        }

        final float[] tmp = this.next;
        this.next = this.current;
        this.current = tmp;

        for (int i = 0; i < (16 * 16); ++i) {
            float pow = this.current[i] * 2.0f;
            if (pow > 1.0f) pow = 1.0f;
            if (pow < 0.0f) pow = 0.0f;

            final float pp = pow;
            int r = (int)(pp * 100.0f + 155.0f);
            int g = (int)(pp * pp * 255.0f);
            int b = (int)(pp * pp * pp * pp * 128.0f);
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
            this.pixels[i * 4 + 3] = (byte)255;
        }
    }
}
