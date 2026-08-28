// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.biome;

import net.minecraft.world.level.ChunkPos;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class BiomeSource
{
    private PerlinSimplexNoise temperatureMap;
    private PerlinSimplexNoise downfallMap;
    private PerlinSimplexNoise noiseMap;
    public double[] temperatures;
    public double[] downfalls;
    public double[] noises;
    public Biome[] biomes;
    
    protected BiomeSource() {
    }
    
    public BiomeSource(final Level level) {
        this.temperatureMap = new PerlinSimplexNoise(new Random(level.getSeed() * 9871L), 4);
        this.downfallMap = new PerlinSimplexNoise(new Random(level.getSeed() * 39811L), 4);
        this.noiseMap = new PerlinSimplexNoise(new Random(level.getSeed() * 543321L), 2);
    }
    
    public Biome getBiome(final ChunkPos chunkPos) {
        return this.getBiome(chunkPos.x << 4, chunkPos.z << 4);
    }
    
    public Biome getBiome(final int x, final int z) {
        return this.getBiomeBlock(x, z, 1, 1)[0];
    }
    
    public double getTemperature(final int x, final int z) {
        this.temperatures = this.temperatureMap.getRegion(this.temperatures, x, z, 1, 1, 0.025f, 0.025f, 0.5);
        return this.temperatures[0];
    }
    
    public Biome[] getBiomeBlock(final int x, final int z, final int w, final int h) {
        return this.biomes = this.getBiomeBlock(this.biomes, x, z, w, h);
    }
    
    public double[] getTemperatureBlock(double[] temperatures, final int x, final int z, final int w, final int h) {
        if (temperatures == null || temperatures.length < w * h) {
            temperatures = new double[w * h];
        }

        temperatures = this.temperatureMap.getRegion(temperatures, x, z, w, h, 0.025f, 0.025f, 0.25);
        this.noises = this.noiseMap.getRegion(this.noises, x, z, w, h, 0.25, 0.25, 10 / 17.0);
        int i = 0;

        for (int _x = 0; _x < w; ++_x) {
            for (int _z = 0; _z < h; ++_z) {
                // Useless - TODO Cannot find information on these local vars
                final double n2 = this.noises[i] * 1.1 + 0.5;
                final double n3 = 0.01;
                final double n4 = (temperatures[i] * 0.15 + 0.7) * (1.0 - n3) + n2 * n3;
                double t = 1.0 - (1.0 - n4) * (1.0 - n4);
                if (t < 0.0) t = 0.0;
                if (t > 1.0) t = 1.0;
                temperatures[i] = t;
                i++;
            }
        }
        return temperatures;
    }

    // Useless - Existed in b1.2 and LCE leaks
    public double[] getDownfallBlock(double[] downfalls, int x, int z, int w, int h) {
        if (downfalls == null || downfalls.length < w * h) {
            downfalls = new double[w * h];
        }

        return this.downfallMap.getRegion(downfalls, x, z, w, w, 0.05F, 0.05F, 0.5);
    }
    
    public Biome[] getBiomeBlock(Biome[] biomes, final int x, final int z, final int w, final int h) {
        if (biomes == null || biomes.length < w * h) {
            biomes = new Biome[w * h];
        }

        this.temperatures = this.temperatureMap.getRegion(this.temperatures, x, z, w, w, 0.025f, 0.025f, 0.25);
        this.downfalls = this.downfallMap.getRegion(this.downfalls, x, z, w, w, 0.05f, 0.05f, 1 / 3.0);
        this.noises = this.noiseMap.getRegion(this.noises, x, z, w, w, 0.25, 0.25, 10 / 17.0);
        int i = 0;

        for (int _x = 0; _x < w; ++_x) {
            for (int _z = 0; _z < h; ++_z) {
                // Useless - Cannot find information on these local vars
                final double n2 = this.noises[i] * 1.1 + 0.5;
                final double n3 = 0.01;
                final double n4 = (this.temperatures[i] * 0.15 + 0.7) * (1.0 - n3) + n2 * n3;
                final double n5 = 0.002;
                double downfall = (this.downfalls[i] * 0.15 + 0.5) * (1.0 - n5) + n2 * n5;
                double temp = 1.0 - (1.0 - n4) * (1.0 - n4);
                if (temp < 0.0) temp = 0.0;
                if (downfall < 0.0) downfall = 0.0;
                if (temp > 1.0) temp = 1.0;
                if (downfall > 1.0) downfall = 1.0;
                this.temperatures[i] = temp;
                this.downfalls[i] = downfall;
                biomes[i++] = Biome.getBiome(temp, downfall);
            }
        }
        return biomes;
    }
}
