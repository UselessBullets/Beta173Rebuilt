// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.biome;

import java.util.Arrays;
import net.minecraft.world.level.ChunkPos;

public class FixedBiomeSource extends BiomeSource
{
    private Biome e;
    private double f;
    private double g;
    
    public FixedBiomeSource(final Biome gs, final double double2, final double double3) {
        this.e = gs;
        this.f = double2;
        this.g = double3;
    }
    
    @Override
    public Biome getBiome(final ChunkPos chunkPos) {
        return this.e;
    }
    
    @Override
    public Biome getBiome(final int x, final int z) {
        return this.e;
    }
    
    @Override
    public Biome[] getBiomeBlock(final int x, final int z, final int w, final int h) {
        return this.biomes = this.getBiomeBlock(this.biomes, x, z, w, h);
    }
    
    @Override
    public double[] getTemperatureBlock(double[] result, final int x, final int z, final int w, final int h) {
        if (result == null || result.length < w * h) {
            result = new double[w * h];
        }
        Arrays.fill(result, 0, w * h, this.f);
        return result;
    }
    
    @Override
    public Biome[] getBiomeBlock(Biome[] result, final int x, final int z, final int w, final int h) {
        if (result == null || result.length < w * h) {
            result = new Biome[w * h];
        }
        if (this.temperatures == null || this.temperatures.length < w * h) {
            this.temperatures = new double[w * h];
            this.downfalls = new double[w * h];
        }
        Arrays.fill(result, 0, w * h, this.e);
        Arrays.fill(this.downfalls, 0, w * h, this.g);
        Arrays.fill(this.temperatures, 0, w * h, this.f);
        return result;
    }
}
