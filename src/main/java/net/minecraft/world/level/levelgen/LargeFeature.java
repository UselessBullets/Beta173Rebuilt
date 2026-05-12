// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import java.util.Random;

public class LargeFeature
{
    protected int radius;
    protected Random random;
    
    public LargeFeature() {
        this.radius = 8;
        this.random = new Random();
    }
    
    public void apply(final ChunkSource chunkSource, final Level level, final int xOffs, final int zOffs, final byte[] blocks) {
        final int radius = this.radius;
        this.random.setSeed(level.getSeed());
        final long n = this.random.nextLong() / 2L * 2L + 1L;
        final long n2 = this.random.nextLong() / 2L * 2L + 1L;
        for (int i = xOffs - radius; i <= xOffs + radius; ++i) {
            for (int j = zOffs - radius; j <= zOffs + radius; ++j) {
                this.random.setSeed(i * n + j * n2 ^ level.getSeed());
                this.addFeature(level, i, j, xOffs, zOffs, blocks);
            }
        }
    }
    
    protected void addFeature(final Level level, final int x, final int z, final int xOffs, final int zOffs, final byte[] blocks) {
    }
}
