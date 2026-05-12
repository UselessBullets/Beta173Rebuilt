// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen;

import util.ProgressListener;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.CactusFeature;
import net.minecraft.world.level.levelgen.feature.PumpkinFeature;
import net.minecraft.world.level.levelgen.feature.ReedsFeature;
import net.minecraft.world.level.levelgen.feature.DeadBushFeature;
import net.minecraft.world.level.levelgen.feature.TallGrassFeature;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.ClayFeature;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.tile.SandTile;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkSource;

public class RandomLevelSource implements ChunkSource
{
    private Random random;
    private PerlinNoise lperlinNoise1;
    private PerlinNoise lperlinNoise2;
    private PerlinNoise perlinNoise1;
    private PerlinNoise perlinNoise2;
    private PerlinNoise perlinNoise3;
    public PerlinNoise scaleNoise;
    public PerlinNoise depthNoise;
    public PerlinNoise forestNoise;
    private Level level;
    private double[] buffer;
    private double[] sandBuffer;
    private double[] gravelBuffer;
    private double[] depthBuffer;
    private LargeFeature caveFeature;
    private Biome[] biomes;
    double[] pnr;
    double[] ar;
    double[] br;
    double[] sr;
    double[] dr;
    int[][] waterDepths;
    private double[] temperatures;
    
    public RandomLevelSource(final Level level, final long seed) {
        this.sandBuffer = new double[256];
        this.gravelBuffer = new double[256];
        this.depthBuffer = new double[256];
        this.caveFeature = new LargeCaveFeature();
        this.waterDepths = new int[32][32];
        this.level = level;
        this.random = new Random(seed);
        this.lperlinNoise1 = new PerlinNoise(this.random, 16);
        this.lperlinNoise2 = new PerlinNoise(this.random, 16);
        this.perlinNoise1 = new PerlinNoise(this.random, 8);
        this.perlinNoise2 = new PerlinNoise(this.random, 4);
        this.perlinNoise3 = new PerlinNoise(this.random, 4);
        this.scaleNoise = new PerlinNoise(this.random, 10);
        this.depthNoise = new PerlinNoise(this.random, 16);
        this.forestNoise = new PerlinNoise(this.random, 8);
    }
    
    public void prepareHeights(final int xOffs, final int zOffs, final byte[] blocks, final Biome[] biomes, final double[] temperatures) {
        final int n = 4;
        final int n2 = 64;
        final int xSize = n + 1;
        final int ySize = 17;
        final int zSize = n + 1;
        this.buffer = this.getHeights(this.buffer, xOffs * n, 0, zOffs * n, xSize, ySize, zSize);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < 16; ++k) {
                    final double n3 = 0.125;
                    double n4 = this.buffer[((i + 0) * zSize + (j + 0)) * ySize + (k + 0)];
                    double n5 = this.buffer[((i + 0) * zSize + (j + 1)) * ySize + (k + 0)];
                    double n6 = this.buffer[((i + 1) * zSize + (j + 0)) * ySize + (k + 0)];
                    double n7 = this.buffer[((i + 1) * zSize + (j + 1)) * ySize + (k + 0)];
                    final double n8 = (this.buffer[((i + 0) * zSize + (j + 0)) * ySize + (k + 1)] - n4) * n3;
                    final double n9 = (this.buffer[((i + 0) * zSize + (j + 1)) * ySize + (k + 1)] - n5) * n3;
                    final double n10 = (this.buffer[((i + 1) * zSize + (j + 0)) * ySize + (k + 1)] - n6) * n3;
                    final double n11 = (this.buffer[((i + 1) * zSize + (j + 1)) * ySize + (k + 1)] - n7) * n3;
                    for (int l = 0; l < 8; ++l) {
                        final double n12 = 0.25;
                        double n13 = n4;
                        double n14 = n5;
                        final double n15 = (n6 - n4) * n12;
                        final double n16 = (n7 - n5) * n12;
                        for (int n17 = 0; n17 < 4; ++n17) {
                            int n18 = n17 + i * 4 << 11 | 0 + j * 4 << 7 | k * 8 + l;
                            final int n19 = 128;
                            final double n20 = 0.25;
                            double n21 = n13;
                            final double n22 = (n14 - n13) * n20;
                            for (int n23 = 0; n23 < 4; ++n23) {
                                final double n24 = temperatures[(i * 4 + n17) * 16 + (j * 4 + n23)];
                                int n25 = 0;
                                if (k * 8 + l < n2) {
                                    if (n24 < 0.5 && k * 8 + l >= n2 - 1) {
                                        n25 = Tile.ice.id;
                                    }
                                    else {
                                        n25 = Tile.calmWater.id;
                                    }
                                }
                                if (n21 > 0.0) {
                                    n25 = Tile.rock.id;
                                }
                                blocks[n18] = (byte)n25;
                                n18 += n19;
                                n21 += n22;
                            }
                            n13 += n15;
                            n14 += n16;
                        }
                        n4 += n8;
                        n5 += n9;
                        n6 += n10;
                        n7 += n11;
                    }
                }
            }
        }
    }
    
    public void buildSurfaces(final int xOffs, final int zOffs, final byte[] blocks, final Biome[] biomes) {
        final int n = 64;
        final double n2 = 0.03125;
        this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, xOffs * 16, zOffs * 16, 0.0, 16, 16, 1, n2, n2, 1.0);
        this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, xOffs * 16, 109.0134, zOffs * 16, 16, 1, 16, n2, 1.0, n2);
        this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, xOffs * 16, zOffs * 16, 0.0, 16, 16, 1, n2 * 2.0, n2 * 2.0, n2 * 2.0);
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                final Biome biome = biomes[i + j * 16];
                final boolean b = this.sandBuffer[i + j * 16] + this.random.nextDouble() * 0.2 > 0.0;
                final boolean b2 = this.gravelBuffer[i + j * 16] + this.random.nextDouble() * 0.2 > 3.0;
                final int n3 = (int)(this.depthBuffer[i + j * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
                int nextInt = -1;
                byte b3 = biome.topMaterial;
                byte b4 = biome.material;
                for (int k = 127; k >= 0; --k) {
                    final int n4 = (j * 16 + i) * 128 + k;
                    if (k <= 0 + this.random.nextInt(5)) {
                        blocks[n4] = (byte)Tile.unbreakable.id;
                    }
                    else {
                        final byte b5 = blocks[n4];
                        if (b5 == 0) {
                            nextInt = -1;
                        }
                        else if (b5 == Tile.rock.id) {
                            if (nextInt == -1) {
                                if (n3 <= 0) {
                                    b3 = 0;
                                    b4 = (byte)Tile.rock.id;
                                }
                                else if (k >= n - 4 && k <= n + 1) {
                                    b3 = biome.topMaterial;
                                    b4 = biome.material;
                                    if (b2) {
                                        b3 = 0;
                                    }
                                    if (b2) {
                                        b4 = (byte)Tile.gravel.id;
                                    }
                                    if (b) {
                                        b3 = (byte)Tile.sand.id;
                                    }
                                    if (b) {
                                        b4 = (byte)Tile.sand.id;
                                    }
                                }
                                if (k < n && b3 == 0) {
                                    b3 = (byte)Tile.calmWater.id;
                                }
                                nextInt = n3;
                                if (k >= n - 1) {
                                    blocks[n4] = b3;
                                }
                                else {
                                    blocks[n4] = b4;
                                }
                            }
                            else if (nextInt > 0) {
                                --nextInt;
                                blocks[n4] = b4;
                                if (nextInt == 0 && b4 == Tile.sand.id) {
                                    nextInt = this.random.nextInt(4);
                                    b4 = (byte)Tile.sandStone.id;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public LevelChunk create(final int x, final int z) {
        return this.getChunk(x, z);
    }
    
    public LevelChunk getChunk(final int x, final int z) {
        this.random.setSeed(x * 341873128712L + z * 132897987541L);
        final byte[] array = new byte[32768];
        final LevelChunk levelChunk = new LevelChunk(this.level, array, x, z);
        this.prepareHeights(x, z, array, this.biomes = this.level.getBiomeSource().getBiomeBlock(this.biomes, x * 16, z * 16, 16, 16), this.level.getBiomeSource().temperatures);
        this.buildSurfaces(x, z, array, this.biomes);
        this.caveFeature.apply(this, this.level, x, z, array);
        levelChunk.recalcHeightmap();
        return levelChunk;
    }
    
    private double[] getHeights(double[] buffer, final int x, final int y, final int z, final int xSize, final int ySize, final int zSize) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        }
        final double n = 684.412;
        final double n2 = 684.412;
        final double[] temperatures = this.level.getBiomeSource().temperatures;
        final double[] downfalls = this.level.getBiomeSource().downfalls;
        this.sr = this.scaleNoise.getRegion(this.sr, x, z, xSize, zSize, 1.121, 1.121, 0.5);
        this.dr = this.depthNoise.getRegion(this.dr, x, z, xSize, zSize, 200.0, 200.0, 0.5);
        this.pnr = this.perlinNoise1.getRegion(this.pnr, x, y, z, xSize, ySize, zSize, n / 80.0, n2 / 160.0, n / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, x, y, z, xSize, ySize, zSize, n, n2, n);
        this.br = this.lperlinNoise2.getRegion(this.br, x, y, z, xSize, ySize, zSize, n, n2, n);
        int n3 = 0;
        int n4 = 0;
        final int n5 = 16 / xSize;
        for (int i = 0; i < xSize; ++i) {
            final int n6 = i * n5 + n5 / 2;
            for (int j = 0; j < zSize; ++j) {
                final int n7 = j * n5 + n5 / 2;
                final double n8 = 1.0 - downfalls[n6 * 16 + n7] * temperatures[n6 * 16 + n7];
                final double n9 = n8 * n8;
                double n10 = (this.sr[n4] + 256.0) / 512.0 * (1.0 - n9 * n9);
                if (n10 > 1.0) {
                    n10 = 1.0;
                }
                double n11 = this.dr[n4] / 8000.0;
                if (n11 < 0.0) {
                    n11 = -n11 * 0.3;
                }
                double n12 = n11 * 3.0 - 2.0;
                double n14;
                if (n12 < 0.0) {
                    double n13 = n12 / 2.0;
                    if (n13 < -1.0) {
                        n13 = -1.0;
                    }
                    n14 = n13 / 1.4 / 2.0;
                    n10 = 0.0;
                }
                else {
                    if (n12 > 1.0) {
                        n12 = 1.0;
                    }
                    n14 = n12 / 8.0;
                }
                if (n10 < 0.0) {
                    n10 = 0.0;
                }
                final double n15 = n10 + 0.5;
                final double n16 = ySize / 2.0 + n14 * ySize / 16.0 * 4.0;
                ++n4;
                for (int k = 0; k < ySize; ++k) {
                    double n17 = (k - n16) * 12.0 / n15;
                    if (n17 < 0.0) {
                        n17 *= 4.0;
                    }
                    final double n18 = this.ar[n3] / 512.0;
                    final double n19 = this.br[n3] / 512.0;
                    final double n20 = (this.pnr[n3] / 10.0 + 1.0) / 2.0;
                    double n21;
                    if (n20 < 0.0) {
                        n21 = n18;
                    }
                    else if (n20 > 1.0) {
                        n21 = n19;
                    }
                    else {
                        n21 = n18 + (n19 - n18) * n20;
                    }
                    double n22 = n21 - n17;
                    if (k > ySize - 4) {
                        final double n23 = (k - (ySize - 4)) / 3.0f;
                        n22 = n22 * (1.0 - n23) + -10.0 * n23;
                    }
                    buffer[n3] = n22;
                    ++n3;
                }
            }
        }
        return buffer;
    }
    
    public boolean hasChunk(final int x, final int z) {
        return true;
    }
    
    public void postProcess(final ChunkSource parent, final int x, final int z) {
        SandTile.instaFall = true;
        final int n = x * 16;
        final int n2 = z * 16;
        final Biome biome = this.level.getBiomeSource().getBiome(n + 16, n2 + 16);
        this.random.setSeed(this.level.getSeed());
        this.random.setSeed(x * (this.random.nextLong() / 2L * 2L + 1L) + z * (this.random.nextLong() / 2L * 2L + 1L) ^ this.level.getSeed());
        if (this.random.nextInt(4) == 0) {
            new LakeFeature(Tile.calmWater.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        if (this.random.nextInt(8) == 0) {
            final int x2 = n + this.random.nextInt(16) + 8;
            final int nextInt = this.random.nextInt(this.random.nextInt(120) + 8);
            final int z2 = n2 + this.random.nextInt(16) + 8;
            if (nextInt < 64 || this.random.nextInt(10) == 0) {
                new LakeFeature(Tile.calmLava.id).place(this.level, this.random, x2, nextInt, z2);
            }
        }
        for (int i = 0; i < 8; ++i) {
            new MonsterRoomFeature().place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        for (int j = 0; j < 10; ++j) {
            new ClayFeature(32).place(this.level, this.random, n + this.random.nextInt(16), this.random.nextInt(128), n2 + this.random.nextInt(16));
        }
        for (int k = 0; k < 20; ++k) {
            new OreFeature(Tile.dirt.id, 32).place(this.level, this.random, n + this.random.nextInt(16), this.random.nextInt(128), n2 + this.random.nextInt(16));
        }
        for (int l = 0; l < 10; ++l) {
            new OreFeature(Tile.gravel.id, 32).place(this.level, this.random, n + this.random.nextInt(16), this.random.nextInt(128), n2 + this.random.nextInt(16));
        }
        for (int n3 = 0; n3 < 20; ++n3) {
            new OreFeature(Tile.coalOre.id, 16).place(this.level, this.random, n + this.random.nextInt(16), this.random.nextInt(128), n2 + this.random.nextInt(16));
        }
        for (int n4 = 0; n4 < 20; ++n4) {
            new OreFeature(Tile.ironOre.id, 8).place(this.level, this.random, n + this.random.nextInt(16), this.random.nextInt(64), n2 + this.random.nextInt(16));
        }
        for (int n5 = 0; n5 < 2; ++n5) {
            new OreFeature(Tile.goldOre.id, 8).place(this.level, this.random, n + this.random.nextInt(16), this.random.nextInt(32), n2 + this.random.nextInt(16));
        }
        for (int n6 = 0; n6 < 8; ++n6) {
            new OreFeature(Tile.redStoneOre.id, 7).place(this.level, this.random, n + this.random.nextInt(16), this.random.nextInt(16), n2 + this.random.nextInt(16));
        }
        for (int n7 = 0; n7 < 1; ++n7) {
            new OreFeature(Tile.emeraldOre.id, 7).place(this.level, this.random, n + this.random.nextInt(16), this.random.nextInt(16), n2 + this.random.nextInt(16));
        }
        for (int n8 = 0; n8 < 1; ++n8) {
            new OreFeature(Tile.lapisOre.id, 6).place(this.level, this.random, n + this.random.nextInt(16), this.random.nextInt(16) + this.random.nextInt(16), n2 + this.random.nextInt(16));
        }
        final double n9 = 0.5;
        final int n10 = (int)((this.forestNoise.getValue(n * n9, n2 * n9) / 8.0 + this.random.nextDouble() * 4.0 + 4.0) / 3.0);
        int n11 = 0;
        if (this.random.nextInt(10) == 0) {
            ++n11;
        }
        if (biome == Biome.forest) {
            n11 += n10 + 5;
        }
        if (biome == Biome.rainForest) {
            n11 += n10 + 5;
        }
        if (biome == Biome.seasonalForest) {
            n11 += n10 + 2;
        }
        if (biome == Biome.taiga) {
            n11 += n10 + 5;
        }
        if (biome == Biome.desert) {
            n11 -= 20;
        }
        if (biome == Biome.tunfra) {
            n11 -= 20;
        }
        if (biome == Biome.plains) {
            n11 -= 20;
        }
        for (int n12 = 0; n12 < n11; ++n12) {
            final int n13 = n + this.random.nextInt(16) + 8;
            final int n14 = n2 + this.random.nextInt(16) + 8;
            final Feature treeFeature = biome.getTreeFeature(this.random);
            treeFeature.init(1.0, 1.0, 1.0);
            treeFeature.place(this.level, this.random, n13, this.level.getHeightmap(n13, n14), n14);
        }
        int n15 = 0;
        if (biome == Biome.forest) {
            n15 = 2;
        }
        if (biome == Biome.seasonalForest) {
            n15 = 4;
        }
        if (biome == Biome.taiga) {
            n15 = 2;
        }
        if (biome == Biome.plains) {
            n15 = 3;
        }
        for (int n16 = 0; n16 < n15; ++n16) {
            new FlowerFeature(Tile.flower.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        int n17 = 0;
        if (biome == Biome.forest) {
            n17 = 2;
        }
        if (biome == Biome.rainForest) {
            n17 = 10;
        }
        if (biome == Biome.seasonalForest) {
            n17 = 2;
        }
        if (biome == Biome.taiga) {
            n17 = 1;
        }
        if (biome == Biome.plains) {
            n17 = 10;
        }
        for (int n18 = 0; n18 < n17; ++n18) {
            int type = 1;
            if (biome == Biome.rainForest && this.random.nextInt(3) != 0) {
                type = 2;
            }
            new TallGrassFeature(Tile.tallgrass.id, type).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        int n19 = 0;
        if (biome == Biome.desert) {
            n19 = 2;
        }
        for (int n20 = 0; n20 < n19; ++n20) {
            new DeadBushFeature(Tile.deadBush.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        if (this.random.nextInt(2) == 0) {
            new FlowerFeature(Tile.rose.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        if (this.random.nextInt(4) == 0) {
            new FlowerFeature(Tile.mushroom1.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        if (this.random.nextInt(8) == 0) {
            new FlowerFeature(Tile.mushroom2.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        for (int n21 = 0; n21 < 10; ++n21) {
            new ReedsFeature().place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        if (this.random.nextInt(32) == 0) {
            new PumpkinFeature().place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        int n22 = 0;
        if (biome == Biome.desert) {
            n22 += 10;
        }
        for (int n23 = 0; n23 < n22; ++n23) {
            new CactusFeature().place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        for (int n24 = 0; n24 < 50; ++n24) {
            new SpringFeature(Tile.water.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(120) + 8), n2 + this.random.nextInt(16) + 8);
        }
        for (int n25 = 0; n25 < 20; ++n25) {
            new SpringFeature(Tile.lava.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(this.random.nextInt(112) + 8) + 8), n2 + this.random.nextInt(16) + 8);
        }
        this.temperatures = this.level.getBiomeSource().getTemperatureBlock(this.temperatures, n + 8, n2 + 8, 16, 16);
        for (int x3 = n + 8; x3 < n + 8 + 16; ++x3) {
            for (int z3 = n2 + 8; z3 < n2 + 8 + 16; ++z3) {
                final int n26 = x3 - (n + 8);
                final int n27 = z3 - (n2 + 8);
                final int topSolidBlock = this.level.getTopSolidBlock(x3, z3);
                if (this.temperatures[n26 * 16 + n27] - (topSolidBlock - 64) / 64.0 * 0.3 < 0.5 && topSolidBlock > 0 && topSolidBlock < 128 && this.level.isEmptyTile(x3, topSolidBlock, z3) && this.level.getMaterial(x3, topSolidBlock - 1, z3).blocksMotion() && this.level.getMaterial(x3, topSolidBlock - 1, z3) != Material.ice) {
                    this.level.setTile(x3, topSolidBlock, z3, Tile.topSnow.id);
                }
            }
        }
        SandTile.instaFall = false;
    }
    
    public boolean save(final boolean force, final ProgressListener progressListener) {
        return true;
    }
    
    public boolean tick() {
        return false;
    }
    
    public boolean shouldSave() {
        return true;
    }
    
    public String gatherStats() {
        return "RandomLevelSource";
    }
}
