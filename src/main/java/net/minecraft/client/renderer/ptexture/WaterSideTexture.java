// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import net.minecraft.world.level.tile.Tile;

public class WaterSideTexture extends DynamicTexture
{
    protected float[] current;
    protected float[] next;
    protected float[] heat;
    protected float[] heata;
    private int tickCount;
    
    public WaterSideTexture() {
        super(Tile.water.tex + 1);
        this.current = new float[256];
        this.next = new float[256];
        this.heat = new float[256];
        this.heata = new float[256];
        this.tickCount = 0;
        this.replicate = 2;
    }
    
    @Override
    public void tick() {
        ++this.tickCount;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                float n = 0.0f;
                for (int k = j - 2; k <= j; ++k) {
                    n += this.current[(i & 0xF) + (k & 0xF) * 16];
                }
                this.next[i + j * 16] = n / 3.2f + this.heat[i + j * 16] * 0.8f;
            }
        }
        for (int l = 0; l < 16; ++l) {
            for (int n2 = 0; n2 < 16; ++n2) {
                final float[] heat = this.heat;
                final int n3 = l + n2 * 16;
                heat[n3] += this.heata[l + n2 * 16] * 0.05f;
                if (this.heat[l + n2 * 16] < 0.0f) {
                    this.heat[l + n2 * 16] = 0.0f;
                }
                final float[] heata = this.heata;
                final int n4 = l + n2 * 16;
                heata[n4] -= 0.3f;
                if (Math.random() < 0.2) {
                    this.heata[l + n2 * 16] = 0.5f;
                }
            }
        }
        final float[] next = this.next;
        this.next = this.current;
        this.current = next;
        for (int n5 = 0; n5 < 256; ++n5) {
            float n6 = this.current[n5 - this.tickCount * 16 & 0xFF];
            if (n6 > 1.0f) {
                n6 = 1.0f;
            }
            if (n6 < 0.0f) {
                n6 = 0.0f;
            }
            final float n7 = n6 * n6;
            int n8 = (int)(32.0f + n7 * 32.0f);
            int n9 = (int)(50.0f + n7 * 64.0f);
            int n10 = 255;
            final int n11 = (int)(146.0f + n7 * 50.0f);
            if (this.anaglyph3d) {
                final int n12 = (n8 * 30 + n9 * 59 + n10 * 11) / 100;
                final int n13 = (n8 * 30 + n9 * 70) / 100;
                final int n14 = (n8 * 30 + n10 * 70) / 100;
                n8 = n12;
                n9 = n13;
                n10 = n14;
            }
            this.pixels[n5 * 4 + 0] = (byte)n8;
            this.pixels[n5 * 4 + 1] = (byte)n9;
            this.pixels[n5 * 4 + 2] = (byte)n10;
            this.pixels[n5 * 4 + 3] = (byte)n11;
        }
    }
}
