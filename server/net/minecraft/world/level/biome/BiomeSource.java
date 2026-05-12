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
    
    public Biome[] getBiomeBlock(final int x, final int z, final int w, final int h) {
        return this.biomes = this.getBiomeBlock(this.biomes, x, z, w, h);
    }
    
    public double[] getTemperatureBlock(double[] result, final int x, final int z, final int w, final int h) {
        if (result == null || result.length < w * h) {
            result = new double[w * h];
        }
        result = this.temperatureMap.getRegion(result, x, z, w, h, 0.02500000037252903, 0.02500000037252903, 0.25);
        this.noises = this.noiseMap.getRegion(this.noises, x, z, w, h, 0.25, 0.25, 0.5882352941176471);
        int n = 0;
        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                final double n2 = this.noises[n] * 1.1 + 0.5;
                final double n3 = 0.01;
                final double n4 = (result[n] * 0.15 + 0.7) * (1.0 - n3) + n2 * n3;
                double n5 = 1.0 - (1.0 - n4) * (1.0 - n4);
                if (n5 < 0.0) {
                    n5 = 0.0;
                }
                if (n5 > 1.0) {
                    n5 = 1.0;
                }
                result[n] = n5;
                ++n;
            }
        }
        return result;
    }
    
    public Biome[] getBiomeBlock(Biome[] result, final int x, final int z, final int w, final int h) {
        if (result == null || result.length < w * h) {
            result = new Biome[w * h];
        }
        this.temperatures = this.temperatureMap.getRegion(this.temperatures, x, z, w, w, 0.02500000037252903, 0.02500000037252903, 0.25);
        this.downfalls = this.downfallMap.getRegion(this.downfalls, x, z, w, w, 0.05000000074505806, 0.05000000074505806, 0.3333333333333333);
        this.noises = this.noiseMap.getRegion(this.noises, x, z, w, w, 0.25, 0.25, 0.5882352941176471);
        int n = 0;
        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                final double n2 = this.noises[n] * 1.1 + 0.5;
                final double n3 = 0.01;
                final double n4 = (this.temperatures[n] * 0.15 + 0.7) * (1.0 - n3) + n2 * n3;
                final double n5 = 0.002;
                double downfall = (this.downfalls[n] * 0.15 + 0.5) * (1.0 - n5) + n2 * n5;
                double temp = 1.0 - (1.0 - n4) * (1.0 - n4);
                if (temp < 0.0) {
                    temp = 0.0;
                }
                if (downfall < 0.0) {
                    downfall = 0.0;
                }
                if (temp > 1.0) {
                    temp = 1.0;
                }
                if (downfall > 1.0) {
                    downfall = 1.0;
                }
                this.temperatures[n] = temp;
                this.downfalls[n] = downfall;
                result[n++] = Biome.getBiome(temp, downfall);
            }
        }
        return result;
    }
}
