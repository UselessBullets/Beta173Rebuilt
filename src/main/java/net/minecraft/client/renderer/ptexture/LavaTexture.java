// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import util.Mth;
import net.minecraft.world.level.tile.Tile;

public class LavaTexture extends DynamicTexture
{
    protected float[] current;
    protected float[] next;
    protected float[] heat;
    protected float[] heata;
    
    public LavaTexture() {
        super(Tile.lava.tex);
        this.current = new float[256];
        this.next = new float[256];
        this.heat = new float[256];
        this.heata = new float[256];
    }
    
    @Override
    public void tick() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                float n = 0.0f;
                final int n2 = (int)(Mth.sin(j * 3.1415927f * 2.0f / 16.0f) * 1.2f);
                final int n3 = (int)(Mth.sin(i * 3.1415927f * 2.0f / 16.0f) * 1.2f);
                for (int k = i - 1; k <= i + 1; ++k) {
                    for (int l = j - 1; l <= j + 1; ++l) {
                        n += this.current[(k + n2 & 0xF) + (l + n3 & 0xF) * 16];
                    }
                }
                this.next[i + j * 16] = n / 10.0f + (this.heat[(i + 0 & 0xF) + (j + 0 & 0xF) * 16] + this.heat[(i + 1 & 0xF) + (j + 0 & 0xF) * 16] + this.heat[(i + 1 & 0xF) + (j + 1 & 0xF) * 16] + this.heat[(i + 0 & 0xF) + (j + 1 & 0xF) * 16]) / 4.0f * 0.8f;
                final float[] heat = this.heat;
                final int n4 = i + j * 16;
                heat[n4] += this.heata[i + j * 16] * 0.01f;
                if (this.heat[i + j * 16] < 0.0f) {
                    this.heat[i + j * 16] = 0.0f;
                }
                final float[] heata = this.heata;
                final int n5 = i + j * 16;
                heata[n5] -= 0.06f;
                if (Math.random() < 0.005) {
                    this.heata[i + j * 16] = 1.5f;
                }
            }
        }
        final float[] next = this.next;
        this.next = this.current;
        this.current = next;
        for (int n6 = 0; n6 < 256; ++n6) {
            float n7 = this.current[n6] * 2.0f;
            if (n7 > 1.0f) {
                n7 = 1.0f;
            }
            if (n7 < 0.0f) {
                n7 = 0.0f;
            }
            final float n8 = n7;
            int n9 = (int)(n8 * 100.0f + 155.0f);
            int n10 = (int)(n8 * n8 * 255.0f);
            int n11 = (int)(n8 * n8 * n8 * n8 * 128.0f);
            if (this.anaglyph3d) {
                final int n12 = (n9 * 30 + n10 * 59 + n11 * 11) / 100;
                final int n13 = (n9 * 30 + n10 * 70) / 100;
                final int n14 = (n9 * 30 + n11 * 70) / 100;
                n9 = n12;
                n10 = n13;
                n11 = n14;
            }
            this.pixels[n6 * 4 + 0] = (byte)n9;
            this.pixels[n6 * 4 + 1] = (byte)n10;
            this.pixels[n6 * 4 + 2] = (byte)n11;
            this.pixels[n6 * 4 + 3] = -1;
        }
    }
}
