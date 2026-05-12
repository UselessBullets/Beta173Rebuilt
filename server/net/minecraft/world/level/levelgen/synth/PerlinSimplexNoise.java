// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

public class PerlinSimplexNoise extends Synth
{
    private SimplexNoise[] noiseLevels;
    private int levels;
    
    public PerlinSimplexNoise(final Random random, final int levels) {
        this.levels = levels;
        this.noiseLevels = new SimplexNoise[levels];
        for (int i = 0; i < levels; ++i) {
            this.noiseLevels[i] = new SimplexNoise(random);
        }
    }
    
    public double[] getRegion(final double[] buffer, final double x, final double y, final int xSize, final int ySize, final double zSize, final double yScale, final double sizeScale) {
        return this.getRegion(buffer, x, y, xSize, ySize, zSize, yScale, sizeScale, 0.5);
    }
    
    public double[] getRegion(double[] buffer, final double x, final double y, final int xSize, final int ySize, double xScale, double yScale, final double sizeScale, final double powScale) {
        xScale /= 1.5;
        yScale /= 1.5;
        if (buffer == null || buffer.length < xSize * ySize) {
            buffer = new double[xSize * ySize];
        }
        else {
            for (int i = 0; i < buffer.length; ++i) {
                buffer[i] = 0.0;
            }
        }
        double n = 1.0;
        double n2 = 1.0;
        for (int j = 0; j < this.levels; ++j) {
            this.noiseLevels[j].add(buffer, x, y, xSize, ySize, xScale * n2, yScale * n2, 0.55 / n);
            n2 *= sizeScale;
            n *= powScale;
        }
        return buffer;
    }
}
