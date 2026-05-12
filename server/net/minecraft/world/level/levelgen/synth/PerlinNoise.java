// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

public class PerlinNoise extends Synth
{
    private ImprovedNoise[] noiseLevels;
    private int levels;
    
    public PerlinNoise(final Random random, final int levels) {
        this.levels = levels;
        this.noiseLevels = new ImprovedNoise[levels];
        for (int i = 0; i < levels; ++i) {
            this.noiseLevels[i] = new ImprovedNoise(random);
        }
    }
    
    public double getValue(final double x, final double y) {
        double n = 0.0;
        double n2 = 1.0;
        for (int i = 0; i < this.levels; ++i) {
            n += this.noiseLevels[i].getValue(x * n2, y * n2) / n2;
            n2 /= 2.0;
        }
        return n;
    }
    
    public double[] getRegion(double[] buffer, final double x, final double y, final double z, final int xSize, final int ySize, final int zSize, final double xScale, final double yScale, final double zScale) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        }
        else {
            for (int i = 0; i < buffer.length; ++i) {
                buffer[i] = 0.0;
            }
        }
        double pow = 1.0;
        for (int j = 0; j < this.levels; ++j) {
            this.noiseLevels[j].add(buffer, x, y, z, xSize, ySize, zSize, xScale * pow, yScale * pow, zScale * pow, pow);
            pow /= 2.0;
        }
        return buffer;
    }
    
    public double[] getRegion(final double[] ar, final int x, final int z, final int xSize, final int zSize, final double xScale, final double zScale, final double pow) {
        return this.getRegion(ar, x, 10.0, z, xSize, 1, zSize, xScale, 1.0, zScale);
    }
}
