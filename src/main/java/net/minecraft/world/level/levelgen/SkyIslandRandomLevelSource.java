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

public class SkyIslandRandomLevelSource implements ChunkSource
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
    
    public SkyIslandRandomLevelSource(final Level level, final long seed) {
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
        final int n = 2;
        final int xSize = n + 1;
        final int ySize = 33;
        final int zSize = n + 1;
        this.buffer = this.getHeights(this.buffer, xOffs * n, 0, zOffs * n, xSize, ySize, zSize);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < 32; ++k) {
                    final double n2 = 0.25;
                    double n3 = this.buffer[((i + 0) * zSize + (j + 0)) * ySize + (k + 0)];
                    double n4 = this.buffer[((i + 0) * zSize + (j + 1)) * ySize + (k + 0)];
                    double n5 = this.buffer[((i + 1) * zSize + (j + 0)) * ySize + (k + 0)];
                    double n6 = this.buffer[((i + 1) * zSize + (j + 1)) * ySize + (k + 0)];
                    final double n7 = (this.buffer[((i + 0) * zSize + (j + 0)) * ySize + (k + 1)] - n3) * n2;
                    final double n8 = (this.buffer[((i + 0) * zSize + (j + 1)) * ySize + (k + 1)] - n4) * n2;
                    final double n9 = (this.buffer[((i + 1) * zSize + (j + 0)) * ySize + (k + 1)] - n5) * n2;
                    final double n10 = (this.buffer[((i + 1) * zSize + (j + 1)) * ySize + (k + 1)] - n6) * n2;
                    for (int l = 0; l < 4; ++l) {
                        final double n11 = 0.125;
                        double n12 = n3;
                        double n13 = n4;
                        final double n14 = (n5 - n3) * n11;
                        final double n15 = (n6 - n4) * n11;
                        for (int n16 = 0; n16 < 8; ++n16) {
                            int n17 = n16 + i * 8 << 11 | 0 + j * 8 << 7 | k * 4 + l;
                            final int n18 = 128;
                            final double n19 = 0.125;
                            double n20 = n12;
                            final double n21 = (n13 - n12) * n19;
                            for (int n22 = 0; n22 < 8; ++n22) {
                                int id = 0;
                                if (n20 > 0.0) {
                                    id = Tile.rock.id;
                                }
                                blocks[n17] = (byte)id;
                                n17 += n18;
                                n20 += n21;
                            }
                            n12 += n14;
                            n13 += n15;
                        }
                        n3 += n7;
                        n4 += n8;
                        n5 += n9;
                        n6 += n10;
                    }
                }
            }
        }
    }
    
    public void buildSurfaces(final int xOffs, final int zOffs, final byte[] blocks, final Biome[] biomes) {
        final double n = 0.03125;
        this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, xOffs * 16, zOffs * 16, 0.0, 16, 16, 1, n, n, 1.0);
        this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, xOffs * 16, 109.0134, zOffs * 16, 16, 1, 16, n, 1.0, n);
        this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, xOffs * 16, zOffs * 16, 0.0, 16, 16, 1, n * 2.0, n * 2.0, n * 2.0);
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                final Biome biome = biomes[i + j * 16];
                final int n2 = (int)(this.depthBuffer[i + j * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
                int nextInt = -1;
                byte topMaterial = biome.topMaterial;
                byte material = biome.material;
                for (int k = 127; k >= 0; --k) {
                    final int n3 = (j * 16 + i) * 128 + k;
                    final byte b = blocks[n3];
                    if (b == 0) {
                        nextInt = -1;
                    }
                    else if (b == Tile.rock.id) {
                        if (nextInt == -1) {
                            if (n2 <= 0) {
                                topMaterial = 0;
                                material = (byte)Tile.rock.id;
                            }
                            nextInt = n2;
                            if (k >= 0) {
                                blocks[n3] = topMaterial;
                            }
                            else {
                                blocks[n3] = material;
                            }
                        }
                        else if (nextInt > 0) {
                            --nextInt;
                            blocks[n3] = material;
                            if (nextInt == 0 && material == Tile.sand.id) {
                                nextInt = this.random.nextInt(4);
                                material = (byte)Tile.sandStone.id;
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
        final double n3 = n * 2.0;
        this.pnr = this.perlinNoise1.getRegion(this.pnr, x, y, z, xSize, ySize, zSize, n3 / 80.0, n2 / 160.0, n3 / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, x, y, z, xSize, ySize, zSize, n3, n2, n3);
        this.br = this.lperlinNoise2.getRegion(this.br, x, y, z, xSize, ySize, zSize, n3, n2, n3);
        int n4 = 0;
        int n5 = 0;
        final int n6 = 16 / xSize;
        for (int i = 0; i < xSize; ++i) {
            final int n7 = i * n6 + n6 / 2;
            for (int j = 0; j < zSize; ++j) {
                final int n8 = j * n6 + n6 / 2;
                final double n9 = 1.0 - downfalls[n7 * 16 + n8] * temperatures[n7 * 16 + n8];
                final double n10 = n9 * n9;
                double n11 = (this.sr[n5] + 256.0) / 512.0 * (1.0 - n10 * n10);
                if (n11 > 1.0) {
                    n11 = 1.0;
                }
                double n12 = this.dr[n5] / 8000.0;
                if (n12 < 0.0) {
                    n12 = -n12 * 0.3;
                }
                double n13 = n12 * 3.0 - 2.0;
                if (n13 > 1.0) {
                    n13 = 1.0;
                }
                final double n14 = 0.0;
                if (n11 < 0.0) {
                    n11 = 0.0;
                }
                final double n15 = n11 + 0.5;
                final double n16 = n14 * ySize / 16.0;
                ++n5;
                final double n17 = ySize / 2.0;
                for (int k = 0; k < ySize; ++k) {
                    if ((k - n17) * 8.0 / n15 < 0.0) {}
                    final double n18 = this.ar[n4] / 512.0;
                    final double n19 = this.br[n4] / 512.0;
                    final double n20 = (this.pnr[n4] / 10.0 + 1.0) / 2.0;
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
                    double n22 = n21 - 8.0;
                    final int n23 = 32;
                    if (k > ySize - n23) {
                        final double n24 = (k - (ySize - n23)) / (n23 - 1.0f);
                        n22 = n22 * (1.0 - n24) + -30.0 * n24;
                    }
                    final int n25 = 8;
                    if (k < n25) {
                        final double n26 = (n25 - k) / (n25 - 1.0f);
                        n22 = n22 * (1.0 - n26) + -30.0 * n26;
                    }
                    buffer[n4] = n22;
                    ++n4;
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
        for (int n15 = 0; n15 < 2; ++n15) {
            new FlowerFeature(Tile.flower.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
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
        for (int n16 = 0; n16 < 10; ++n16) {
            new ReedsFeature().place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        if (this.random.nextInt(32) == 0) {
            new PumpkinFeature().place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        int n17 = 0;
        if (biome == Biome.desert) {
            n17 += 10;
        }
        for (int n18 = 0; n18 < n17; ++n18) {
            new CactusFeature().place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        for (int n19 = 0; n19 < 50; ++n19) {
            new SpringFeature(Tile.water.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(120) + 8), n2 + this.random.nextInt(16) + 8);
        }
        for (int n20 = 0; n20 < 20; ++n20) {
            new SpringFeature(Tile.lava.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(this.random.nextInt(112) + 8) + 8), n2 + this.random.nextInt(16) + 8);
        }
        this.temperatures = this.level.getBiomeSource().getTemperatureBlock(this.temperatures, n + 8, n2 + 8, 16, 16);
        for (int x3 = n + 8; x3 < n + 8 + 16; ++x3) {
            for (int z3 = n2 + 8; z3 < n2 + 8 + 16; ++z3) {
                final int n21 = x3 - (n + 8);
                final int n22 = z3 - (n2 + 8);
                final int topSolidBlock = this.level.getTopSolidBlock(x3, z3);
                if (this.temperatures[n21 * 16 + n22] - (topSolidBlock - 64) / 64.0 * 0.3 < 0.5 && topSolidBlock > 0 && topSolidBlock < 128 && this.level.isEmptyTile(x3, topSolidBlock, z3) && this.level.getMaterial(x3, topSolidBlock - 1, z3).blocksMotion() && this.level.getMaterial(x3, topSolidBlock - 1, z3) != Material.ice) {
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
