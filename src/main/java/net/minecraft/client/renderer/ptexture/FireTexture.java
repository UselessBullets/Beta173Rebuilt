// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import net.minecraft.world.level.tile.Tile;

public class FireTexture extends DynamicTexture
{
    protected float[] current = new float[20 * 16];
    protected float[] next = new float[20 * 16];
    
    public FireTexture(final int tex) {
        super(Tile.fire.tex + tex * 16);
    }
    
    @Override
    public void tick() {
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 20; ++y) {
                int count = 18;
                float pow = this.current[x + (y + 1) % 20 * 16] * count;

                for (int xx = x - 1; xx <= x + 1; ++xx) {
                    for (int yy = y; yy <= y + 1; ++yy) {
                        final int xi = xx;
                        final int yi = yy;
                        if (xi >= 0 && yi >= 0 && xi < 16 && yi < 20) {
                            pow += this.current[xi + yi * 16];
                        }

                        ++count;
                    }
                }

                this.next[x + y * 16] = pow / (count * 1.06f);
                if (y >= 19) {
                    this.next[x + y * 16] = (float)(Math.random() * Math.random() * Math.random() * 4.0 + Math.random() * 0.1f + 0.2f);
                }
            }
        }

        final float[] tmp = this.next;
        this.next = this.current;
        this.current = tmp;

        for (int i = 0; i < (16 * 16); ++i) {
            float pow = this.current[i] * 1.8f;
            if (pow > 1.0f) pow = 1.0f;
            if (pow < 0.0f) pow = 0.0f;

            final float pp = pow;
            int r = (int)(pp * 155.0f + 100.0f);
            int g = (int)(pp * pp * 255.0f);
            int b = (int)(pp * pp * pp * pp * pp * pp * pp * pp * pp * pp * 255.0f);
            int a = 255;
            if (pp < 0.5f) a = 0;

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
