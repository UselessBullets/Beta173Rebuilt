// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import util.Mth;
import java.util.Random;
import net.minecraft.world.level.tile.Tile;

public class PortalTexture extends DynamicTexture
{
    private int time;
    private byte[][] frames;
    
    public PortalTexture() {
        super(Tile.portalTile.tex);
        this.time = 0;
        this.frames = new byte[32][1024];
        final Random random = new Random(100L);
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    float n = 0.0f;
                    for (int l = 0; l < 2; ++l) {
                        final float n2 = (float)(l * 8);
                        final float n3 = (float)(l * 8);
                        float n4 = (j - n2) / 16.0f * 2.0f;
                        float n5 = (k - n3) / 16.0f * 2.0f;
                        if (n4 < -1.0f) {
                            n4 += 2.0f;
                        }
                        if (n4 >= 1.0f) {
                            n4 -= 2.0f;
                        }
                        if (n5 < -1.0f) {
                            n5 += 2.0f;
                        }
                        if (n5 >= 1.0f) {
                            n5 -= 2.0f;
                        }
                        final float n6 = n4 * n4 + n5 * n5;
                        n += (Mth.sin((float)Math.atan2(n5, n4) + (i / 32.0f * Mth.PI * 2.0f - n6 * 10.0f + l * 2) * (l * 2 - 1)) + 1.0f) / 2.0f / (n6 + 1.0f) * 0.5f;
                    }
                    final float n7 = n + random.nextFloat() * 0.1f;
                    final int n8 = (int)(n7 * 100.0f + 155.0f);
                    final int n9 = (int)(n7 * n7 * 200.0f + 55.0f);
                    final int n10 = (int)(n7 * n7 * n7 * n7 * 255.0f);
                    final int n11 = (int)(n7 * 100.0f + 155.0f);
                    final int n12 = k * 16 + j;
                    this.frames[i][n12 * 4 + 0] = (byte)n9;
                    this.frames[i][n12 * 4 + 1] = (byte)n10;
                    this.frames[i][n12 * 4 + 2] = (byte)n8;
                    this.frames[i][n12 * 4 + 3] = (byte)n11;
                }
            }
        }
    }
    
    @Override
    public void tick() {
        ++this.time;
        final byte[] array = this.frames[this.time & 0x1F];
        for (int i = 0; i < 256; ++i) {
            int n = array[i * 4 + 0] & 0xFF;
            int n2 = array[i * 4 + 1] & 0xFF;
            int n3 = array[i * 4 + 2] & 0xFF;
            final int n4 = array[i * 4 + 3] & 0xFF;
            if (this.anaglyph3d) {
                final int n5 = (n * 30 + n2 * 59 + n3 * 11) / 100;
                final int n6 = (n * 30 + n2 * 70) / 100;
                final int n7 = (n * 30 + n3 * 70) / 100;
                n = n5;
                n2 = n6;
                n3 = n7;
            }
            this.pixels[i * 4 + 0] = (byte)n;
            this.pixels[i * 4 + 1] = (byte)n2;
            this.pixels[i * 4 + 2] = (byte)n3;
            this.pixels[i * 4 + 3] = (byte)n4;
        }
    }
}
