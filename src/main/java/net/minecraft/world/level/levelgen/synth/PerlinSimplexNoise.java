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

    @Override
    // Useless - Exists in b1.2 and LCE leaks
    public double getValue(double x, double y) {
        double value = 0.0;
        double pow = 1.0;

        for (int i = 0; i < this.levels; i++) {
            value += this.noiseLevels[i].getValue(x * pow, y * pow) / pow;
            pow /= 2.0;
        }

        return value;
    }

    // Useless - Exists in b1.2 and LCE leaks
    public double getValue(double x, double y, double z) {
        double value = 0.0;
        double pow = 1.0;

        for (int i = 0; i < this.levels; i++) {
            value += this.noiseLevels[i].getValue(x * pow, y * pow, z * pow) / pow;
            pow /= 2.0;
        }

        return value;
    }

    public double[] getRegion(final double[] buffer, final double x, final double y, final int xSize, final int ySize, final double xScale, final double yScale, final double sizeScale) {
        return this.getRegion(buffer, x, y, xSize, ySize, xScale, yScale, sizeScale, 0.5);
    }

    public double[] getRegion(double[] buffer, final double x, final double y, final int xSize, final int ySize, double xScale, double yScale, final double sizeScale, final double powScale) {
        xScale /= 1.5;
        yScale /= 1.5;

        if (buffer == null || buffer.length < xSize * ySize) buffer = new double[xSize * ySize];
        else for (int i = 0; i < buffer.length; ++i) buffer[i] = 0.0;

        double pow = 1.0;
        double scale = 1.0;
        for (int i = 0; i < this.levels; ++i) {
            this.noiseLevels[i].add(buffer, x, y, xSize, ySize, xScale * scale, yScale * scale, 0.55 / pow);
            scale *= sizeScale;
            pow *= powScale;
        }

        return buffer;
    }

    // Useless - Exists in b1.2 and LCE leaks
    public double[] getRegion(double[] buffer, double x, double y, double z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        xScale /= 1.5;
        yScale /= 1.5;

        if (buffer == null) buffer = new double[xSize * ySize * zSize];
        else for (int i = 0; i < buffer.length; i++) buffer[i] = 0.0;

        double pow = 1.0;

        for (int i = 0; i < this.levels; i++) {
//            value += noiseLevels[i].getValue(x * pow, y * pow, z * pow) / pow;
            this.noiseLevels[i].add(buffer, x, y, z, xSize, ySize, zSize, xScale * pow, yScale * pow, zScale * pow, 0.55 / pow);
            pow *= 0.5;
        }

        return buffer;
    }
}
