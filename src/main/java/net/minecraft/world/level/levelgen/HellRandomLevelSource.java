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
    public static final int CHUNK_HEIGHT = 8;
    public static final int CHUNK_WIDTH = 4;
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
    private double[] sandBuffer = new double[256];
    private double[] gravelBuffer = new double[256];
    private double[] depthBuffer = new double[256];
    private LargeFeature caveFeature = new LargeHellCaveFeature();
    double[] pnr;
    double[] ar;
    double[] br;
    double[] sr;
    double[] dr;
    
    public HellRandomLevelSource(final Level level, final long seed) {
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
        final int xChunks = 16 / CHUNK_WIDTH;
        final int waterHeight = 32;

        final int xSize = xChunks + 1;
        final int ySize = Level.MAX_HEIGHT / CHUNK_HEIGHT + 1;
        final int zSize = xChunks + 1;
        this.buffer = this.getHeights(this.buffer, xOffs * xChunks, 0, zOffs * xChunks, xSize, ySize, zSize);
        for (int xc = 0; xc < xChunks; ++xc) {
            for (int zc = 0; zc < xChunks; ++zc) {
                for (int yc = 0; yc < Level.MAX_HEIGHT / CHUNK_HEIGHT; ++yc) {
                    final double yStep = 1 / (double) CHUNK_HEIGHT;
                    double s0 = this.buffer[((xc + 0) * zSize + (zc + 0)) * ySize + (yc + 0)];
                    double s1 = this.buffer[((xc + 0) * zSize + (zc + 1)) * ySize + (yc + 0)];
                    double s2 = this.buffer[((xc + 1) * zSize + (zc + 0)) * ySize + (yc + 0)];
                    double s3 = this.buffer[((xc + 1) * zSize + (zc + 1)) * ySize + (yc + 0)];

                    final double s0a = (this.buffer[((xc + 0) * zSize + (zc + 0)) * ySize + (yc + 1)] - s0) * yStep;
                    final double s1a = (this.buffer[((xc + 0) * zSize + (zc + 1)) * ySize + (yc + 1)] - s1) * yStep;
                    final double s2a = (this.buffer[((xc + 1) * zSize + (zc + 0)) * ySize + (yc + 1)] - s2) * yStep;
                    final double s3a = (this.buffer[((xc + 1) * zSize + (zc + 1)) * ySize + (yc + 1)] - s3) * yStep;

                    for (int y = 0; y < CHUNK_HEIGHT; ++y) {
                        final double xStep = 1 / (double) CHUNK_WIDTH;
                        double _s0 = s0;
                        double _s1 = s1;
                        final double _s0a = (s2 - s0) * xStep;
                        final double _s1a = (s3 - s1) * xStep;

                        for (int x = 0; x < CHUNK_WIDTH; ++x) {
                            int offs = x + xc * CHUNK_WIDTH << 11 | 0 + zc * CHUNK_WIDTH << 7 | yc * CHUNK_HEIGHT + y;
                            final int step = Level.MAX_HEIGHT;
                            final double zStep = 1 / (double) CHUNK_WIDTH;

                            double val = _s0;
                            final double vala = (_s1 - _s0) * zStep;
                            for (int z = 0; z < CHUNK_WIDTH; ++z) {
                                int tileId = 0;
                                if (yc * CHUNK_HEIGHT + y < waterHeight) {
                                    tileId = Tile.calmLava.id;
                                }
                                if (val > 0.0) {
                                    tileId = Tile.hellRock.id;
                                }

                                blocks[offs] = (byte)tileId;
                                offs += step;
                                val += vala;
                            }
                            _s0 += _s0a;
                            _s1 += _s1a;
                        }

                        s0 += s0a;
                        s1 += s1a;
                        s2 += s2a;
                        s3 += s3a;
                    }
                }
            }
        }
    }
    
    public void buildSurfaces(final int xOffs, final int zOffs, final byte[] blocks) {
        final int waterHeight = Level.MAX_HEIGHT - 64;

        final double s = 1 / 32.0;

        this.sandBuffer = this.perlinNoise2.getRegion(this.sandBuffer, xOffs * 16, zOffs * 16, 0.0, 16, 16, 1, s, s, 1.0);
        this.gravelBuffer = this.perlinNoise2.getRegion(this.gravelBuffer, xOffs * 16, 109.0134, zOffs * 16, 16, 1, 16, s, 1.0, s);
        this.depthBuffer = this.perlinNoise3.getRegion(this.depthBuffer, xOffs * 16, zOffs * 16, 0.0, 16, 16, 1, s * 2.0, s * 2.0, s * 2.0);

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                final boolean sand = this.sandBuffer[x + z * 16] + this.random.nextDouble() * 0.2 > 0.0;
                final boolean gravel = this.gravelBuffer[x + z * 16] + this.random.nextDouble() * 0.2 > 0.0;
                final int runDepth = (int)(this.depthBuffer[x + z * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);

                int run = -1;

                byte top = (byte)Tile.hellRock.id;
                byte material = (byte)Tile.hellRock.id;

                for (int y = Level.MAX_HEIGHT - 1; y >= 0; --y) {
                    final int offs = (z * 16 + x) * Level.MAX_HEIGHT + y;

                    if (y >= (Level.MAX_HEIGHT - 1) - this.random.nextInt(5)) {
                        blocks[offs] = (byte)Tile.unbreakable.id;
                    }
                    else if (y <= 0 + this.random.nextInt(5)) {
                        blocks[offs] = (byte)Tile.unbreakable.id;
                    }
                    else {
                        final byte old = blocks[offs];
                        if (old == 0) {
                            run = -1;
                        }
                        else if (old == Tile.hellRock.id) {
                            if (run == -1) {
                                if (runDepth <= 0) {
                                    top = 0;
                                    material = (byte)Tile.hellRock.id;
                                }
                                else if (y >= waterHeight - 4 && y <= waterHeight + 1) {
                                    top = (byte)Tile.hellRock.id;
                                    material = (byte)Tile.hellRock.id;
                                    if (gravel) top = (byte) Tile.gravel.id;
                                    if (gravel) material = (byte) Tile.hellRock.id;
                                    if (sand) top = (byte) Tile.hellSand.id;
                                    if (sand) material = (byte) Tile.hellSand.id;
                                }

                                if (y < waterHeight && top == 0) top = (byte) Tile.calmLava.id;

                                run = runDepth;
                                if (y >= waterHeight - 1) blocks[offs] = top;
                                else blocks[offs] = material;
                            }
                            else if (run > 0) {
                                run--;
                                blocks[offs] = material;
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

        final byte[] blocks = new byte[Level.MAX_HEIGHT * 16 * 16];

        this.prepareHeights(x, z, blocks);
        this.buildSurfaces(x, z, blocks);

        this.caveFeature.apply(this, this.level, x, z, blocks);
        return new LevelChunk(this.level, blocks, x, z);
    }
    
    private double[] getHeights(double[] buffer, final int x, final int y, final int z, final int xSize, final int ySize, final int zSize) {
        if (buffer == null) buffer = new double[xSize * ySize * zSize];

        final double s = 1 * 684.412;
        final double hs = 1 * 684.412 * 3;

        this.sr = this.scaleNoise.getRegion(this.sr, x, y, z, xSize, 1, zSize, 1.0, 0.0, 1.0);
        this.dr = this.depthNoise.getRegion(this.dr, x, y, z, xSize, 1, zSize, 100.0, 0.0, 100.0);

        this.pnr = this.perlinNoise1.getRegion(this.pnr, x, y, z, xSize, ySize, zSize, s / 80.0, hs / 60.0, s / 80.0);
        this.ar = this.lperlinNoise1.getRegion(this.ar, x, y, z, xSize, ySize, zSize, s, hs, s);
        this.br = this.lperlinNoise2.getRegion(this.br, x, y, z, xSize, ySize, zSize, s, hs, s);

        int p = 0;
        int pp = 0;
        final double[] yoffs = new double[ySize];
        for (int yy = 0; yy < ySize; ++yy) {
            yoffs[yy] = Math.cos(yy * Math.PI * 6.0 / ySize) * 2.0;

            double dd = yy;
            if (yy > ySize / 2) {
                dd = ySize - 1 - yy;
            }
            if (dd < 4.0) {
                dd = 4.0 - dd;
                yoffs[yy] -= dd * dd * dd * 10.0;
            }
        }

        for (int xx = 0; xx < xSize; ++xx) {
            for (int zz = 0; zz < zSize; ++zz) {
                double scale = (this.sr[pp] + 256.0) / 512.0;
                if (scale > 1.0) scale = 1.0;

                final double floating = 0.0;

                double depth = this.dr[pp] / 8000.0;
                if (depth < 0.0) depth = -depth;
                depth = depth * 3.0 - 3.0;

                if (depth < 0.0) {
                    depth /= 2.0;
                    if (depth < -1.0) depth = -1.0;
                    depth /= 1.4;
                    depth /= 2.0;
                    scale = 0.0;
                }
                else {
                    if (depth > 1.0) depth = 1.0;
                    depth = depth / 6.0;
                }
                scale = scale + 0.5;
                depth = depth * ySize / 16.0;
                pp++;

                for (int yy = 0; yy < ySize; ++yy) {
                    double val = 0;

                    final double yOffs = yoffs[yy];

                    final double bb = this.ar[p] / 512.0;
                    final double cc = this.br[p] / 512.0;

                    final double v = (this.pnr[p] / 10.0 + 1.0) / 2.0;
                    if (v < 0.0) val = bb;
                    else if (v > 1.0) val = cc;
                    else val = bb + (cc - bb) * v;
                    val -= yOffs;

                    if (yy > ySize - 4) {
                        final double slide = (yy - (ySize - 4)) / 3.0f;
                        val = val * (1.0 - slide) + -10.0 * slide;
                    }

                    if (yy < floating) {
                        double slide = (floating - yy) / 4.0;
                        if (slide < 0.0) slide = 0.0;
                        if (slide > 1.0) slide = 1.0;
                        val = val * (1.0 - slide) + -10.0 * slide;
                    }

                    buffer[p] = val;
                    p++;
                }
            }
        }

        return buffer;
    }
    
    public boolean hasChunk(final int x, final int z) {
        return true;
    }
    
    public void postProcess(final ChunkSource parent, final int xt, final int zt) {
        SandTile.instaFall = true;
        final int xo = xt * 16;
        final int zo = zt * 16;

        for (int i = 0; i < 8; ++i) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT - 8) + 4;
            int z = zo + this.random.nextInt(16) + 8;
            new HellSpringFeature(Tile.lava.id).place(this.level, this.random, x, y, z);
        }

        int count = this.random.nextInt(this.random.nextInt(10) + 1) + 1;

        for (int i = 0; i < count; ++i) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT - 8) + 4;
            int z = zo + this.random.nextInt(16) + 8;
            new HellFireFeature().place(this.level, this.random, x, y, z);
        }

        count = this.random.nextInt(this.random.nextInt(10) + 1);
        for (int i = 0; i < count; ++i) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT - 8) + 4;
            int z = zo + this.random.nextInt(16) + 8;
            new HellPortalFeature().place(this.level, this.random, x, y, z);
        }

        for (int i = 0; i < 10; ++i) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            new LightGemFeature().place(this.level, this.random, x, y, z);
        }

        if (this.random.nextInt(1) == 0) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            new FlowerFeature(Tile.mushroom1.id).place(this.level, this.random, x, y, z);
        }
        if (this.random.nextInt(1) == 0) {
            int x = xo + this.random.nextInt(16) + 8;
            int y = this.random.nextInt(Level.MAX_HEIGHT);
            int z = zo + this.random.nextInt(16) + 8;
            new FlowerFeature(Tile.mushroom2.id).place(this.level, this.random, x, y, z);
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
        return "HellRandomLevelSource";
    }
}
