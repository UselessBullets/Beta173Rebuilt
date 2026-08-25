// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.biome;

import java.util.Arrays;
import net.minecraft.world.level.ChunkPos;

public class FixedBiomeSource extends BiomeSource
{
    private Biome biome;
    private double temperature;
    private double downfall;
    
    public FixedBiomeSource(final Biome biome, final double temperature, final double downfall) {
        this.biome = biome;
        this.temperature = temperature;
        this.downfall = downfall;
    }
    
    @Override
    public Biome getBiome(final ChunkPos chunkPos) {
        return this.biome;
    }
    
    @Override
    public Biome getBiome(final int x, final int z) {
        return this.biome;
    }
    
    @Override
    public double getTemperature(final int x, final int z) {
        return this.temperature;
    }
    
    @Override
    public Biome[] getBiomeBlock(final int x, final int z, final int w, final int h) {
        return this.biomes = this.getBiomeBlock(this.biomes, x, z, w, h);
    }
    
    @Override
    public double[] getTemperatureBlock(double[] temperatures, final int x, final int z, final int w, final int h) {
        if (temperatures == null || temperatures.length < w * h) {
            temperatures = new double[w * h];
        }
        Arrays.fill(temperatures, 0, w * h, this.temperature);
        return temperatures;
    }

    @Override
    // Useless - Existed in b1.2 and LCE leaks
    public double[] getDownfallBlock(double[] downfalls, int x, int z, int w, int h) {
        if (downfalls == null || downfalls.length < w * h) {
            downfalls = new double[w * h];
        }

        Arrays.fill(downfalls, 0, w * h, this.downfall);
        return downfalls;
    }
    
    @Override
    public Biome[] getBiomeBlock(Biome[] biomes, final int x, final int z, final int w, final int h) {
        if (biomes == null || biomes.length < w * h) {
            biomes = new Biome[w * h];
        }
        if (this.temperatures == null || this.temperatures.length < w * h) {
            this.temperatures = new double[w * h];
            this.downfalls = new double[w * h];
        }
        Arrays.fill(biomes, 0, w * h, this.biome);
        Arrays.fill(this.downfalls, 0, w * h, this.downfall);
        Arrays.fill(this.temperatures, 0, w * h, this.temperature);
        return biomes;
    }
}
