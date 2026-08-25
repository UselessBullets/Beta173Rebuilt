// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import java.util.Random;

public class LargeFeature
{
    protected int radius = 8;
    protected Random random = new Random();

    public void apply(final ChunkSource chunkSource, final Level level, final int xOffs, final int zOffs, final byte[] blocks) {
        final int r = this.radius;

        this.random.setSeed(level.getSeed());
        final long xScale = this.random.nextLong() / 2L * 2L + 1L;
        final long zScale = this.random.nextLong() / 2L * 2L + 1L;

        for (int x = xOffs - r; x <= xOffs + r; ++x) {
            for (int z = zOffs - r; z <= zOffs + r; ++z) {
                long xx = x * xScale;
                long zz = z * zScale;
                this.random.setSeed(xx + zz ^ level.getSeed());
                this.addFeature(level, x, z, xOffs, zOffs, blocks);
            }
        }
    }
    
    protected void addFeature(final Level level, final int x, final int z, final int xOffs, final int zOffs, final byte[] blocks) {
    }
}
