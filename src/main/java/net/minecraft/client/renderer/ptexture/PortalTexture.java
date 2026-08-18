// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.ptexture;

import util.Mth;
import java.util.Random;
import net.minecraft.world.level.tile.Tile;

public class PortalTexture extends DynamicTexture
{
    private int time = 0;
    private byte[][] frames = new byte[32][16 * 16 * 4];
    
    public PortalTexture() {
        super(Tile.portalTile.tex);
        final Random random = new Random(100L);
        for (int time = 0; time < 32; ++time) {
            for (int x = 0; x < 16; ++x) {
                for (int y = 0; y < 16; ++y) {
                    float pow = 0.0f;

                    for (int i = 0; i < 2; ++i) {
                        final float xo = (float)(i * 8);
                        final float yo = (float)(i * 8);
                        float xd = (x - xo) / 16.0f * 2.0f;
                        float yd = (y - yo) / 16.0f * 2.0f;
                        if (xd < -1.0f) xd += 2.0f;
                        if (xd >= 1.0f) xd -= 2.0f;
                        if (yd < -1.0f) yd += 2.0f;
                        if (yd >= 1.0f) yd -= 2.0f;

                        final float dd = xd * xd + yd * yd;
                        float pp = (float)Math.atan2(yd, xd) + (time / 32.0f * Mth.PI * 2.0f - dd * 10.0f + i * 2) * (i * 2 - 1);
                        pp = (Mth.sin(pp) + 1.0f) / 2.0f;
                        pp /= (dd + 1.0f);
                        pow += pp * 0.5f;
                    }

                    pow += random.nextFloat() * 0.1f;
                    final int b = (int)(pow * 100.0f + 155.0f);
                    final int r = (int)(pow * pow * 200.0f + 55.0f);
                    final int g = (int)(pow * pow * pow * pow * 255.0f);
                    final int a = (int)(pow * 100.0f + 155.0f);
                    final int i = y * 16 + x;
                    this.frames[time][i * 4 + 0] = (byte)r;
                    this.frames[time][i * 4 + 1] = (byte)g;
                    this.frames[time][i * 4 + 2] = (byte)b;
                    this.frames[time][i * 4 + 3] = (byte)a;
                }
            }
        }
    }
    
    @Override
    public void tick() {
        ++this.time;
        final byte[] source = this.frames[this.time & 0x1F];

        for (int i = 0; i < (16 * 16); ++i) {
            int r = source[i * 4 + 0] & 0xFF;
            int g = source[i * 4 + 1] & 0xFF;
            int b = source[i * 4 + 2] & 0xFF;
            int a = source[i * 4 + 3] & 0xFF;
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
