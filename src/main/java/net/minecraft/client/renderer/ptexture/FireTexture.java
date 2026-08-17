// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import net.minecraft.world.level.tile.Tile;

public class FireTexture extends DynamicTexture
{
    protected float[] current;
    protected float[] next;
    
    public FireTexture(final int tex) {
        super(Tile.fire.tex + tex * 16);
        this.current = new float[320];
        this.next = new float[320];
    }
    
    @Override
    public void tick() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 20; ++j) {
                int n = 18;
                float n2 = this.current[i + (j + 1) % 20 * 16] * n;
                for (int k = i - 1; k <= i + 1; ++k) {
                    for (int l = j; l <= j + 1; ++l) {
                        final int n3 = k;
                        final int n4 = l;
                        if (n3 >= 0 && n4 >= 0 && n3 < 16 && n4 < 20) {
                            n2 += this.current[n3 + n4 * 16];
                        }
                        ++n;
                    }
                }
                this.next[i + j * 16] = n2 / (n * 1.06f);
                if (j >= 19) {
                    this.next[i + j * 16] = (float)(Math.random() * Math.random() * Math.random() * 4.0 + Math.random() * 0.1f + 0.2f);
                }
            }
        }
        final float[] next = this.next;
        this.next = this.current;
        this.current = next;
        for (int n5 = 0; n5 < 256; ++n5) {
            float n6 = this.current[n5] * 1.8f;
            if (n6 > 1.0f) {
                n6 = 1.0f;
            }
            if (n6 < 0.0f) {
                n6 = 0.0f;
            }
            final float n7 = n6;
            int n8 = (int)(n7 * 155.0f + 100.0f);
            int n9 = (int)(n7 * n7 * 255.0f);
            int n10 = (int)(n7 * n7 * n7 * n7 * n7 * n7 * n7 * n7 * n7 * n7 * 255.0f);
            int n11 = 255;
            if (n7 < 0.5f) {
                n11 = 0;
            }
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
