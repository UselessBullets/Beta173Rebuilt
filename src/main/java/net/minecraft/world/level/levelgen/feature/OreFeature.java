// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.feature;

import net.minecraft.world.level.tile.Tile;
import util.Mth;
import java.util.Random;
import net.minecraft.world.level.Level;

public class OreFeature extends Feature
{
    private int tile;
    private int count;
    
    public OreFeature(final int tile, final int count) {
        this.tile = tile;
        this.count = count;
    }
    
    @Override
    public boolean place(final Level level, final Random random, final int x, final int y, final int z) {
        final float dir = random.nextFloat() * Mth.PI;

        final double x0 = x + 8 + Mth.sin(dir) * this.count / 8.0f;
        final double x1 = x + 8 - Mth.sin(dir) * this.count / 8.0f;
        final double z0 = z + 8 + Mth.cos(dir) * this.count / 8.0f;
        final double z1 = z + 8 - Mth.cos(dir) * this.count / 8.0f;

        final double y0 = y + random.nextInt(3) + 2;
        final double y1 = y + random.nextInt(3) + 2;

        for (int d = 0; d <= this.count; ++d) {
            final double xx = x0 + (x1 - x0) * d / this.count;
            final double yy = y0 + (y1 - y0) * d / this.count;
            final double zz = z0 + (z1 - z0) * d / this.count;

            final double ss = random.nextDouble() * this.count / 16.0;
            final double r = (Mth.sin(d * Mth.PI / this.count) + 1.0f) * ss + 1.0;
            final double hr = (Mth.sin(d * Mth.PI / this.count) + 1.0f) * ss + 1.0;

            final int xt0 = Mth.floor(xx - r / 2.0);
            final int yt0 = Mth.floor(yy - hr / 2.0);
            final int zt0 = Mth.floor(zz - r / 2.0);

            final int xt1 = Mth.floor(xx + r / 2.0);
            final int yt1 = Mth.floor(yy + hr / 2.0);
            final int zt1 = Mth.floor(zz + r / 2.0);

            for (int x2 = xt0; x2 <= xt1; ++x2) {
                final double xd = (x2 + 0.5 - xx) / (r / 2.0);
                if (xd * xd < 1.0) {
                    for (int y2 = yt0; y2 <= yt1; ++y2) {
                        final double yd = (y2 + 0.5 - yy) / (hr / 2.0);
                        if (xd * xd + yd * yd < 1.0) {
                            for (int z2 = zt0; z2 <= zt1; ++z2) {
                                final double zd = (z2 + 0.5 - zz) / (r / 2.0);
                                if (xd * xd + yd * yd + zd * zd < 1.0) {
                                    if (level.getTile(x2, y2, z2) == Tile.rock.id) {
                                        level.setTileNoUpdate(x2, y2, z2, this.tile);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
