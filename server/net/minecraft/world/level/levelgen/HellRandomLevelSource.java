// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.level.levelgen;

import util.ProgressListener;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;
import net.minecraft.world.level.levelgen.feature.LightGemFeature;
import net.minecraft.world.level.levelgen.feature.HellPortalFeature;
import net.minecraft.world.level.levelgen.feature.HellFireFeature;
import net.minecraft.world.level.levelgen.feature.HellSpringFeature;
import net.minecraft.world.level.tile.SandTile;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import java.util.Random;
import net.minecraft.world.level.chunk.ChunkSource;

public class HellRandomLevelSource implements ChunkSource
{
    private Random random;
    private PerlinNoise lperlinNoise1;
    private PerlinNoise lperlinNoise2;
    private PerlinNoise perlinNoise1;
    private PerlinNoise perlinNoise2;
    private PerlinNoise perlinNoise3;
    public PerlinNoise scaleNoise;
    public PerlinNoise depthNoise;
    private Level level;
    private double[] buffer;
    private double[] sandBuffer;
    private double[] gravelBuffer;
    private double[] depthBuffer;
    private LargeFeature caveFeature;
    double[] pnr;
    double[] ar;
    double[] br;
    double[] sr;
    double[] dr;
    
    public HellRandomLevelSource(final Level level, final long seed) {
        this.sandBuffer = new double[256];
        this.gravelBuffer = new double[256];
        this.depthBuffer = new double[256];
        this.caveFeature = new LargeHellCaveFeature();
        this.level = level;
        this.random = new Random(seed);
        this.lperlinNoise1 = new PerlinNoise(this.random, 16);
        this.lperlinNoise2 = new PerlinNoise(this.random, 16);
        this.perlinNoise1 = new PerlinNoise(this.random, 8);
        this.perlinNoise2 = new PerlinNoise(this.random, 4);
        this.perlinNoise3 = new PerlinNoise(this.random, 4);
        this.scaleNoise = new PerlinNoise(this.random, 10);
        this.depthNoise = new PerlinNoise(this.random, 16);
    }
    
    public void prepareHeights(final int xOffs, final int zOffs, final byte[] blocks) {
        final int n = 4;
        final int n2 = 32;
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
                                int n24 = 0;
                                if (k * 8 + l < n2) {
                                    n24 = Tile.calmLava.id;
                                }
                                if (n21 > 0.0) {
                                    n24 = Tile.hellRock.id;
                                }
                                blocks[n18] = (byte)n24;
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
    
    public void buildSurfaces(final int xOffs, final int zOffs, final byte[] blocks) {
        final int n = 64;
        final double n2 = 0.03125;
        this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, xOffs * 16, zOffs * 16, 0.0, 16, 16, 1, n2, n2, 1.0);
        this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, xOffs * 16, 109.0134, zOffs * 16, 16, 1, 16, n2, 1.0, n2);
        this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, xOffs * 16, zOffs * 16, 0.0, 16, 16, 1, n2 * 2.0, n2 * 2.0, n2 * 2.0);
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                final boolean b = this.sandBuffer[i + j * 16] + this.random.nextDouble() * 0.2 > 0.0;
                final boolean b2 = this.gravelBuffer[i + j * 16] + this.random.nextDouble() * 0.2 > 0.0;
                final int n3 = (int)(this.depthBuffer[i + j * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
                int n4 = -1;
                byte b3 = (byte)Tile.hellRock.id;
                byte b4 = (byte)Tile.hellRock.id;
                for (int k = 127; k >= 0; --k) {
                    final int n5 = (j * 16 + i) * 128 + k;
                    if (k >= 127 - this.random.nextInt(5)) {
                        blocks[n5] = (byte)Tile.unbreakable.id;
                    }
                    else if (k <= 0 + this.random.nextInt(5)) {
                        blocks[n5] = (byte)Tile.unbreakable.id;
                    }
                    else {
                        final byte b5 = blocks[n5];
                        if (b5 == 0) {
                            n4 = -1;
                        }
                        else if (b5 == Tile.hellRock.id) {
                            if (n4 == -1) {
                                if (n3 <= 0) {
                                    b3 = 0;
                                    b4 = (byte)Tile.hellRock.id;
                                }
                                else if (k >= n - 4 && k <= n + 1) {
                                    b3 = (byte)Tile.hellRock.id;
                                    b4 = (byte)Tile.hellRock.id;
                                    if (b2) {
                                        b3 = (byte)Tile.gravel.id;
                                    }
                                    if (b2) {
                                        b4 = (byte)Tile.hellRock.id;
                                    }
                                    if (b) {
                                        b3 = (byte)Tile.hellSand.id;
                                    }
                                    if (b) {
                                        b4 = (byte)Tile.hellSand.id;
                                    }
                                }
                                if (k < n && b3 == 0) {
                                    b3 = (byte)Tile.calmLava.id;
                                }
                                n4 = n3;
                                if (k >= n - 1) {
                                    blocks[n5] = b3;
                                }
                                else {
                                    blocks[n5] = b4;
                                }
                            }
                            else if (n4 > 0) {
                                --n4;
                                blocks[n5] = b4;
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
        this.prepareHeights(x, z, array);
        this.buildSurfaces(x, z, array);
        this.caveFeature.apply(this, this.level, x, z, array);
        return new LevelChunk(this.level, array, x, z);
    }
    
    private double[] getHeights(double[] buffer, final int x, final int y, final int z, final int xSize, final int ySize, final int zSize) {
        if (buffer == null) {
            buffer = new double[xSize * ySize * zSize];
        }
        final double n = 684.412;
        final double n2 = 2053.236;
        this.sr = this.scaleNoise.getRegion(this.sr, x, y, z, xSize, 1, zSize, 1.0, 0.0, 1.0);
        this.dr = this.depthNoise.getRegion(this.dr, x, y, z, xSize, 1, zSize, 100.0, 0.0, 100.0);
        this.pnr = this.perlinNoise1.getRegion(this.pnr, x, y, z, xSize, ySize, zSize, n / 80.0, n2 / 60.0, n / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, x, y, z, xSize, ySize, zSize, n, n2, n);
        this.br = this.lperlinNoise2.getRegion(this.br, x, y, z, xSize, ySize, zSize, n, n2, n);
        int n3 = 0;
        int n4 = 0;
        final double[] array = new double[ySize];
        for (int i = 0; i < ySize; ++i) {
            array[i] = Math.cos(i * 3.141592653589793 * 6.0 / ySize) * 2.0;
            double n5 = i;
            if (i > ySize / 2) {
                n5 = ySize - 1 - i;
            }
            if (n5 < 4.0) {
                final double n6 = 4.0 - n5;
                final double[] array2 = array;
                final int n7 = i;
                array2[n7] -= n6 * n6 * n6 * 10.0;
            }
        }
        for (int j = 0; j < xSize; ++j) {
            for (int k = 0; k < zSize; ++k) {
                double n8 = (this.sr[n4] + 256.0) / 512.0;
                if (n8 > 1.0) {
                    n8 = 1.0;
                }
                final double n9 = 0.0;
                double n10 = this.dr[n4] / 8000.0;
                if (n10 < 0.0) {
                    n10 = -n10;
                }
                double n11 = n10 * 3.0 - 3.0;
                double n13;
                if (n11 < 0.0) {
                    double n12 = n11 / 2.0;
                    if (n12 < -1.0) {
                        n12 = -1.0;
                    }
                    n13 = n12 / 1.4 / 2.0;
                    n8 = 0.0;
                }
                else {
                    if (n11 > 1.0) {
                        n11 = 1.0;
                    }
                    n13 = n11 / 6.0;
                }
                final double n14 = n13 * ySize / 16.0;
                ++n4;
                for (int l = 0; l < ySize; ++l) {
                    final double n15 = array[l];
                    final double n16 = this.ar[n3] / 512.0;
                    final double n17 = this.br[n3] / 512.0;
                    final double n18 = (this.pnr[n3] / 10.0 + 1.0) / 2.0;
                    double n19;
                    if (n18 < 0.0) {
                        n19 = n16;
                    }
                    else if (n18 > 1.0) {
                        n19 = n17;
                    }
                    else {
                        n19 = n16 + (n17 - n16) * n18;
                    }
                    double n20 = n19 - n15;
                    if (l > ySize - 4) {
                        final double n21 = (l - (ySize - 4)) / 3.0f;
                        n20 = n20 * (1.0 - n21) + -10.0 * n21;
                    }
                    if (l < n9) {
                        double n22 = (n9 - l) / 4.0;
                        if (n22 < 0.0) {
                            n22 = 0.0;
                        }
                        if (n22 > 1.0) {
                            n22 = 1.0;
                        }
                        n20 = n20 * (1.0 - n22) + -10.0 * n22;
                    }
                    buffer[n3] = n20;
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
        for (int i = 0; i < 8; ++i) {
            new HellSpringFeature(Tile.lava.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(120) + 4, n2 + this.random.nextInt(16) + 8);
        }
        for (int n3 = this.random.nextInt(this.random.nextInt(10) + 1) + 1, j = 0; j < n3; ++j) {
            new HellFireFeature().place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(120) + 4, n2 + this.random.nextInt(16) + 8);
        }
        for (int nextInt = this.random.nextInt(this.random.nextInt(10) + 1), k = 0; k < nextInt; ++k) {
            new HellPortalFeature().place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(120) + 4, n2 + this.random.nextInt(16) + 8);
        }
        for (int l = 0; l < 10; ++l) {
            new LightGemFeature().place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        if (this.random.nextInt(1) == 0) {
            new FlowerFeature(Tile.mushroom1.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
        }
        if (this.random.nextInt(1) == 0) {
            new FlowerFeature(Tile.mushroom2.id).place(this.level, this.random, n + this.random.nextInt(16) + 8, this.random.nextInt(128), n2 + this.random.nextInt(16) + 8);
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
}
